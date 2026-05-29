import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";
import axios from "axios";

function SignupPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [nickname, setNickname] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit: React.SubmitEventHandler<HTMLFormElement> = async (
        e,
    ) => {
        e.preventDefault();
        setErrorMessage("");
        setIsLoading(true);

        try {
            await userApi.signup({ email, password, nickname });
            alert("회원가입 성공! 로그인 페이지로 이동합니다.");
            navigate("/login");
        } catch (error: unknown) {
            const message =
                axios.isAxiosError(error) && error.response?.data?.message
                    ? error.response.data.message
                    : "회원가입에 실패했습니다.";
            setErrorMessage(message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-amber-50 flex items-center justify-center">
            <h1 className="text-2xl text-amber-900">회원가입 페이지</h1>
        </div>
    );
}

export default SignupPage;
