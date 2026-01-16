/**
 * Header Component
 */
import { Link, useLocation } from 'react-router-dom';
import './Header.css';

export function Header() {
    const location = useLocation();

    const navItems = [
        { path: '/', label: '案件一覧', icon: '📋' },
        { path: '/favorites', label: 'お気に入り', icon: '⭐' },
        { path: '/stats', label: '統計', icon: '📊' },
        { path: '/settings', label: '設定', icon: '⚙️' },
    ];

    return (
        <header className="header">
            <div className="header-container container">
                <Link to="/" className="header-logo">
                    <span className="logo-icon">🔍</span>
                    <span className="logo-text">SES Job Crawler</span>
                </Link>

                <nav className="header-nav">
                    {navItems.map(item => (
                        <Link
                            key={item.path}
                            to={item.path}
                            className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
                        >
                            <span className="nav-icon">{item.icon}</span>
                            <span className="nav-label">{item.label}</span>
                        </Link>
                    ))}
                </nav>
            </div>
        </header>
    );
}
