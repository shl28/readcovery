import { Navigate } from "react-router-dom";
import { tokenUtils } from "../utils/token";

interface PrivateRouteProps {
  children: React.ReactNode;
}

function PrivateRoute({ children }: PrivateRouteProps) {
  if (!tokenUtils.exists()) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

export default PrivateRoute;
