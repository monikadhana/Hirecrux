
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./components/AppLayout";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ModulePlaceholder from "./pages/ModulePlaceholder";
import RegisterPage from "./pages/RegisterPage";
import JobsPage from "./pages/JobsPage";
import ApiErrorPopup from "./components/ApiErrorPopup";
import CandidateProfilePage from "./pages/CandidateProfilePage";

export default function App() {
  return (
      <BrowserRouter>
        <ApiErrorPopup />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/jobs" element={<JobsPage />} />
              <Route path="/applications" element={<ModulePlaceholder />} />
              <Route path="/assessments" element={<ModulePlaceholder />} />
              <Route path="/profile" element={<CandidateProfilePage />} />              <Route path="/notifications" element={<ModulePlaceholder />} />
            </Route>
          </Route>

          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
  );
}
