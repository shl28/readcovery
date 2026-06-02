import apiClient from "./axios";

export interface AnalysisResponse {
    summary: string;
    keywords: string[];
    personality: string;
    quote_count: number;
    analyzed_at: string;
    from_cache: boolean;
}

export const analysisApi = {
    analyze: async (): Promise<AnalysisResponse> => {
        const response =
            await apiClient.post<AnalysisResponse>("/api/analysis");
        return response.data;
    },
};
