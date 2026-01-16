"""
Base Crawler - 基底クローラークラス
"""
import asyncio
import logging
import re
from abc import ABC, abstractmethod
from typing import List, Optional
from datetime import datetime

import httpx
from bs4 import BeautifulSoup

from models import JobData, CrawlResult, RemoteType
import config

logger = logging.getLogger(__name__)


class BaseCrawler(ABC):
    """基底クローラークラス"""
    
    def __init__(self):
        self.source_name = ""
        self.base_url = ""
        self.list_url = ""
        self.client = httpx.AsyncClient(
            timeout=config.TIMEOUT,
            headers={"User-Agent": config.USER_AGENT},
            follow_redirects=True
        )
    
    async def crawl(self) -> CrawlResult:
        """クロール実行"""
        start_time = datetime.now()
        jobs: List[JobData] = []
        error_message = None
        
        try:
            logger.info(f"Starting crawl: {self.source_name}")
            jobs = await self._crawl_all_pages()
            logger.info(f"Crawled {len(jobs)} jobs from {self.source_name}")
        except Exception as e:
            error_message = str(e)
            logger.error(f"Crawl failed for {self.source_name}: {e}")
        
        return CrawlResult(
            source=self.source_name,
            success=error_message is None,
            jobs_count=len(jobs),
            error_message=error_message,
            crawled_at=start_time
        )
    
    async def get_jobs(self) -> List[JobData]:
        """案件リストを取得"""
        return await self._crawl_all_pages()
    
    @abstractmethod
    async def _crawl_all_pages(self) -> List[JobData]:
        """全ページをクロール（サブクラスで実装）"""
        pass
    
    @abstractmethod
    async def _parse_job_list(self, html: str) -> List[JobData]:
        """案件リストをパース（サブクラスで実装）"""
        pass
    
    async def _fetch_page(self, url: str) -> Optional[str]:
        """ページ取得"""
        try:
            await asyncio.sleep(config.REQUEST_DELAY)
            response = await self.client.get(url)
            response.raise_for_status()
            return response.text
        except httpx.HTTPError as e:
            logger.error(f"Failed to fetch {url}: {e}")
            return None
    
    async def close(self):
        """クライアントを閉じる"""
        await self.client.aclose()
    
    # Utility methods
    
    @staticmethod
    def parse_price(price_text: str) -> tuple[Optional[int], Optional[int]]:
        """単価テキストから最小・最大単価を抽出"""
        if not price_text:
            return None, None
        
        # "50万円〜80万円" のようなパターン
        match = re.search(r'(\d+)\s*[万〜~\-]+\s*(\d+)', price_text)
        if match:
            return int(match.group(1)), int(match.group(2))
        
        # "80万円" のような単一値
        match = re.search(r'(\d+)\s*万', price_text)
        if match:
            price = int(match.group(1))
            return price, price
        
        return None, None
    
    @staticmethod
    def detect_remote_type(text: str) -> Optional[RemoteType]:
        """テキストからリモートタイプを検出"""
        if not text:
            return None
        
        text_lower = text.lower()
        
        if any(kw in text_lower for kw in ['フルリモート', 'full remote', '完全リモート']):
            return RemoteType.FULL
        elif any(kw in text_lower for kw in ['一部リモート', '週数日出社', 'ハイブリッド']):
            return RemoteType.PARTIAL
        elif any(kw in text_lower for kw in ['出社必須', 'オンサイト', '常駐']):
            return RemoteType.NONE
        elif 'リモート' in text_lower or 'remote' in text_lower:
            return RemoteType.PARTIAL
        
        return None
    
    @staticmethod
    def extract_skills(text: str) -> List[str]:
        """テキストからスキルを抽出"""
        if not text:
            return []
        
        # 一般的なスキルキーワード
        skill_keywords = [
            'Java', 'Python', 'JavaScript', 'TypeScript', 'Go', 'PHP', 'Ruby', 'C#', 'C++',
            'Kotlin', 'Swift', 'Scala', 'Rust', 'SQL', 'HTML', 'CSS',
            'React', 'Vue', 'Angular', 'Next.js', 'Node.js', 'Spring', 'Django', 'Flask',
            'Rails', 'Laravel', '.NET', 'Express',
            'AWS', 'Azure', 'GCP', 'Docker', 'Kubernetes', 'Terraform',
            'MySQL', 'PostgreSQL', 'Oracle', 'MongoDB', 'Redis',
            'Git', 'Linux', 'Jenkins', 'CircleCI',
            'PM', 'PMO', 'リーダー', 'マネージャー',
        ]
        
        found_skills = []
        for skill in skill_keywords:
            # Case-insensitive search
            if re.search(rf'\b{re.escape(skill)}\b', text, re.IGNORECASE):
                found_skills.append(skill)
        
        return list(set(found_skills))
