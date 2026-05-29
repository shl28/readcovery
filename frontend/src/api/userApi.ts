import apiClient from "./axios";

// 로그인 요청 타입
export interface LoginRequest {
    email: string;
    password: string;
}

// 로그인 응답 타입
export interface LoginResponse {
    access_token: string;
    token_type: string;
    user_id: number;
    nickname: string;
}

export const userApi = {
    login: async (data: LoginRequest): Promise<LoginResponse> => {
        const response = await apiClient.post<LoginResponse>(
            "/api/users/login",
            data,
        );
        return response.data;
    },
};
