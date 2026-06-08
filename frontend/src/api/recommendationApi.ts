import apiClient from "./axios";
import type { BookSearchResult } from "./bookApi";

export const recommendationApi = {
    getRecommendations: async (): Promise<BookSearchResult[]> => {
        const response = await apiClient.get<BookSearchResult[]>(
            "/api/recommendations",
        );
        return response.data;
    },
};
