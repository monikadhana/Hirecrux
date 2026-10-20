
import { useEffect, useState } from "react";
import { AlertCircle, X } from "lucide-react";
import "./ApiErrorPopup.css";

interface ApiErrorDetail {
    message: string;
    status?: number;
}

export default function ApiErrorPopup() {
    const [error, setError] = useState<ApiErrorDetail | null>(null);

    useEffect(() => {
        let dismissTimer: ReturnType<typeof setTimeout>;

        const handleApiError = (event: Event) => {
            const customEvent = event as CustomEvent<ApiErrorDetail>;

            setError(customEvent.detail);

            clearTimeout(dismissTimer);
            dismissTimer = setTimeout(() => {
                setError(null);
            }, 6000);
        };

        window.addEventListener("hirecrux:api-error", handleApiError);

        return () => {
            window.removeEventListener("hirecrux:api-error", handleApiError);
            clearTimeout(dismissTimer);
        };
    }, []);

    if (!error) return null;

    return (
        <div className="api-error-popup" role="alert" aria-live="assertive">
            <div className="api-error-icon">
                <AlertCircle size={22} />
            </div>

            <div className="api-error-content">
                <strong>
                    {error.status ? `Request failed (${error.status})` : "Something went wrong"}
                </strong>
                <p>{error.message}</p>
            </div>

            <button
                type="button"
                className="api-error-close"
                aria-label="Dismiss error"
                onClick={() => setError(null)}
            >
                <X size={18} />
            </button>
        </div>
    );
}
