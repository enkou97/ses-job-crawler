/**
 * FavoritesPage - お気に入り案件ページ
 */
import { useState } from 'react';
import { JobCard, JobDetail, Pagination } from '../components';
import { useFavorites, useJob } from '../hooks/useJobs';
import './FavoritesPage.css';

export function FavoritesPage() {
    const [page, setPage] = useState(0);
    const [selectedJobId, setSelectedJobId] = useState<number | null>(null);

    const { data: favoritesData, isLoading } = useFavorites(page, 20);
    const { data: selectedJob } = useJob(selectedJobId || 0);

    const handlePageChange = (newPage: number) => {
        setPage(newPage);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div className="page container">
            <div className="page-header">
                <h1 className="page-title">⭐ お気に入り</h1>
                {favoritesData && (
                    <span className="job-count">
                        {favoritesData.totalElements.toLocaleString()} 件
                    </span>
                )}
            </div>

            {isLoading && (
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            )}

            {favoritesData && favoritesData.content.length === 0 && (
                <div className="empty-state">
                    <div className="empty-state-icon">⭐</div>
                    <p>お気に入りの案件がありません</p>
                    <p className="text-muted text-sm">案件カードの☆をクリックして追加できます</p>
                </div>
            )}

            {favoritesData && favoritesData.content.length > 0 && (
                <>
                    <div className="job-grid">
                        {favoritesData.content.map(job => (
                            <JobCard
                                key={job.id}
                                job={job}
                                onClick={() => setSelectedJobId(job.id)}
                            />
                        ))}
                    </div>

                    <Pagination
                        currentPage={favoritesData.number}
                        totalPages={favoritesData.totalPages}
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
