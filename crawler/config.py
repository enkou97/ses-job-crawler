"""
SES Job Crawler - Configuration
"""
import os
from dotenv import load_dotenv

load_dotenv()

# Backend API Configuration
BACKEND_API_URL = os.getenv("BACKEND_API_URL", "http://localhost:8080/api")

# Crawler Configuration
REQUEST_DELAY = float(os.getenv("REQUEST_DELAY", "2.0"))  # Delay between requests (seconds)
MAX_PAGES = int(os.getenv("MAX_PAGES", "10"))  # Maximum pages to crawl per source
TIMEOUT = int(os.getenv("TIMEOUT", "30"))  # Request timeout (seconds)

# User Agent
USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# Source Configuration
SOURCES = {
    "sesboard": {
        "name": "SESBoard",
        "base_url": "https://sesboard.jp",
        "list_url": "https://sesboard.jp/cases/list",
        "enabled": True,
        "requires_login": False,
    },
    "techdirect": {
        "name": "Tech Direct",
        "base_url": "https://techdirect.jp",
        "list_url": "https://techdirect.jp/jobs",
        "enabled": True,
        "requires_login": True,
        "login_url": "https://techdirect.jp/login",
    },
    "freelanceboard": {
        "name": "フリーランスボード",
        "base_url": "https://freelance-board.com",
        "list_url": "https://freelance-board.com/jobs",
        "enabled": True,
        "requires_login": False,
    },
}

# Logging
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
