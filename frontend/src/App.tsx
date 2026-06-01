import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import PrivateRoute from "./components/PrivateRoute";
import MyLibraryPage from "./pages/MyLibraryPage";
import SearchPage from "./pages/SearchPage";
import MyBookDetailPage from "./pages/MyBookDetailPage";

function App() {
    return (
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route
                path="/my-library"
                element={
                    <PrivateRoute>
                        <MyLibraryPage />
                    </PrivateRoute>
                }
            />
            <Route
                path="/search"
                element={
                    <PrivateRoute>
                        <SearchPage />
                    </PrivateRoute>
                }
            />
            <Route
                path="/my-books/:myBookId"
                element={
                    <PrivateRoute>
                        <MyBookDetailPage />
                    </PrivateRoute>
                }
            />
        </Routes>
    );
}

export default App;
