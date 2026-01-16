/**
 * SearchFilter Component - 検索・フィルターUI
 */
import { useState } from 'react';
import type { SearchRequest, RemoteType } from '../types';
import { remoteTypeLabels } from '../types';
import './SearchFilter.css';

interface SearchFilterProps {
    onSearch: (request: SearchRequest) => void;
    initialValues?: SearchRequest;
}

export function SearchFilter({ onSearch, initialValues }: SearchFilterProps) {
    const [keyword, setKeyword] = useState(initialValues?.keyword || '');
    const [minPrice, setMinPrice] = useState(initialValues?.minPrice?.toString() || '');
    const [maxPrice, setMaxPrice] = useState(initialValues?.maxPrice?.toString() || '');
    const [location, setLocation] = useState(initialValues?.location || '');
    const [remoteType, setRemoteType] = useState<RemoteType | ''>(initialValues?.remoteType || '');
    const [isExpanded, setIsExpanded] = useState(false);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        const request: SearchRequest = {
            page: 0,
            size: 20,
        };

        if (keyword.trim()) request.keyword = keyword.trim();
        if (minPrice) request.minPrice = parseInt(minPrice);
        if (maxPrice) request.maxPrice = parseInt(maxPrice);
        if (location.trim()) request.location = location.trim();
        if (remoteType) request.remoteType = remoteType;

        onSearch(request);
    };

    const handleReset = () => {
        setKeyword('');
        setMinPrice('');
        setMaxPrice('');
        setLocation('');
        setRemoteType('');
        onSearch({ page: 0, size: 20 });
    };

    return (
        <div className="search-filter card-glass">
            <form onSubmit={handleSubmit}>
                <div className="search-main">
                    <div className="search-input-wrapper">
                        <span className="search-icon">🔍</span>
                        <input
                            type="text"
                            className="input search-input"
                            placeholder="キーワード検索（スキル、案件名など）"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                        />
                    </div>

                    <button type="submit" className="btn btn-primary">
                        検索
                    </button>

                    <button
                        type="button"
                        className="btn btn-ghost"
                        onClick={() => setIsExpanded(!isExpanded)}
                    >
                        {isExpanded ? '▲ 閉じる' : '▼ 詳細'}
                    </button>
                </div>

                {isExpanded && (
                    <div className="search-details">
                        <div className="filter-row">
                            <div className="filter-group">
                                <label className="filter-label">単価（万円）</label>
                                <div className="price-range">
                                    <input
                                        type="number"
                                        className="input"
                                        placeholder="下限"
                                        value={minPrice}
                                        onChange={(e) => setMinPrice(e.target.value)}
                                    />
                                    <span className="price-separator">〜</span>
                                    <input
                                        type="number"
                                        className="input"
                                        placeholder="上限"
                                        value={maxPrice}
                                        onChange={(e) => setMaxPrice(e.target.value)}
                                    />
                                </div>
                            </div>

                            <div className="filter-group">
                                <label className="filter-label">勤務地</label>
                                <input
                                    type="text"
                                    className="input"
                                    placeholder="例: 東京"
                                    value={location}
                                    onChange={(e) => setLocation(e.target.value)}
                                />
                            </div>

                            <div className="filter-group">
                                <label className="filter-label">リモート</label>
                                <select
                                    className="input select"
                                    value={remoteType}
                                    onChange={(e) => setRemoteType(e.target.value as RemoteType | '')}
                                >
                                    <option value="">すべて</option>
                                    {Object.entries(remoteTypeLabels).map(([value, label]) => (
                                        <option key={value} value={value}>{label}</option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="filter-actions">
                            <button type="button" className="btn btn-ghost" onClick={handleReset}>
                                リセット
                            </button>
                        </div>
                    </div>
                )}
            </form>
        </div>
    );
}
