import { Link } from "react-router-dom";
import { tokenUtils } from "../utils/token";

function HomePage() {
  const isLoggedIn = tokenUtils.exists();

  return (
    <div className="min-h-screen bg-amber-50 flex items-center justify-center px-4">
      <div className="bg-white p-8 rounded-xl shadow-lg text-center max-w-md">
        <h1 className="text-4xl font-bold text-amber-900 mb-2">Readcovery</h1>
        <p className="text-amber-700 mb-6">read + discovery</p>
        <p className="text-sm text-amber-600 mb-8">
          인용구로 나를 발견하는 독서 기록
        </p>

        {isLoggedIn ? (
          <Link
            to="/my-library"
            className="inline-block px-6 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800"
          >
            내 서재로 →
          </Link>
        ) : (
          <div className="flex gap-3 justify-center">
            <Link
              to="/login"
              className="px-4 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800"
            >
              로그인
            </Link>
            <Link
              to="/signup"
              className="px-4 py-2 bg-white text-amber-900 border border-amber-900 rounded-lg hover:bg-amber-100"
            >
              회원가입
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}

export default HomePage;
