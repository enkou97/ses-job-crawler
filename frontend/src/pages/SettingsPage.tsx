/**
 * SettingsPage - 通知設定ページ
 */
import { useState, useEffect } from 'react';
import { notificationService } from '../services/api';
import type { NotificationSettings, NotificationSettingsUpdate } from '../types/notification';
import './SettingsPage.css';

export function SettingsPage() {
    const [settings, setSettings] = useState<NotificationSettings | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [testResult, setTestResult] = useState<{ success: boolean; message: string } | null>(null);

    // Form state
    const [emailEnabled, setEmailEnabled] = useState(false);
    const [emailAddress, setEmailAddress] = useState('');
    const [lineEnabled, setLineEnabled] = useState(false);
    const [lineToken, setLineToken] = useState('');
    const [slackEnabled, setSlackEnabled] = useState(false);
    const [slackWebhookUrl, setSlackWebhookUrl] = useState('');
    const [minPriceThreshold, setMinPriceThreshold] = useState('');
    const [skillsFilter, setSkillsFilter] = useState('');
    const [remoteOnly, setRemoteOnly] = useState(false);
    const [notifyIntervalHours, setNotifyIntervalHours] = useState('6');

    useEffect(() => {
        loadSettings();
    }, []);

    const loadSettings = async () => {
        try {
            const data = await notificationService.getSettings();
            setSettings(data);
            setEmailEnabled(data.emailEnabled || false);
            setEmailAddress(data.emailAddress || '');
            setLineEnabled(data.lineEnabled || false);
            setSlackEnabled(data.slackEnabled || false);
            setMinPriceThreshold(data.minPriceThreshold?.toString() || '');
            setSkillsFilter(data.skillsFilter || '');
            setRemoteOnly(data.remoteOnly || false);
            setNotifyIntervalHours(data.notifyIntervalHours?.toString() || '6');
        } catch (error) {
            console.error('Failed to load settings:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async () => {
        setSaving(true);
        try {
            const update: NotificationSettingsUpdate = {
                emailEnabled,
                emailAddress: emailAddress || undefined,
                lineEnabled,
                lineToken: lineToken || undefined,
                slackEnabled,
                slackWebhookUrl: slackWebhookUrl || undefined,
                minPriceThreshold: minPriceThreshold ? parseInt(minPriceThreshold) : undefined,
                skillsFilter: skillsFilter || undefined,
                remoteOnly,
                notifyIntervalHours: parseInt(notifyIntervalHours) || 6,
            };
            await notificationService.updateSettings(update as unknown as Record<string, unknown>);
            await loadSettings();
            setTestResult({ success: true, message: '設定を保存しました' });
        } catch (error) {
            setTestResult({ success: false, message: '設定の保存に失敗しました' });
        } finally {
            setSaving(false);
        }
    };

    const handleTestNotification = async (channel: string) => {
        try {
            const result = await notificationService.sendTestNotification(channel);
            setTestResult(result);
        } catch (error) {
            setTestResult({ success: false, message: 'テスト通知の送信に失敗しました' });
        }
    };

    if (loading) {
        return (
            <div className="page container">
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="page container">
            <div className="page-header">
                <h1 className="page-title">⚙️ 通知設定</h1>
            </div>

            {testResult && (
                <div className={`alert ${testResult.success ? 'alert-success' : 'alert-error'}`}>
                    {testResult.message}
                    <button className="alert-close" onClick={() => setTestResult(null)}>×</button>
                </div>
            )}

            <div className="settings-grid">
                {/* Email Settings */}
                <div className="settings-card card">
                    <div className="settings-card-header">
                        <span className="settings-icon">📧</span>
                        <h3>メール通知</h3>
                        <label className="toggle">
                            <input
                                type="checkbox"
                                checked={emailEnabled}
                                onChange={(e) => setEmailEnabled(e.target.checked)}
                            />
                            <span className="toggle-slider"></span>
                        </label>
                    </div>
                    {emailEnabled && (
                        <div className="settings-card-body">
                            <label className="input-label">メールアドレス</label>
                            <input
                                type="email"
                                className="input"
                                placeholder="example@email.com"
                                value={emailAddress}
                                onChange={(e) => setEmailAddress(e.target.value)}
                            />
                            <button
                                className="btn btn-secondary mt-md"
                                onClick={() => handleTestNotification('email')}
                            >
                                テスト送信
                            </button>
                        </div>
                    )}
                </div>

                {/* LINE Settings */}
                <div className="settings-card card">
                    <div className="settings-card-header">
                        <span className="settings-icon">💬</span>
                        <h3>LINE通知</h3>
                        <label className="toggle">
                            <input
                                type="checkbox"
                                checked={lineEnabled}
                                onChange={(e) => setLineEnabled(e.target.checked)}
                            />
                            <span className="toggle-slider"></span>
                        </label>
                    </div>
                    {lineEnabled && (
                        <div className="settings-card-body">
                            <label className="input-label">LINE Notify Token</label>
                            <input
                                type="password"
                                className="input"
                                placeholder="トークンを入力"
                                value={lineToken}
                                onChange={(e) => setLineToken(e.target.value)}
                            />
                            <p className="input-hint">
                                <a href="https://notify-bot.line.me/" target="_blank" rel="noopener">
                                    LINE Notify
                                </a>
                                でトークンを取得してください
                            </p>
                            <button
                                className="btn btn-secondary mt-md"
                                onClick={() => handleTestNotification('line')}
                            >
                                テスト送信
                            </button>
                        </div>
                    )}
                </div>

                {/* Slack Settings */}
                <div className="settings-card card">
                    <div className="settings-card-header">
                        <span className="settings-icon">💼</span>
                        <h3>Slack通知</h3>
                        <label className="toggle">
                            <input
                                type="checkbox"
                                checked={slackEnabled}
                                onChange={(e) => setSlackEnabled(e.target.checked)}
                            />
                            <span className="toggle-slider"></span>
                        </label>
                    </div>
                    {slackEnabled && (
                        <div className="settings-card-body">
                            <label className="input-label">Webhook URL</label>
                            <input
                                type="password"
                                className="input"
                                placeholder="https://hooks.slack.com/services/..."
                                value={slackWebhookUrl}
                                onChange={(e) => setSlackWebhookUrl(e.target.value)}
                            />
                            <button
                                className="btn btn-secondary mt-md"
                                onClick={() => handleTestNotification('slack')}
                            >
                                テスト送信
                            </button>
                        </div>
                    )}
                </div>

                {/* Filter Settings */}
                <div className="settings-card card settings-card-wide">
                    <div className="settings-card-header">
                        <span className="settings-icon">🎯</span>
                        <h3>通知フィルター</h3>
                    </div>
                    <div className="settings-card-body">
                        <div className="filter-grid">
                            <div className="filter-item">
                                <label className="input-label">最低単価（万円）</label>
                                <input
                                    type="number"
                                    className="input"
                                    placeholder="例: 60"
                                    value={minPriceThreshold}
                                    onChange={(e) => setMinPriceThreshold(e.target.value)}
                                />
                            </div>

                            <div className="filter-item">
                                <label className="input-label">通知間隔（時間）</label>
                                <select
                                    className="input select"
                                    value={notifyIntervalHours}
                                    onChange={(e) => setNotifyIntervalHours(e.target.value)}
                                >
                                    <option value="1">1時間ごと</option>
                                    <option value="3">3時間ごと</option>
                                    <option value="6">6時間ごと</option>
                                    <option value="12">12時間ごと</option>
                                    <option value="24">24時間ごと</option>
                                </select>
                            </div>

                            <div className="filter-item">
                                <label className="input-label">スキルフィルター</label>
                                <input
                                    type="text"
                                    className="input"
                                    placeholder="Java, Python, AWS（カンマ区切り）"
                                    value={skillsFilter}
                                    onChange={(e) => setSkillsFilter(e.target.value)}
                                />
                            </div>

                            <div className="filter-item">
                                <label className="checkbox-label">
                                    <input
                                        type="checkbox"
                                        checked={remoteOnly}
                                        onChange={(e) => setRemoteOnly(e.target.checked)}
                                    />
                                    <span>リモート案件のみ通知</span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="settings-actions">
                <button
                    className="btn btn-primary"
                    onClick={handleSave}
                    disabled={saving}
                >
                    {saving ? '保存中...' : '設定を保存'}
                </button>
            </div>

            {settings?.lastNotifiedAt && (
                <p className="last-notified text-muted text-sm">
                    最終通知: {new Date(settings.lastNotifiedAt).toLocaleString('ja-JP')}
                </p>
            )}
        </div>
    );
}
