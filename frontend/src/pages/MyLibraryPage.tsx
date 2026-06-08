import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import {
    myBookApi,
    type MyBookResponse,
    type ReadingStatus,
} from "../api/myBookApi";
import { extractErrorMessage } from "../utils/error";
import Navbar from "../components/Navbar";

const STATUS_LABELS: Record<ReadingStatus, string> = {
    WANT: "읽고 싶은",
    READING: "읽는 중",
    DONE: "완독",
};

function MyLibraryPage() {
    const [books, setBooks] = useState<MyBookResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [deletingId, setDeletingId] = useState<number | null>(null);

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

    const handleDelete = async (
        e: React.MouseEvent,
        myBookId: number,
        bookTitle: string,
    ) => {
        e.preventDefault();
        e.stopPropagation();

        if (
            !confirm(
                `"${bookTitle}"을(를) 서재에서 삭제하시겠습니까?\n\n등록된 인용구도 함께 삭제됩니다.`,
            )
        ) {
            return;
        }

        setDeletingId(myBookId);
        try {
            await myBookApi.delete(myBookId);
            setBooks(books.filter((b) => b.my_book_id !== myBookId));
        } catch (error: unknown) {
            setErrorMessage(extractErrorMessage(error, "삭제에 실패했습니다."));
        } finally {
            setDeletingId(null);
        }
    };

    const statusLabel = (status: ReadingStatus): string =>
        STATUS_LABELS[status];

    return (
        <div className="min-h-screen bg-amber-50">
            <Navbar />

            <div className="max-w-5xl mx-auto p-8">
                <h1 className="text-3xl font-bold text-amber-900">내 서재</h1>

                {isLoading && (
                    <p className="text-amber-700 text-center py-12">
                        불러오는 중...
                    </p>
                )}

                {errorMessage && (
                    <p className="text-red-600 text-sm mb-4">{errorMessage}</p>
                )}

                {!isLoading && books.length === 0 && !errorMessage && (
                    <div className="bg-white p-8 rounded-xl shadow text-center">
                        <p className="text-amber-700 mb-4">
                            아직 담은 책이 없어요
                        </p>
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
                        <Link
                            key={book.my_book_id}
                            to={`/my-books/${book.my_book_id}`}
                            className="relative bg-white p-4 rounded-xl shadow flex gap-4 hover:shadow-lg transition-shadow"
                        >
                            <button
                                onClick={(e) =>
                                    handleDelete(
                                        e,
                                        book.my_book_id,
                                        book.book_title,
                                    )
                                }
                                disabled={deletingId === book.my_book_id}
                                aria-label={`${book.book_title} 삭제`}
                                className="absolute top-2 right-2 w-6 h-6 flex items-center justify-center text-amber-300 hover:text-amber-700 hover:bg-amber-50 rounded-full text-sm transition-colors disabled:opacity-30"
                            >
                                ✕
                            </button>
                            {book.book_thumbnail ? (
                                <img
                                    src={book.book_thumbnail}
                                    alt={book.book_title}
                                    className="w-20 h-28 object-cover rounded"
                                    referrerPolicy="no-referrer"
                                />
                            ) : (
                                <div className="w-20 h-28 bg-amber-100 rounded" />
                            )}
                            <div className="flex-1">
                                <h3 className="font-bold text-amber-900 line-clamp-2 pr-6">
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
                        </Link>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default MyLibraryPage;
