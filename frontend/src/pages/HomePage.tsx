import { Link } from "react-router-dom";

function HomePage() {
    return (
        <div className="min-h-screen bg-amber-50 flex items-center justify-center">
            <div className="bg-white p-8 rounded-xl shadow-lg text-center">
                <h1 className="text-4xl font-bold text-amber-900 mb-2">
                    Readcovery
                </h1>
                <p className="text-amber-700 mb-6">read + discovery</p>
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
            </div>
        </div>
    );
}

export default HomePage;
