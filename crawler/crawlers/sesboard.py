"""
SESBoard Crawler - SESBoard専用クローラー
"""
import logging
import re
from typing import List, Optional
from urllib.parse import urljoin

from bs4 import BeautifulSoup

from models import JobData, PriceType
from crawlers.base import BaseCrawler
import config

logger = logging.getLogger(__name__)


class SESBoardCrawler(BaseCrawler):
    """SESBoard クローラー"""
    
    def __init__(self):
        super().__init__()
        source_config = config.SOURCES["sesboard"]
        self.source_name = source_config["name"]
        self.base_url = source_config["base_url"]
        self.list_url = source_config["list_url"]
    
    async def _crawl_all_pages(self) -> List[JobData]:
        """全ページをクロール"""
        all_jobs: List[JobData] = []
        page = 1
        
        while page <= config.MAX_PAGES:
            url = f"{self.list_url}?page={page}"
            logger.info(f"Fetching page {page}: {url}")
            
            html = await self._fetch_page(url)
            if not html:
                break
            
            jobs = await self._parse_job_list(html)
            if not jobs:
                logger.info(f"No more jobs found on page {page}")
                break
            
            all_jobs.extend(jobs)
            logger.info(f"Found {len(jobs)} jobs on page {page}")
            page += 1
        
        return all_jobs
    
    async def _parse_job_list(self, html: str) -> List[JobData]:
        """案件リストをパース"""
        soup = BeautifulSoup(html, 'lxml')
        jobs: List[JobData] = []
        
        # SESBoardの案件カード要素を探す
        # ページ構造に基づいて調整が必要
        job_cards = soup.select('h5')  # タイトルがh5で囲まれている
        
        for card in job_cards:
            try:
                job = self._parse_job_card(card, soup)
                if job:
                    jobs.append(job)
            except Exception as e:
                logger.warning(f"Failed to parse job card: {e}")
                continue
        
        return jobs
    
    def _parse_job_card(self, title_elem, soup: BeautifulSoup) -> Optional[JobData]:
        """個別の案件カードをパース"""
        try:
            # タイトル
            title = title_elem.get_text(strip=True)
            if not title:
                return None
            
            # 周辺要素から情報を取得
            parent = title_elem.parent
            if not parent:
                return None
            
            # URL（応募リンクから取得）
            apply_link = parent.find('a', href=lambda x: x and 'contact' in x)
            source_id = None
            if apply_link and 'href' in apply_link.attrs:
                href = apply_link['href']
                # id[0]=3754 のようなパターンからIDを抽出
                id_match = re.search(r'id%5B0%5D=(\d+)', href)
                if id_match:
                    source_id = id_match.group(1)
            
            # 詳細ページURLを構築
            if source_id:
                source_url = f"{self.base_url}/cases/{source_id}"
            else:
                source_url = f"{self.base_url}/cases/list"
            
            # ロケーション
            location = ""
            location_elem = parent.find(string=lambda x: x and 'エリア' in x)
            if location_elem:
                location = location_elem.strip()
            
            # スキル
            skills_text = parent.get_text()
            required_skills = self.extract_skills(skills_text)
            
            # 説明文
            description = ""
            desc_elem = parent.find('p') or parent.find(class_='description')
            if desc_elem:
                description = desc_elem.get_text(strip=True)
            else:
                # タイトル以降のテキストを説明として使用
                description = parent.get_text(strip=True).replace(title, '', 1).strip()
            
            # リモートタイプ検出
            remote_type = self.detect_remote_type(skills_text + " " + description)
            
            return JobData(
                source="SESBoard",
                source_url=source_url,
                source_id=source_id,
                title=title,
                location=location,
                required_skills=required_skills,
                remote_type=remote_type,
                description=description[:500] if description else None,
                price_type=PriceType.MONTHLY,
            )
            
        except Exception as e:
            logger.warning(f"Error parsing job card: {e}")
            return None
