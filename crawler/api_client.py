"""
API Client - バックエンドAPIとの通信
"""
import httpx
import logging
from typing import List, Optional
from models import JobData

logger = logging.getLogger(__name__)

BATCH_SIZE = 50  # 每批发送50条记录


class ApiClient:
    """バックエンドAPIクライアント"""
    
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip("/")
        self.client = httpx.Client(timeout=180.0)  # 增加到180秒
    
    def create_job(self, job: JobData) -> Optional[dict]:
        """単一案件を登録"""
        try:
            response = self.client.post(
                f"{self.base_url}/jobs",
                json=job.to_api_request()
            )
            response.raise_for_status()
            logger.info(f"Created job: {job.title}")
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"Failed to create job: {e}")
            return None
    
    def create_jobs_batch(self, jobs: List[JobData]) -> List[dict]:
        """複数案件を一括登録（分批发送）"""
        all_results = []
        total = len(jobs)
        
        # 分批发送
        for i in range(0, total, BATCH_SIZE):
            batch = jobs[i:i + BATCH_SIZE]
            batch_num = i // BATCH_SIZE + 1
            total_batches = (total + BATCH_SIZE - 1) // BATCH_SIZE
            
            logger.info(f"Sending batch {batch_num}/{total_batches} ({len(batch)} jobs)")
            
            try:
                response = self.client.post(
                    f"{self.base_url}/jobs/batch",
                    json=[job.to_api_request() for job in batch]
                )
                response.raise_for_status()
                results = response.json()
                all_results.extend(results)
                logger.info(f"Batch {batch_num} succeeded: {len(results)} jobs saved")
            except httpx.HTTPError as e:
                logger.error(f"Batch {batch_num} failed: {e}")
        
        logger.info(f"Total saved: {len(all_results)} / {total} jobs")
        return all_results

    
    def health_check(self) -> bool:
        """APIヘルスチェック"""
        try:
            response = self.client.get(f"{self.base_url}/jobs?page=0&size=1")
            return response.status_code == 200
        except httpx.HTTPError:
            return False
    
    def close(self):
        """クライアントを閉じる"""
        self.client.close()


class AsyncApiClient:
    """非同期APIクライアント"""
    
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip("/")
        self.client = httpx.AsyncClient(timeout=30.0)
    
    async def create_jobs_batch(self, jobs: List[JobData]) -> List[dict]:
        """複数案件を一括登録"""
        try:
            response = await self.client.post(
                f"{self.base_url}/jobs/batch",
                json=[job.to_api_request() for job in jobs]
            )
            response.raise_for_status()
            logger.info(f"Created {len(jobs)} jobs in batch")
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"Failed to create jobs batch: {e}")
            return []
    
    async def close(self):
        """クライアントを閉じる"""
        await self.client.aclose()
