/**
 * React Query Hooks
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { jobService } from '../services/api';
import type { SearchRequest, JobStatus } from '../types';

// 案件一覧
export function useJobs(page: number = 0, size: number = 20, sortBy?: string, sortOrder?: string) {
    return useQuery({
        queryKey: ['jobs', page, size, sortBy, sortOrder],
        queryFn: () => jobService.getJobs(page, size, sortBy, sortOrder),
    });
}

// 案件詳細
export function useJob(id: number) {
    return useQuery({
        queryKey: ['job', id],
        queryFn: () => jobService.getJob(id),
        enabled: id > 0,
    });
}

// 案件検索
export function useSearchJobs(request: SearchRequest, enabled: boolean = true) {
    return useQuery({
        queryKey: ['jobs', 'search', request],
        queryFn: () => jobService.searchJobs(request),
        enabled,
    });
}

// お気に入り一覧
export function useFavorites(page: number = 0, size: number = 20) {
    return useQuery({
        queryKey: ['favorites', page, size],
        queryFn: () => jobService.getFavorites(page, size),
    });
}

// 統計情報
export function useStats() {
    return useQuery({
        queryKey: ['stats'],
        queryFn: () => jobService.getStats(),
    });
}

// ステータス更新
export function useUpdateStatus() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, status }: { id: number; status: JobStatus }) =>
            jobService.updateStatus(id, status),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['jobs'] });
            queryClient.invalidateQueries({ queryKey: ['job'] });
        },
    });
}

// お気に入り切替
export function useToggleFavorite() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: number) => jobService.toggleFavorite(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['jobs'] });
            queryClient.invalidateQueries({ queryKey: ['job'] });
            queryClient.invalidateQueries({ queryKey: ['favorites'] });
        },
    });
}
