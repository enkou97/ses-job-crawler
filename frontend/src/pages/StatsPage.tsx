/**
 * StatsPage - 統計ダッシュボード
 */
import { useStats } from '../hooks/useJobs';
import './StatsPage.css';

export function StatsPage() {
    const { data: stats, isLoading, error } = useStats();

    if (isLoading) {
        return (
            <div className="page container">
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            </div>
        );
    }

    if (error || !stats) {
        return (
            <div className="page container">
                <div className="error-state card">
                    <p>統計データの取得に失敗しました</p>
                    <p className="text-muted text-sm">APIサーバーが起動しているか確認してください</p>
                </div>
            </div>
        );
    }

    return (
        <div className="page container">
            <div className="page-header">
                <h1 className="page-title">📊 統計ダッシュボード</h1>
            </div>

            <div className="stats-grid">
                <div className="stat-card stat-total">
                    <div className="stat-icon">📋</div>
                    <div className="stat-content">
                        <span className="stat-value">{stats.totalJobs.toLocaleString()}</span>
                        <span className="stat-label">総案件数</span>
                    </div>
                </div>

                <div className="stat-card stat-new">
                    <div className="stat-icon">🆕</div>
                    <div className="stat-content">
                        <span className="stat-value">{stats.newJobs.toLocaleString()}</span>
                        <span className="stat-label">新着案件</span>
                    </div>
                </div>

                <div className="stat-card stat-favorite">
                    <div className="stat-icon">⭐</div>
                    <div className="stat-content">
                        <span className="stat-value">{stats.favoriteJobs.toLocaleString()}</span>
                        <span className="stat-label">お気に入り</span>
                    </div>
                </div>

                <div className="stat-card stat-price">
                    <div className="stat-icon">💰</div>
                    <div className="stat-content">
                        <span className="stat-value">
                            {stats.averagePrice ? `${Math.round(stats.averagePrice)}万円` : '-'}
                        </span>
                        <span className="stat-label">平均単価</span>
                    </div>
                </div>
            </div>

            {Object.keys(stats.jobsBySource).length > 0 && (
                <div className="source-section card">
                    <h2 className="section-title">ソース別案件数</h2>
                    <div className="source-bars">
                        {Object.entries(stats.jobsBySource)
                            .sort(([, a], [, b]) => b - a)
                            .map(([source, count]) => {
                                const maxCount = Math.max(...Object.values(stats.jobsBySource));
                                const percentage = (count / maxCount) * 100;

                                return (
                                    <div key={source} className="source-bar-item">
                                        <div className="source-bar-header">
                                            <span className="source-name">{source}</span>
                                            <span className="source-count">{count.toLocaleString()} 件</span>
                                        </div>
                                        <div className="source-bar-track">
                                            <div
                                                className="source-bar-fill"
                                                style={{ width: `${percentage}%` }}
                                            />
                                        </div>
                                    </div>
                                );
                            })}
                    </div>
                </div>
            )}

            <div className="info-card card">
                <h3>📌 使い方</h3>
                <ul className="info-list">
                    <li>案件一覧で気になる案件をチェック</li>
                    <li>☆をクリックしてお気に入りに追加</li>
                    <li>詳細画面から元サイトへジャンプして応募</li>
                </ul>
            </div>
        </div>
    );
}
