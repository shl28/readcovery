import { useState } from "react";
import { analysisApi, type AnalysisResponse } from "../api/analysisApi";
import { extractErrorMessage } from "../utils/error";
import Navbar from "../components/Navbar";

function AnalysisPage() {
    const [result, setResult] = useState<AnalysisResponse | null>(null);
    const [isAnalyzing, setIsAnalyzing] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [hasTriedOnce, setHasTriedOnce] = useState(false);

    const handleAnalyze = async () => {
        setIsAnalyzing(true);
        setErrorMessage("");
        setHasTriedOnce(true);

        try {
            const data = await analysisApi.analyze();
            setResult(data);
        } catch (error: unknown) {
            setErrorMessage(extractErrorMessage(error, "분석에 실패했습니다."));
            setResult(null);
        } finally {
            setIsAnalyzing(false);
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
            </div>
        </div>
    );
}

export default AnalysisPage;
