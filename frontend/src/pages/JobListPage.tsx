/**
 * JobListPage - 案件一覧ページ
 */
import { useState, useCallback } from 'react';
import { SearchFilter, JobCard, JobDetail, Pagination } from '../components';
import { useSearchJobs, useJob } from '../hooks/useJobs';
import type { SearchRequest } from '../types';
import './JobListPage.css';

export function JobListPage() {
    const [searchRequest, setSearchRequest] = useState<SearchRequest>({
        page: 0,
        size: 20,
        sortBy: 'crawledAt',
        sortOrder: 'desc',
    });

    const [selectedJobId, setSelectedJobId] = useState<number | null>(null);

    const { data: jobsData, isLoading, error } = useSearchJobs(searchRequest);
    const { data: selectedJob } = useJob(selectedJobId || 0);

    const handleSearch = useCallback((request: SearchRequest) => {
        setSearchRequest({
            ...request,
            sortBy: searchRequest.sortBy,
            sortOrder: searchRequest.sortOrder,
        });
    }, [searchRequest.sortBy, searchRequest.sortOrder]);

    const handlePageChange = (page: number) => {
        setSearchRequest(prev => ({ ...prev, page }));
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const [sortBy, sortOrder] = e.target.value.split(':') as [string, 'asc' | 'desc'];
        setSearchRequest(prev => ({ ...prev, sortBy, sortOrder, page: 0 }));
    };

    return (
        <div className="page container">
            <div className="page-header">
                <h1 className="page-title">案件一覧</h1>
                {jobsData && (
                    <span className="job-count">
                        {jobsData.totalElements.toLocaleString()} 件
                    </span>
                )}
            </div>

            <SearchFilter onSearch={handleSearch} initialValues={searchRequest} />

            <div className="list-header">
                <div className="sort-control">
                    <label className="sort-label">並び順:</label>
                    <select
                        className="input select"
                        value={`${searchRequest.sortBy}:${searchRequest.sortOrder}`}
                        onChange={handleSortChange}
                    >
                        <option value="crawledAt:desc">取得日時（新しい順）</option>
                        <option value="crawledAt:asc">取得日時（古い順）</option>
                        <option value="maxPrice:desc">単価（高い順）</option>
                        <option value="maxPrice:asc">単価（低い順）</option>
                        <option value="postedAt:desc">掲載日（新しい順）</option>
                    </select>
                </div>
            </div>

            {isLoading && (
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            )}

            {error && (
                <div className="error-state card">
                    <p>データの取得に失敗しました。</p>
                    <p className="text-muted text-sm">APIサーバーが起動しているか確認してください。</p>
                </div>
            )}

            {jobsData && jobsData.content.length === 0 && (
                <div className="empty-state">
                    <div className="empty-state-icon">📋</div>
                    <p>案件が見つかりませんでした</p>
                    <p className="text-muted text-sm">検索条件を変更してお試しください</p>
                </div>
            )}

            {jobsData && jobsData.content.length > 0 && (
                <>
                    <div className="job-grid">
                        {jobsData.content.map(job => (
                            <JobCard
                                key={job.id}
                                job={job}
                                onClick={() => setSelectedJobId(job.id)}
                            />
                        ))}
                    </div>

                    <Pagination
                        currentPage={jobsData.number}
                        totalPages={jobsData.totalPages}
                        onPageChange={handlePageChange}
                    />
                </>
            )}

            {selectedJobId && selectedJob && (
                <JobDetail
                    job={selectedJob}
                    onClose={() => setSelectedJobId(null)}
                />
            )}
        </div>
    );
}
