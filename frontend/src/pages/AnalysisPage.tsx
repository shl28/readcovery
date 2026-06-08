import { useState } from "react";
import { Link } from "react-router-dom";
import { analysisApi, type AnalysisResponse } from "../api/analysisApi";
import { extractErrorMessage } from "../utils/error";
import Navbar from "../components/Navbar";
import { bookApi, type BookSearchResult } from "../api/bookApi";
import { recommendationApi } from "../api/recommendationApi";
import { myBookApi } from "../api/myBookApi";

function AnalysisPage() {
    const [result, setResult] = useState<AnalysisResponse | null>(null);
    const [isAnalyzing, setIsAnalyzing] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [hasTriedOnce, setHasTriedOnce] = useState(false);

    const [recommendations, setRecommendations] = useState<BookSearchResult[]>(
        [],
    );
    const [isLoadingRecs, setIsLoadingRecs] = useState(false);
    const [recError, setRecError] = useState("");
    const [addingIsbn, setAddingIsbn] = useState<string | null>(null);

    const handleAnalyze = async () => {
        setIsAnalyzing(true);
        setErrorMessage("");
        setHasTriedOnce(true);
        setRecommendations([]);
        setRecError("");

        try {
            const data = await analysisApi.analyze();
            setResult(data);
            fetchRecommendations();
        } catch (error: unknown) {
            setErrorMessage(extractErrorMessage(error, "분석에 실패했습니다."));
            setResult(null);
        } finally {
            setIsAnalyzing(false);
        }
    };

    const fetchRecommendations = async () => {
        setIsLoadingRecs(true);
        setRecError("");

        try {
            const data = await recommendationApi.getRecommendations();
            setRecommendations(data);
        } catch (error: unknown) {
            setRecError(
                extractErrorMessage(error, "도서 추천을 불러오지 못했습니다."),
            );
        } finally {
            setIsLoadingRecs(false);
        }
    };

    const handleAddRecommendation = async (book: BookSearchResult) => {
        setAddingIsbn(book.isbn);

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
            setRecommendations(
                recommendations.filter((b) => b.isbn !== book.isbn),
            );
        } catch (error: unknown) {
            setRecError(
                extractErrorMessage(error, "서재 담기에 실패했습니다."),
            );
        } finally {
            setAddingIsbn(null);
        }
    };

    return (
        <div className="min-h-screen bg-amber-50">
            <Navbar />

            <div className="max-w-3xl mx-auto p-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-amber-900 mb-2">
                        내 독서 분석
                    </h1>
                    <p className="text-sm text-amber-700">
                        모은 인용구로 당신의 독서 성향을 분석합니다.
                    </p>
                </div>

                {!hasTriedOnce && !result && (
                    <div className="bg-white p-12 rounded-xl shadow text-center">
                        <p className="text-6xl mb-4">📖</p>
                        <p className="text-amber-900 font-medium mb-2">
                            인용구 3개 이상이 모이면 분석할 수 있어요
                        </p>
                        <p className="text-sm text-amber-700 mb-6">
                            AI가 당신이 좋아하는 문장의 패턴을 읽어냅니다.
                        </p>
                        <button
                            onClick={handleAnalyze}
                            disabled={isAnalyzing}
                            className="px-6 py-3 bg-amber-900 text-white rounded-lg hover:bg-amber-800 disabled:bg-amber-300"
                        >
                            분석 시작
                        </button>
                    </div>
                )}

                {isAnalyzing && (
                    <div className="bg-white p-12 rounded-xl shadow text-center">
                        <div className="inline-block animate-pulse">
                            <p className="text-6xl mb-4">✨</p>
                        </div>
                        <p className="text-amber-900 font-medium mb-2">
                            당신의 인용구를 읽고 있어요
                        </p>
                        <p className="text-sm text-amber-700">
                            잠시만 기다려주세요 (3~10초)
                        </p>
                    </div>
                )}

                {errorMessage && !isAnalyzing && (
                    <div className="bg-white p-8 rounded-xl shadow text-center">
                        <p className="text-red-600 mb-4">{errorMessage}</p>
                        {errorMessage.includes("인용구") && (
                            <p className="text-sm text-amber-700 mb-4">
                                책 상세 페이지에서 인용구를 더 추가해보세요
                            </p>
                        )}
                        <div className="flex gap-2 justify-center">
                            <button
                                onClick={handleAnalyze}
                                disabled={isAnalyzing}
                                className="px-4 py-2 bg-amber-900 text-white rounded-lg hover:bg-amber-800"
                            >
                                다시 시도
                            </button>
                            <Link
                                to="my-library"
                                className="px-4 py-2 bg-white border border-amber-900 text-amber-900 rounded-lg hover:bg-amber-100"
                            >
                                내 서재로
                            </Link>
                        </div>
                    </div>
                )}

                {result && !isAnalyzing && (
                    <div className="space-y-6">
                        <div className="flex justify-between items-center text-xs text-amber-600">
                            <span>인용구 {result.quote_count}개 기중</span>
                            <span>
                                {result.from_cache
                                    ? "이전 분석 결과"
                                    : "새로운 분석 결과"}
                            </span>
                        </div>

                        <div className="bg-white p-6 rounded-xl shadow">
                            <h2 className="text-sm font-medium text-amber-700 mb-3">
                                📝 한 줄 요약
                            </h2>
                            <p className="text-lg text-amber-900 leading-relaxed">
                                {result.summary}
                            </p>
                        </div>

                        <div className="bg-white p-6 rounded-xl shadow">
                            <h2 className="text-sm font-medium text-amber-700 mb-3">
                                🔑 핵심 키워드
                            </h2>
                            <div className="flex flex-wrap gap-2">
                                {result.keywords.map((keyword) => (
                                    <span
                                        key={keyword}
                                        className="px-3 py-1.5 bg-amber-100 text-amber-900 rounded-full text-sm "
                                    >
                                        {keyword}
                                    </span>
                                ))}
                            </div>
                        </div>

                        <div className="bg-white p-6 rounded-lg shadow">
                            <h2 className="text-sm font-medium text-amber-700 mb-3">
                                🌱 독서 성향
                            </h2>
                            <p className="text-amber-900 leading-relaxed whitespace-pre-wrap">
                                {result.personality}
                            </p>
                        </div>

                        <div className="bg-white p-6 rounded-xl shadow">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-sm font-medium text-amber-700">
                                    📚 당신을 위한 추천 책
                                </h2>
                                {recommendations.length === 0 &&
                                    !isLoadingRecs &&
                                    !recError && (
                                        <button
                                            onClick={fetchRecommendations}
                                            className="text-xs text-amber-700 hover:underline"
                                        >
                                            다시 불러오기
                                        </button>
                                    )}
                            </div>

                            {isLoadingRecs && (
                                <p className="text-sm text-amber-700 py-8 text-center">
                                    추천 책을 찾고 있어요...
                                </p>
                            )}

                            {recError && (
                                <p className="text-sm text-red-600 py-4 text-center">
                                    {recError}
                                </p>
                            )}

                            {!isLoadingRecs &&
                                !recError &&
                                recommendations.length === 0 && (
                                    <p className="text-sm text-amber-700 py-4 text-center">
                                        더 추천할 책이 없어요
                                    </p>
                                )}

                            {recommendations.length > 0 && (
                                <div className="space-y-3">
                                    <p className="text-xs text-amber-600 mb-3">
                                        분석된 키워드를 바탕으로 골라봤어요
                                    </p>
                                    {recommendations.map((book) => (
                                        <div
                                            key={book.isbn}
                                            className="flex gap-3 p-3 bg-amber-50 rounded-lg"
                                        >
                                            {book.thumbnail ? (
                                                <img
                                                    src={book.thumbnail}
                                                    alt={book.title}
                                                    className="w-14 h-20 object-cover rounded flex-shrink-0"
                                                    referrerPolicy="no-referrer"
                                                />
                                            ) : (
                                                <div className="w-14 h-20 bg-amber-100 rounded flex-shrink-0" />
                                            )}
                                            <div className="flex-1 min-w-0">
                                                <h3 className="font-medium text-amber-900 text-sm line-clamp-1">
                                                    {book.title}
                                                </h3>
                                                <p className="text-xs text-amber-700 mt-1">
                                                    {book.author}
                                                </p>
                                                <p className="text-xs text-amber-600 mt-1 line-clamp-2">
                                                    {book.contents}
                                                </p>
                                            </div>
                                            <button
                                                onClick={() =>
                                                    handleAddRecommendation(
                                                        book,
                                                    )
                                                }
                                                disabled={
                                                    addingIsbn === book.isbn
                                                }
                                                className="self-start px-3 py-1.5 text-xs bg-amber-900 text-white rounded-lg hover:bg-amber-800 disabled:opacity-50 flex-shrink-0"
                                            >
                                                {addingIsbn === book.isbn
                                                    ? "담는 중..."
                                                    : "서재 담기"}
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        <div className="text-center pt-4">
                            <button
                                onClick={handleAnalyze}
                                disabled={isAnalyzing}
                                className="px-6 py-2 bg-white border border-amber-900 text-amber-900 rounded-lg hover:bg-amber-100 text-sm"
                            >
                                다시 분석하기
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default AnalysisPage;
