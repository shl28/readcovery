import apiClient from "./axios";

export type ReadingStatus = "WANT" | "READING" | "DONE";

export interface MyBookCreateRequest {
  book_id: number;
  status: ReadingStatus;
}

export interface MyBookResponse {
  my_book_id: number;
  book_id: number;
  book_title: string;
  book_thumbnail: string;
  status: ReadingStatus;
  rating: number | null;
  started_at: string | null;
  finished_at: string | null;
  created_at: string;
}

export const myBookApi = {
  addToLibrary: async (data: MyBookCreateRequest): Promise<MyBookResponse> => {
    const response = await apiClient.post<MyBookResponse>(
      "/api/my-books",
      data,
    );
    return response.data;
  },

  getMyLibrary: async (): Promise<MyBookResponse[]> => {
    const response = await apiClient.get<MyBookResponse[]>("/api/my-books");
    return response.data;
  },
};
