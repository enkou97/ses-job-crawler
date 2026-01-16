/**
 * Pagination Component
 */
import './Pagination.css';

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
}

export function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
    if (totalPages <= 1) return null;

    const getVisiblePages = () => {
        const pages: (number | string)[] = [];
        const maxVisible = 5;

        if (totalPages <= maxVisible) {
            for (let i = 0; i < totalPages; i++) pages.push(i);
        } else {
            pages.push(0);

            if (currentPage > 2) pages.push('...');

            const start = Math.max(1, currentPage - 1);
            const end = Math.min(totalPages - 2, currentPage + 1);

            for (let i = start; i <= end; i++) pages.push(i);

            if (currentPage < totalPages - 3) pages.push('...');

            pages.push(totalPages - 1);
        }

        return pages;
    };

    return (
        <nav className="pagination">
            <button
                className="btn btn-ghost pagination-btn"
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 0}
            >
                ← 前へ
            </button>

            <div className="pagination-pages">
                {getVisiblePages().map((page, index) => (
                    typeof page === 'number' ? (
                        <button
                            key={index}
                            className={`pagination-page ${page === currentPage ? 'active' : ''}`}
                            onClick={() => onPageChange(page)}
                        >
                            {page + 1}
                        </button>
                    ) : (
                        <span key={index} className="pagination-ellipsis">{page}</span>
                    )
                ))}
            </div>

            <button
                className="btn btn-ghost pagination-btn"
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
            >
                次へ →
            </button>
        </nav>
    );
}
