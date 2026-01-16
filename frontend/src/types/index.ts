/**
 * API Types - 案件関連の型定義
 */

// 単価種別
export type PriceType = 'MONTHLY' | 'HOURLY';

// リモート種別
export type RemoteType = 'FULL' | 'PARTIAL' | 'NONE';

// 案件ステータス
export type JobStatus = 'NEW' | 'READ' | 'APPLIED' | 'CLOSED';

// 案件サマリー（一覧表示用）
export interface JobSummary {
    id: number;
    source: string;
    title: string;
    maxPrice: number | null;
    location: string | null;
    remoteType: RemoteType | null;
    requiredSkills: string[];
    status: JobStatus;
    isFavorite: boolean;
    postedAt: string | null;
}

// 案件詳細
export interface Job {
    id: number;
    source: string;
    sourceUrl: string;
    sourceId: string | null;
    title: string;
    minPrice: number | null;
    maxPrice: number | null;
    priceType: PriceType | null;
    settlementHours: string | null;
    requiredSkills: string[];
    preferredSkills: string[];
    experienceYears: string | null;
    location: string | null;
    remoteType: RemoteType | null;
    workDays: string | null;
    startDate: string | null;
    contractPeriod: string | null;
    companyName: string | null;
    industry: string | null;
    description: string | null;
    status: JobStatus;
    isFavorite: boolean;
    postedAt: string | null;
    crawledAt: string;
    createdAt: string;
}

// ページネーションレスポンス
export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

// 検索リクエスト
export interface SearchRequest {
    keyword?: string;
    skills?: string[];
    minPrice?: number;
    maxPrice?: number;
    location?: string;
    remoteType?: RemoteType;
    sources?: string[];
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    page?: number;
    size?: number;
}

// 統計情報
export interface StatsOverview {
    totalJobs: number;
    newJobs: number;
    favoriteJobs: number;
    averagePrice: number | null;
    jobsBySource: Record<string, number>;
}

// リモートタイプのラベル
export const remoteTypeLabels: Record<RemoteType, string> = {
    FULL: 'フルリモート',
    PARTIAL: '一部リモート',
    NONE: '出社',
};

// ステータスのラベル
export const statusLabels: Record<JobStatus, string> = {
    NEW: '新着',
    READ: '既読',
    APPLIED: '応募済',
    CLOSED: '終了',
};

// ソースのラベル
export const sourceLabels: Record<string, string> = {
    SESBoard: 'SESBoard',
    TechDirect: 'Tech Direct',
    FreelanceBoard: 'フリーランスボード',
};
