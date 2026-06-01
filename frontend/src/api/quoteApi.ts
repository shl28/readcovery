import apiClient from "./axios";

export interface QuoteCreateRequest {
    my_book_id: number;
    content: string;
    page: number | null;
}

export interface QuoteResponse {
    quote_id: number;
    my_book_id: number;
    book_title: string;
    content: string;
    page: number | null;
    created_at: string;
}

export const quoteApi = {
    getByMyBook: async (myBookId: number): Promise<QuoteResponse[]> => {
        const response = await apiClient.get<QuoteResponse[]>("/api/quotes", {
            params: { myBookId },
        });
        return response.data;
    },

    create: async (data: QuoteCreateRequest): Promise<QuoteResponse> => {
        const response = await apiClient.post<QuoteResponse>(
            "/api/quotes",
            data,
        );
        return response.data;
    },

    delete: async (quoteId: number): Promise<void> => {
        await apiClient.delete(`/api/quotes/${quoteId}`);
    },
};
