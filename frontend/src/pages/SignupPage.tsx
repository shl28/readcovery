import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";
import { extractErrorMessage } from "../utils/error";

function SignupPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit: React.SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setIsLoading(true);

    try {
      await userApi.signup({ email, password, nickname });
      alert("회원가입 성공! 로그인 페이지로 이동합니다.");
      navigate("/login");
    } catch (error: unknown) {
      setErrorMessage(extractErrorMessage(error, "회원가입에 실패했습니다."));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-amber-50 flex items-center justify-center px-4">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <h1 className="text-3xl font-bold text-amber-900 mb-2 text-center">
          회원가입
        </h1>
        <p className="text-amber-700 text-center mb-8">
          Readcovery와 함께 시작하세요
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
              placeholder="영문+숫자+특수문자 8~20자로 작성"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-amber-900 mb-1">
              닉네임
            </label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              required
              className="w-full px-3 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-500"
              placeholder="2~20자로 작성"
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
            {isLoading ? "가입 중..." : "회원가입"}
          </button>
        </form>

        <p className="text-center text-sm text-amber-700 mt-6">
          이미 계정이 있으신가요?{" "}
          <Link
            to="/login"
            className="text-amber-900 font-medium hover:underline"
          >
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}

export default SignupPage;
