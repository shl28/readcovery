import axios from "axios";

export const extractErrorMessage = (
  error: unknown,
  fallback: string = "요청 처리에 실패했습니다.",
): string => {
  if (!axios.isAxiosError(error) || !error.response?.data) {
    return fallback;
  }

  const data = error.response.data;

  if (data.fieldErrors && typeof data.fieldErrors === "object") {
    const messages = Object.values(data.fieldErrors);
    if (messages.length > 0) {
      return messages.join(" ");
    }
  }

  if (typeof data.messages === "string") {
    return data.messages;
  }

  return fallback;
};
