import { Link, useLocation, useNavigate } from "react-router-dom";
import { tokenUtils } from "../utils/token";

interface NavItem {
    label: string;
    path: string;
}

const NAV_ITEMS: NavItem[] = [
    { label: "내 서재", path: "/my-library" },
    { label: "책 찾기", path: "/search" },
    { label: "내 분석", path: "/analysis" },
];

function Navbar() {
    const location = useLocation();
    const navigate = useNavigate();

    const handleLogout = () => {
        tokenUtils.remove();
        navigate("/login");
    };

    const isActive = (path: string) => location.pathname === path;

    return (
        <nav className="bg-white border-b border-amber-100">
            <div className="max-w-5xl mx-auto px-4 py-3 flex justify-between items-center">
                <Link
                    to="/my-library"
                    className="text-xl font-bold text-amber-900"
                >
                    Readcovery
                </Link>

                <div className="flex items-center gap-1">
                    {NAV_ITEMS.map((item) => (
                        <Link
                            key={item.path}
                            to={item.path}
                            className={`px-3 py-1.5 rounded-lg text-sm transition-colors ${
                                isActive(item.path)
                                    ? "bg-amber-900 text-white"
                                    : "text-amber-900 hover:bg-amber-100"
                            }`}
                        >
                            {item.label}
                        </Link>
                    ))}
                    <button
                        onClick={handleLogout}
                        className="ml-2 px-3 py-1.5 text-sm text-amber-700 hover:bg-amber-100 rounded-lg"
                    >
                        로그아웃
                    </button>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
