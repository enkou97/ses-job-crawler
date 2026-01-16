"""
SES Job Crawler - Main Entry Point
"""
import asyncio
import logging
import sys
from datetime import datetime

from crawlers import SESBoardCrawler, TechDirectCrawler
from api_client import ApiClient
from models import JobData
import config

# Configure logging
logging.basicConfig(
    level=getattr(logging, config.LOG_LEVEL),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('crawler.log', encoding='utf-8')
    ]
)
logger = logging.getLogger(__name__)


async def run_crawler(crawler, api_client: ApiClient) -> int:
    """単一クローラーを実行し、結果をAPIに送信"""
    try:
        logger.info(f"Starting crawler: {crawler.source_name}")
        jobs = await crawler.get_jobs()
        
        if jobs:
            logger.info(f"Sending {len(jobs)} jobs to API")
            results = api_client.create_jobs_batch(jobs)
            logger.info(f"Successfully saved {len(results)} jobs")
            return len(results)
        else:
            logger.warning(f"No jobs found from {crawler.source_name}")
            return 0
            
    except Exception as e:
        logger.error(f"Crawler error for {crawler.source_name}: {e}", exc_info=True)
        return 0
    finally:
        await crawler.close()


async def main():
    """メイン処理"""
    logger.info("=" * 60)
    logger.info(f"SES Job Crawler started at {datetime.now()}")
    logger.info("=" * 60)
    
    # APIクライアント初期化
    api_client = ApiClient(config.BACKEND_API_URL)
    
    # APIヘルスチェック
    if not api_client.health_check():
        logger.error(f"Cannot connect to API: {config.BACKEND_API_URL}")
        logger.info("Running in standalone mode (no API connection)")
    
    total_jobs = 0
    
    # クローラーを順次実行
    crawlers = []
    
    # SESBoard
    if config.SOURCES["sesboard"]["enabled"]:
        crawlers.append(SESBoardCrawler())
    
    # Tech Direct (ログイン情報が必要)
    if config.SOURCES["techdirect"]["enabled"]:
        # TODO: 環境変数からログイン情報を取得
        # username = os.getenv("TECHDIRECT_USERNAME")
        # password = os.getenv("TECHDIRECT_PASSWORD")
        # if username and password:
        #     crawlers.append(TechDirectCrawler(username, password))
        # else:
        crawlers.append(TechDirectCrawler())  # ログインなしで公開情報のみ取得
    
    for crawler in crawlers:
        count = await run_crawler(crawler, api_client)
        total_jobs += count
    
    api_client.close()
    
    logger.info("=" * 60)
    logger.info(f"Crawl completed. Total jobs collected: {total_jobs}")
    logger.info("=" * 60)


def test_single_crawler():
    """単一クローラーのテスト実行"""
    async def _test():
        crawler = SESBoardCrawler()
        jobs = await crawler.get_jobs()
        
        print(f"\n=== Found {len(jobs)} jobs ===\n")
        for job in jobs[:5]:  # 最初の5件のみ表示
            print(f"Title: {job.title}")
            print(f"URL: {job.source_url}")
            print(f"Location: {job.location}")
            print(f"Skills: {job.required_skills}")
            print("-" * 40)
        
        await crawler.close()
    
    asyncio.run(_test())


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "test":
        test_single_crawler()
    else:
        asyncio.run(main())
