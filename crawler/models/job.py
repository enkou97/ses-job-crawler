"""
SES Job Crawler - Data Models
"""
from pydantic import BaseModel, Field, HttpUrl
from typing import Optional, List
from datetime import date, datetime
from enum import Enum


class PriceType(str, Enum):
    MONTHLY = "MONTHLY"
    HOURLY = "HOURLY"


class RemoteType(str, Enum):
    FULL = "FULL"
    PARTIAL = "PARTIAL"
    NONE = "NONE"


class JobData(BaseModel):
    """案件データモデル"""
    
    source: str = Field(..., description="データソース名")
    source_url: HttpUrl = Field(..., description="元のURL")
    source_id: Optional[str] = Field(None, description="サイト内のID")
    title: str = Field(..., description="案件タイトル")
    
    # 報酬情報
    min_price: Optional[int] = Field(None, description="最低単価（万円）")
    max_price: Optional[int] = Field(None, description="最高単価（万円）")
    price_type: Optional[PriceType] = Field(None, description="単価種別")
    settlement_hours: Optional[str] = Field(None, description="精算幅")
    
    # スキル要件
    required_skills: List[str] = Field(default_factory=list, description="必須スキル")
    preferred_skills: List[str] = Field(default_factory=list, description="歓迎スキル")
    experience_years: Optional[str] = Field(None, description="経験年数")
    
    # 勤務条件
    location: Optional[str] = Field(None, description="勤務地")
    remote_type: Optional[RemoteType] = Field(None, description="リモート可否")
    work_days: Optional[str] = Field(None, description="稼働日数")
    start_date: Optional[date] = Field(None, description="稼働開始日")
    contract_period: Optional[str] = Field(None, description="契約期間")
    
    # その他
    company_name: Optional[str] = Field(None, description="企業名")
    industry: Optional[str] = Field(None, description="業界")
    description: Optional[str] = Field(None, description="詳細説明")
    posted_at: Optional[datetime] = Field(None, description="掲載日時")

    def to_api_request(self) -> dict:
        """API送信用の辞書に変換"""
        data = self.model_dump(exclude_none=True)
        # Convert HttpUrl to string
        data["source_url"] = str(data["source_url"])
        # Convert snake_case to camelCase for API
        return self._to_camel_case(data)
    
    def _to_camel_case(self, data: dict) -> dict:
        """snake_case を camelCase に変換"""
        def convert_key(key: str) -> str:
            components = key.split('_')
            return components[0] + ''.join(x.title() for x in components[1:])
        
        result = {}
        for key, value in data.items():
            new_key = convert_key(key)
            if isinstance(value, dict):
                result[new_key] = self._to_camel_case(value)
            elif isinstance(value, list):
                result[new_key] = value
            elif isinstance(value, Enum):
                result[new_key] = value.value
            elif isinstance(value, (date, datetime)):
                result[new_key] = value.isoformat() if value else None
            else:
                result[new_key] = value
        return result


class CrawlResult(BaseModel):
    """クロール結果"""
    source: str
    success: bool
    jobs_count: int
    error_message: Optional[str] = None
    crawled_at: datetime = Field(default_factory=datetime.now)
