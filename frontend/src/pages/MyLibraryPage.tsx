import { useNavigate } from "react-router-dom";
import { tokenUtils } from "../utils/token";

function MyLibraryPage() {
  const navigate = useNavigate();

  const handleLogout = () => {
    tokenUtils.remove();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-amber-50 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-amber-900">내 서재</h1>
          <button
            onClick={handleLogout}
            className="px-4 py-2 text-sm text-amber-900 hover:bg-amber-100 rounded-lg"
          >
            로그아웃
          </button>
        </div>
        <p className="text-amber-700">곧 책 목록이 여기에 나옵니다.</p>
      </div>
    </div>
  );
}

export default MyLibraryPage;
