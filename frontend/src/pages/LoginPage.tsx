import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";
import { tokenUtils } from "../utils/token";
import axios from "axios";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit: React.SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setIsLoading(true);

    try {
      const response = await userApi.login({ email, password });
      tokenUtils.save(response.access_token);
      navigate("/my-library");
    } catch (error: unknown) {
      const message =
        axios.isAxiosError(error) && error.response?.data?.message
          ? error.response.data.message
          : "로그인에 실패했습니다.";
      setErrorMessage(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-amber-50 flex items-center justify-center px-4">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <h1 className="text-3xl font-bold text-amber-900 mb-2 text-center">
          Readcovery
        </h1>
        <p className="text-amber-700 text-center mb-8">
          로그인하고 인용구를 모아보세요
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-amber-900 mb-1">
              이메일
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full px-3 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-500"
              placeholder="email@example.com"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-amber-900 mb-1">
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full px-3 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-500"
            />
          </div>

          {errorMessage && (
            <p className="text-red-600 text-sm">{errorMessage}</p>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800 disabled:bg-amber-300 disabled:cursor-not-allowed"
          >
            {isLoading ? "로그인 중..." : "로그인"}
          </button>
        </form>

        <p className="text-center text-sm text-amber-700 mt-6">
          계정이 없으신가요?{" "}
          <Link
            to="/signup"
            className="text-amber-900 font-medium hover:underline"
          >
            회원가입
          </Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
