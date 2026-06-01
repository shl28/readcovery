import { useNavigate, useParams, Link } from "react-router-dom";
import {
    myBookApi,
    type MyBookResponse,
    type ReadingStatus,
} from "../api/myBookApi";
import { useEffect, useState } from "react";
import { quoteApi, type QuoteResponse } from "../api/quoteApi";
import { extractErrorMessage } from "../utils/error";

const STATUS_LABELS: Record<ReadingStatus, string> = {
    WANT: "읽고 싶은",
    READING: "읽는 중",
    DONE: "완독",
};

function MyBookDetailPage() {
    const { myBookId } = useParams<{ myBookId: string }>();
    const navigate = useNavigate();

    const [myBook, setMyBook] = useState<MyBookResponse | null>(null);
    const [quotes, setQuotes] = useState<QuoteResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    const [newContent, setNewContent] = useState("");
    const [newPage, setNewPage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!myBookId) return;

        const numericId = Number(myBookId);

        const fetchData = async () => {
            try {
                const [myBookData, quoteList] = await Promise.all([
                    myBookApi.getOne(numericId),
                    quoteApi.getByMyBook(numericId),
                ]);
                setMyBook(myBookData);
                setQuotes(quoteList);
            } catch (error: unknown) {
                setErrorMessage(
                    extractErrorMessage(error, "데이터를 불러오지 못했습니다."),
                );
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [myBookId]);

    const handelAddQuote: React.SubmitEventHandler<HTMLFormElement> = async (
        e,
    ) => {
        e.preventDefault();
        if (!myBookId || !newContent.trim()) return;

        setIsSubmitting(true);
        setErrorMessage("");

        try {
            const created = await quoteApi.create({
                my_book_id: Number(myBookId),
                content: newContent.trim(),
                page: newPage.trim() ? Number(newPage) : null,
            });
            setQuotes([created, ...quotes]);
            setNewContent("");
            setNewPage("");
        } catch (error: unknown) {
            setErrorMessage(
                extractErrorMessage(error, "인용구 추가에 실패했습니다."),
            );
        } finally {
            setIsSubmitting(false);
        }
    };

    const hendleDeleteQuote = async (quoteId: number) => {
        if (!confirm("이 인용구를 삭제하시겠습니까?")) return;

        try {
            await quoteApi.delete(quoteId);
            setQuotes(quotes.filter((q) => q.quote_id !== quoteId));
        } catch (error: unknown) {
            setErrorMessage(
                extractErrorMessage(error, "인용구 삭제에 실패했습니다."),
            );
        }
    };

    if (isLoading) {
        return (
            <div className="min-h-screen bg-amber-50 flex items-center justify-center">
                <p className="text-amber-700">불러오는 중...</p>
            </div>
        );
    }

    if (!myBook) {
        return (
            <div className="min-h-screen bg-amber-50 flex items-center justify-center">
                <div className="bg-white p-8 rounded-xl shadow text-center">
                    <p className="text-red-600 mb-4">{errorMessage}</p>
                    <button
                        onClick={() => navigate("/my-library")}
                        className="px-4 py-2 bg-amber-900 text-white rounded-lg"
                    >
                        내 서재로
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-amber-50 p-8">
            <div className="max-w-3xl mx-auto">
                <Link
                    to="/my-library"
                    className="text-amber-900 hover:underline text-sm"
                >
                    ← 내 서재로
                </Link>

                <div className="bg-white p-6 rounded-xl shadow mt-4 mb-6 flex gap-6">
                    {myBook.book_thumbnail ? (
                        <img
                            src={myBook.book_thumbnail}
                            alt={myBook.book_title}
                            className="w-24 h-32 object-cover rounded"
                        />
                    ) : (
                        <div className="w-24 h-32 bg-amber-100 rounded" />
                    )}
                    <div className="flex-1">
                        <h1 className="text-2xl font-bold text-amber-900">
                            {myBook.book_title}
                        </h1>
                        <p className="text-sm text-amber-700 mt-2">
                            {STATUS_LABELS[myBook.status]}
                        </p>
                        {myBook.rating && (
                            <p className="text-sm text-amber-600 mt-1">
                                {"★".repeat(myBook.rating)}
                            </p>
                        )}
                    </div>
                </div>

                <div className="bg-white p-6 rounded-xl shadow mb-6">
                    <h2 className="text-lg font-bold text-amber-900 mb-4">
                        인용구 추가
                    </h2>
                    <form onSubmit={handelAddQuote} className="space-y-3">
                        <textarea
                            value={newContent}
                            onChange={(e) => setNewContent(e.target.value)}
                            placeholder="마음에 남는 문장을 적어주세요."
                            required
                            rows={3}
                            className="w-full px-3 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-500 resize-none"
                        />
                        <div className="flex gap-2">
                            <input
                                type="number"
                                value={newPage}
                                onChange={(e) => setNewPage(e.target.value)}
                                placeholder="페이지 (선택)"
                                min="1"
                                className="w-32 px-3 py-2 border border-amber-200 rounded-lg focus:outline-none focus:border-amber-500"
                            />
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="flex-1 px-4 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800 disabled:bg-amber-300"
                            >
                                {isSubmitting ? "추가 중..." : "인용구 추가"}
                            </button>
                        </div>
                    </form>
                </div>

                {errorMessage && (
                    <p className="text-red-600 text-sm mb-4">{errorMessage}</p>
                )}

                <div className="space-y-3">
                    <h2 className="text-lg font-bold text-amber-900">
                        인용구 ({quotes.length})
                    </h2>
                    {quotes.length === 0 ? (
                        <p className="text-amber-700 text-center py-8 bg-white rounded-xl">
                            아직 등록한 인용구가 없어요
                        </p>
                    ) : (
                        quotes.map((quote) => (
                            <div
                                key={quote.quote_id}
                                className="bg-white p-5 rounded-xl shadow"
                            >
                                <p className="text-amber-900 whitespace-pre-wrap">
                                    "{quote.content}"
                                </p>
                                <div className="flex justify-between items-center mt-3">
                                    <p className="text-xs text-amber-600">
                                        {quote.page ? `p.${quote.page}` : ""}
                                    </p>
                                    <button
                                        onClick={() =>
                                            hendleDeleteQuote(quote.quote_id)
                                        }
                                        className="text-xs text-amber-700 hover:text-red-600"
                                    >
                                        삭제
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}

export default MyBookDetailPage;
