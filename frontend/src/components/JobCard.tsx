/**
 * JobCard Component - 案件一覧のカード表示
 */
import { useState } from 'react';
import type { JobSummary, RemoteType } from '../types';
import { remoteTypeLabels, sourceLabels } from '../types';
import { useToggleFavorite } from '../hooks/useJobs';
import './JobCard.css';

interface JobCardProps {
    job: JobSummary;
    onClick?: () => void;
}

export function JobCard({ job, onClick }: JobCardProps) {
    const toggleFavorite = useToggleFavorite();
    const [isHovered, setIsHovered] = useState(false);

    const handleFavoriteClick = (e: React.MouseEvent) => {
        e.stopPropagation();
        toggleFavorite.mutate(job.id);
    };

    const getRemoteTypeClass = (type: RemoteType | null) => {
        if (!type) return '';
        return `tag-remote-${type.toLowerCase()}`;
    };

    const formatPrice = (price: number | null) => {
        if (!price) return '-';
        return `${price}万円`;
    };

    return (
        <article
            className={`job-card ${isHovered ? 'hovered' : ''}`}
            onClick={onClick}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            <div className="job-card-header">
                <div className="job-source">
                    <span className="tag">{sourceLabels[job.source] || job.source}</span>
                    {job.status === 'NEW' && <span className="tag tag-new">新着</span>}
                </div>
                <button
                    className={`favorite-btn ${job.isFavorite ? 'active' : ''}`}
                    onClick={handleFavoriteClick}
                    aria-label={job.isFavorite ? 'お気に入り解除' : 'お気に入り登録'}
                >
                    {job.isFavorite ? '★' : '☆'}
                </button>
            </div>

            <h3 className="job-title">{job.title}</h3>

            <div className="job-info">
                <div className="job-price">
                    <span className="price-label">単価</span>
                    <span className="price-value">{formatPrice(job.maxPrice)}</span>
                </div>

                {job.location && (
                    <div className="job-location">
                        <span className="location-icon">📍</span>
                        <span>{job.location}</span>
                    </div>
                )}

                {job.remoteType && (
                    <span className={`tag ${getRemoteTypeClass(job.remoteType)}`}>
                        {remoteTypeLabels[job.remoteType]}
                    </span>
                )}
            </div>

            {job.requiredSkills && job.requiredSkills.length > 0 && (
                <div className="job-skills">
                    {job.requiredSkills.slice(0, 5).map((skill, index) => (
                        <span key={index} className="tag tag-skill">{skill}</span>
                    ))}
                    {job.requiredSkills.length > 5 && (
                        <span className="tag">+{job.requiredSkills.length - 5}</span>
                    )}
                </div>
            )}

            <div className="job-card-footer">
                {job.postedAt && (
                    <span className="job-date text-muted text-sm">
                        {new Date(job.postedAt).toLocaleDateString('ja-JP')}
                    </span>
                )}
            </div>
        </article>
    );
}
