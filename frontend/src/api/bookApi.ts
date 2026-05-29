import apiClient from "./axios";

export interface BookSearchResult {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  thumbnail: string;
  contents: string;
}

export interface BookRegisterRequest {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  thumbnail: string;
  contents: string;
}

export interface BookResponse {
  id: number;
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  thumbnail: string;
  contents: string;
  created_at: string;
}

export const bookApi = {
  search: async (
    query: string,
    page: number = 1,
    size: number = 10,
  ): Promise<BookSearchResult[]> => {
    const response = await apiClient.get<BookSearchResult[]>(
      "/api/books/search",
      {
        params: { query, page, size },
      },
    );
    return response.data;
  },

  register: async (data: BookRegisterRequest): Promise<BookResponse> => {
    const response = await apiClient.post<BookResponse>("/api/books", data);
    return response.data;
  },
};
