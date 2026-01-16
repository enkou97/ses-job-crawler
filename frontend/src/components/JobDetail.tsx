/**
 * JobDetail Modal - 案件詳細表示モーダル
 */
import { useEffect } from 'react';
import type { Job } from '../types';
import { remoteTypeLabels, statusLabels } from '../types';
import { useToggleFavorite, useUpdateStatus } from '../hooks/useJobs';
import './JobDetail.css';

interface JobDetailProps {
    job: Job;
    onClose: () => void;
}

export function JobDetail({ job, onClose }: JobDetailProps) {
    const toggleFavorite = useToggleFavorite();
    const updateStatus = useUpdateStatus();

    useEffect(() => {
        // Mark as read when opened
        if (job.status === 'NEW') {
            updateStatus.mutate({ id: job.id, status: 'READ' });
        }

        // Close on escape key
        const handleEscape = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose();
        };
        document.addEventListener('keydown', handleEscape);
        return () => document.removeEventListener('keydown', handleEscape);
    }, [job.id, job.status]);

    const handleBackdropClick = (e: React.MouseEvent) => {
        if (e.target === e.currentTarget) onClose();
    };

    const formatPrice = () => {
        if (!job.minPrice && !job.maxPrice) return '-';
        if (job.minPrice === job.maxPrice) return `${job.maxPrice}万円`;
        return `${job.minPrice || '-'}〜${job.maxPrice || '-'}万円`;
    };

    return (
        <div className="modal-backdrop" onClick={handleBackdropClick}>
            <div className="modal-content">
                <div className="modal-header">
                    <div className="modal-title-area">
                        <span className="tag">{job.source}</span>
                        <span className={`tag tag-status-${job.status.toLowerCase()}`}>
                            {statusLabels[job.status]}
                        </span>
                    </div>

                    <div className="modal-actions">
                        <button
                            className={`btn btn-icon favorite-btn ${job.isFavorite ? 'active' : ''}`}
                            onClick={() => toggleFavorite.mutate(job.id)}
                        >
                            {job.isFavorite ? '★' : '☆'}
                        </button>
                        <button className="btn btn-icon" onClick={onClose}>✕</button>
                    </div>
                </div>

                <h2 className="modal-title">{job.title}</h2>

                <div className="detail-grid">
                    <div className="detail-card detail-price">
                        <span className="detail-label">月額単価</span>
                        <span className="detail-value price-value">{formatPrice()}</span>
                        {job.settlementHours && (
                            <span className="detail-sub">精算: {job.settlementHours}</span>
                        )}
                    </div>

                    <div className="detail-card">
                        <span className="detail-label">勤務地</span>
                        <span className="detail-value">{job.location || '-'}</span>
                    </div>

                    <div className="detail-card">
                        <span className="detail-label">リモート</span>
                        <span className="detail-value">
                            {job.remoteType ? remoteTypeLabels[job.remoteType] : '-'}
                        </span>
                    </div>

                    <div className="detail-card">
                        <span className="detail-label">稼働日数</span>
                        <span className="detail-value">{job.workDays || '-'}</span>
                    </div>
                </div>

                {(job.requiredSkills?.length > 0 || job.preferredSkills?.length > 0) && (
                    <div className="detail-section">
                        <h3 className="section-title">スキル要件</h3>

                        {job.requiredSkills?.length > 0 && (
                            <div className="skill-block">
                                <span className="skill-label">必須</span>
                                <div className="skill-tags">
                                    {job.requiredSkills.map((skill, i) => (
                                        <span key={i} className="tag tag-skill">{skill}</span>
                                    ))}
                                </div>
                            </div>
                        )}

                        {job.preferredSkills?.length > 0 && (
                            <div className="skill-block">
                                <span className="skill-label">歓迎</span>
                                <div className="skill-tags">
                                    {job.preferredSkills.map((skill, i) => (
                                        <span key={i} className="tag">{skill}</span>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>
                )}

                {job.description && (
                    <div className="detail-section">
                        <h3 className="section-title">詳細</h3>
                        <p className="detail-description">{job.description}</p>
                    </div>
                )}

                <div className="modal-footer">
                    <a
                        href={job.sourceUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="btn btn-primary"
                    >
                        元サイトで見る →
                    </a>

                    <div className="detail-meta text-sm text-muted">
                        取得日時: {new Date(job.crawledAt).toLocaleString('ja-JP')}
                    </div>
                </div>
            </div>
        </div>
    );
}
