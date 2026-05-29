import { useState } from "react";
import { bookApi, type BookSearchResult } from "../api/bookApi";
import { Link, useNavigate } from "react-router-dom";
import { extractErrorMessage } from "../utils/error";
import { myBookApi } from "../api/myBookApi";

function SearchPage() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<BookSearchResult[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [addingIsbn, setAddingIsbn] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSearch: React.SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;

    setIsSearching(true);
    setErrorMessage("");

    try {
      const data = await bookApi.search(query);
      setResults(data);
      setHasSearched(true);
    } catch (error: unknown) {
      setErrorMessage(extractErrorMessage(error, "검색에 실패했습니다."));
    } finally {
      setIsSearching(false);
    }
  };

  const handleAddToLibrary = async (book: BookSearchResult) => {
    setAddingIsbn(book.isbn);
    setErrorMessage("");

    try {
      const registered = await bookApi.register({
        isbn: book.isbn,
        title: book.title,
        author: book.author,
        publisher: book.publisher,
        thumbnail: book.thumbnail,
        contents: book.contents,
      });

      await myBookApi.addToLibrary({
        book_id: registered.id,
        status: "WANT",
      });

      alert(`"${book.title}"을(를) 서재에 담았습니다.`);
      navigate("/my-library");
    } catch (error: unknown) {
      setErrorMessage(extractErrorMessage(error, "서재 담기에 실패했습니다."));
    } finally {
      setAddingIsbn(null);
    }
  };

  return (
    <div className="min-h-screen bg-amber-50 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <Link to="/my-library" className="text-amber-900 hover:underline">
            ← 내 서재로
          </Link>
          <h1 className="text-3xl font-bold text-amber-900">책 검색</h1>
          <div className="w-20"></div>
        </div>

        <form onSubmit={handleSearch} className="flex gap-2 mb-6">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="책 제목, 저자, 출판사 등"
            className="flex-1 px-4 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-50"
          />
          <button
            type="submit"
            disabled={isSearching}
            className="px-6 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800 disabled:bg-amber-300"
          >
            {isSearching ? "검색 중..." : "검색"}
          </button>
        </form>

        {errorMessage && (
          <p className="text-red-600 text-sm mb-4">{errorMessage}</p>
        )}

        {hasSearched && results.length === 0 && !isSearching && (
          <p className="text-amber-700 text-center py-12">
            검색 결과가 없습니다.
          </p>
        )}

        <div className="space-y-4">
          {results.map((book) => (
            <div
              key={book.isbn || book.title}
              className="bg-white p-4 rounded-xl shadow flex gap-4"
            >
              {book.thumbnail ? (
                <img
                  src={book.thumbnail}
                  alt={book.title}
                  className="w-20 h-28 object-cover rounded"
                />
              ) : (
                <div className="w-20 h-28 bg-amber-100 rounded flex items-center justify-center text-amber-400 text-xs">
                  No Image
                </div>
              )}

              <div className="flex-1">
                <h3 className="font-bold text-amber-900">{book.title}</h3>
                <p className="text-sm text-amber-700 mt-1">
                  {book.author} · {book.publisher}
                </p>
                <p className="text-xs text-amber-600 mt-2 line-clamp-2">
                  {book.contents}
                </p>
              </div>

              <button
                onClick={() => handleAddToLibrary(book)}
                disabled={addingIsbn === book.isbn}
                className="self-start px-3 py-1.5 text-sm bg-amber-100 text-amber-900 rounded-lg hover:bg-amber-200 disabled:opacity-50"
              >
                {addingIsbn === book.isbn ? "담는 중..." : "서재 담기"}
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default SearchPage;
