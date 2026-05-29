import { Link, useNavigate } from "react-router-dom";
import { tokenUtils } from "../utils/token";
import { useEffect, useState } from "react";
import {
  myBookApi,
  type MyBookResponse,
  type ReadingStatus,
} from "../api/myBookApi";
import { extractErrorMessage } from "../utils/error";

function MyLibraryPage() {
  const [books, setBooks] = useState<MyBookResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchLibrary = async () => {
      try {
        const data = await myBookApi.getMyLibrary();
        setBooks(data);
      } catch (error: unknown) {
        setErrorMessage(
          extractErrorMessage(error, "서재를 불러오지 못하였습니다."),
        );
      } finally {
        setIsLoading(false);
      }
    };

    fetchLibrary();
  }, []);

  const handleLogout = () => {
    tokenUtils.remove();
    navigate("/login");
  };

  const STATUS_LABELS: Record<ReadingStatus, string> = {
    WANT: "읽고 싶은",
    READING: "읽는 중",
    DONE: "완독",
  };

  const statusLabel = (status: ReadingStatus): string => STATUS_LABELS[status];

  return (
    <div className="min-h-screen bg-amber-50 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-amber-900">내 서재</h1>
          <div className="flex gap-2">
            <Link
              to="/search"
              className="px-4 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800 text-sm"
            >
              + 책 찾기
            </Link>
            <button
              onClick={handleLogout}
              className="px-4 py-2 text-sm text-amber-900 hover:bg-amber-100 rounded-lg"
            >
              로그아웃
            </button>
          </div>
        </div>

        {isLoading && (
          <p className="text-amber-700 text-center py-12">불러오는 중...</p>
        )}

        {errorMessage && (
          <p className="text-red-600 text-sm mb-4">{errorMessage}</p>
        )}

        {!isLoading && books.length === 0 && !errorMessage && (
          <div className="bg-white p-8 rounded-xl shadow text-center">
            <p className="text-amber-700 mb-4">아직 담은 책이 없어요</p>
            <Link
              to="/search"
              className="inline-block px-4 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800"
            >
              첫 책 찾으러 가기
            </Link>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {books.map((book) => (
            <div
              key={book.my_book_id}
              className="bg-white p-4 rounded-xl shadow flex gap-4"
            >
              {book.book_thumbnail ? (
                <img
                  src={book.book_thumbnail}
                  alt={book.book_title}
                  className="w-20 h-28 object-cover rounded"
                />
              ) : (
                <div className="w-20 h-28 bg-amber-100 rounded" />
              )}
              <div className="flex-1">
                <h3 className="font-bold text-amber-900 line-clamp-2">
                  {book.book_title}
                </h3>
                <p className="text-xs text-amber-700 mt-2">
                  {statusLabel(book.status)}
                </p>
                {book.rating && (
                  <p className="text-xs text-amber-600 mt-1">
                    {"★".repeat(book.rating)}
                  </p>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default MyLibraryPage;
