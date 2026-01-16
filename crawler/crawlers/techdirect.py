"""
Tech Direct Crawler - Tech Direct専用クローラー（ログイン対応）
"""
import logging
import re
from typing import List, Optional

from bs4 import BeautifulSoup
from playwright.async_api import async_playwright

from models import JobData, PriceType
from crawlers.base import BaseCrawler
import config

logger = logging.getLogger(__name__)


class TechDirectCrawler(BaseCrawler):
    """Tech Direct クローラー（Playwright使用）"""
    
    def __init__(self, username: str = None, password: str = None):
        super().__init__()
        source_config = config.SOURCES["techdirect"]
        self.source_name = source_config["name"]
        self.base_url = source_config["base_url"]
        self.list_url = source_config["list_url"]
        self.login_url = source_config.get("login_url")
        self.username = username
        self.password = password
    
    async def _crawl_all_pages(self) -> List[JobData]:
        """全ページをクロール（Playwright使用）"""
        all_jobs: List[JobData] = []
        
        async with async_playwright() as p:
            browser = await p.chromium.launch(headless=True)
            context = await browser.new_context(user_agent=config.USER_AGENT)
            page = await context.new_page()
            
            try:
                # ログインが必要な場合
                if self.username and self.password and self.login_url:
                    await self._login(page)
                
                # 案件一覧ページにアクセス
                await page.goto(self.list_url)
                await page.wait_for_load_state('networkidle')
                
                # ページ番号を進めながらクロール
                page_num = 1
                while page_num <= config.MAX_PAGES:
                    logger.info(f"Processing page {page_num}")
                    
                    html = await page.content()
                    jobs = await self._parse_job_list(html)
                    
                    if not jobs:
                        break
                    
                    all_jobs.extend(jobs)
                    logger.info(f"Found {len(jobs)} jobs on page {page_num}")
                    
                    # 次のページへ
                    next_button = await page.query_selector('a[rel="next"], button:has-text("次へ")')
                    if not next_button:
                        break
                    
                    await next_button.click()
                    await page.wait_for_load_state('networkidle')
                    page_num += 1
                    
            except Exception as e:
                logger.error(f"Error during crawl: {e}")
            finally:
                await browser.close()
        
        return all_jobs
    
    async def _login(self, page):
        """ログイン処理"""
        try:
            await page.goto(self.login_url)
            await page.wait_for_load_state('networkidle')
            
            # ログインフォームに入力
            await page.fill('input[type="email"], input[name="email"]', self.username)
            await page.fill('input[type="password"], input[name="password"]', self.password)
            
            # ログインボタンをクリック
            await page.click('button[type="submit"], input[type="submit"]')
            await page.wait_for_load_state('networkidle')
            
            logger.info("Login successful")
        except Exception as e:
            logger.error(f"Login failed: {e}")
            raise
    
    async def _parse_job_list(self, html: str) -> List[JobData]:
        """案件リストをパース"""
        soup = BeautifulSoup(html, 'lxml')
        jobs: List[JobData] = []
        
        # Tech Directの案件リンクを探す
        job_links = soup.select('a[href*="/jobs/"]')
        
        seen_urls = set()
        for link in job_links:
            try:
                href = link.get('href', '')
                if not href or href in seen_urls:
                    continue
                
                # 案件詳細ページのURLかチェック
                if not re.match(r'/jobs/\d+', href):
                    continue
                
                seen_urls.add(href)
                
                job = self._parse_job_link(link, soup)
                if job:
                    jobs.append(job)
                    
            except Exception as e:
                logger.warning(f"Failed to parse job link: {e}")
                continue
        
        return jobs
    
    def _parse_job_link(self, link_elem, soup: BeautifulSoup) -> Optional[JobData]:
        """案件リンク要素をパース"""
        try:
            href = link_elem.get('href', '')
            full_url = f"{self.base_url}{href}"
            
            # IDを抽出
            id_match = re.search(r'/jobs/(\d+)', href)
            source_id = id_match.group(1) if id_match else None
            
            # タイトル
            title = link_elem.get_text(strip=True)
            if not title or len(title) < 5:
                return None
            
            # 親要素から詳細情報を取得
            parent = link_elem.find_parent(['div', 'li', 'article'])
            if not parent:
                parent = link_elem.parent
            
            # 単価
            price_text = ""
            price_elem = parent.find(string=lambda x: x and ('万' in x or '円' in x))
            if price_elem:
                price_text = price_elem.strip()
            
            min_price, max_price = self.parse_price(price_text)
            
            # スキル
            all_text = parent.get_text() if parent else ""
            required_skills = self.extract_skills(all_text)
            
            # リモートタイプ
            remote_type = self.detect_remote_type(title + " " + all_text)
            
            # 稼働日数
            work_days = None
            days_match = re.search(r'週(\d+)[〜~]?(\d*)\s*日?', all_text)
            if days_match:
                if days_match.group(2):
                    work_days = f"週{days_match.group(1)}〜{days_match.group(2)}日"
                else:
                    work_days = f"週{days_match.group(1)}日"
            
            return JobData(
                source="TechDirect",
                source_url=full_url,
                source_id=source_id,
                title=title,
                min_price=min_price,
                max_price=max_price,
                price_type=PriceType.MONTHLY,
                required_skills=required_skills,
                remote_type=remote_type,
                work_days=work_days,
            )
            
        except Exception as e:
            logger.warning(f"Error parsing job link: {e}")
            return None
