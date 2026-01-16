/**
 * API Service - バックエンドAPIとの通信
 */
import axios from 'axios';
import type { Job, JobSummary, PageResponse, SearchRequest, StatsOverview, JobStatus } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const jobService = {
    // 案件一覧取得
    async getJobs(
        page: number = 0,
        size: number = 20,
        sortBy: string = 'crawledAt',
        sortOrder: string = 'desc'
    ): Promise<PageResponse<JobSummary>> {
        const { data } = await api.get<PageResponse<JobSummary>>('/jobs', {
            params: { page, size, sortBy, sortOrder },
        });
        return data;
    },

    // 案件詳細取得
    async getJob(id: number): Promise<Job> {
        const { data } = await api.get<Job>(`/jobs/${id}`);
        return data;
    },

    // 案件検索
    async searchJobs(request: SearchRequest): Promise<PageResponse<JobSummary>> {
        const { data } = await api.post<PageResponse<JobSummary>>('/jobs/search', request);
        return data;
    },

    // ステータス更新
    async updateStatus(id: number, status: JobStatus): Promise<Job> {
        const { data } = await api.patch<Job>(`/jobs/${id}/status`, { status });
        return data;
    },

    // お気に入り切替
    async toggleFavorite(id: number): Promise<Job> {
        const { data } = await api.post<Job>(`/jobs/${id}/favorite`);
        return data;
    },

    // お気に入り一覧
    async getFavorites(page: number = 0, size: number = 20): Promise<PageResponse<JobSummary>> {
        const { data } = await api.get<PageResponse<JobSummary>>('/jobs/favorites', {
            params: { page, size },
        });
        return data;
    },

    // 統計情報
    async getStats(): Promise<StatsOverview> {
        const { data } = await api.get<StatsOverview>('/jobs/stats');
        return data;
    },
};

// Notification Settings API
export const notificationService = {
    async getSettings() {
        const { data } = await api.get('/notifications/settings');
        return data;
    },

    async updateSettings(settings: Record<string, unknown>) {
        const { data } = await api.put('/notifications/settings', settings);
        return data;
    },

    async sendTestNotification(channel: string) {
        const { data } = await api.post(`/notifications/test/${channel}`);
        return data;
    },
};

export default api;
