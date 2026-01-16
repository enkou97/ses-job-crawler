"""
API Client - バックエンドAPIとの通信
"""
import httpx
import logging
from typing import List, Optional
from models import JobData

logger = logging.getLogger(__name__)


class ApiClient:
    """バックエンドAPIクライアント"""
    
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip("/")
        self.client = httpx.Client(timeout=30.0)
    
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
        """複数案件を一括登録"""
        try:
            response = self.client.post(
                f"{self.base_url}/jobs/batch",
                json=[job.to_api_request() for job in jobs]
            )
            response.raise_for_status()
            logger.info(f"Created {len(jobs)} jobs in batch")
            return response.json()
        except httpx.HTTPError as e:
            logger.error(f"Failed to create jobs batch: {e}")
            return []
    
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
