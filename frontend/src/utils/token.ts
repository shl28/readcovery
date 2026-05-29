const TOKEN_KEY = "accessToken";

export const tokenUtils = {
    save: (token: string): void => {
        localStorage.setItem(TOKEN_KEY, token);
    },

    get: (): string | null => {
        return localStorage.getItem(TOKEN_KEY);
    },

    remove: (): void => {
        localStorage.removeItem(TOKEN_KEY);
    },

    exists: (): boolean => {
        return !!localStorage.getItem(TOKEN_KEY);
    },
};
