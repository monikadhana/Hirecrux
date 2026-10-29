
import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Attach JWT to every request when logged in.
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("hirecrux_token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

// Authentication
export const authApi = {
    login: (data: {
        email: string;
        password: string;
    }) => api.post("/api/users/login", data),

    register: (data: {
        fullName: string;
        email: string;
        password: string;
        phone: string;
    }) => api.post("/api/users/register", data),
};

// Jobs
export const jobApi = {
    getAll: () => api.get("/api/job/all"),
    getById: (jobId: number) => api.get(`/api/job/${jobId}`),
    create: (data: unknown) => api.post("/api/job/create", data),
    update: (jobId: number, data: unknown) =>
        api.put(`/api/job/update/${jobId}`, data),
    close: (jobId: number) => api.put(`/api/job/close/${jobId}`),
    search: (params: {
        title?: string;
        location?: string;
        experienceRequired?: string;
    }) => api.get("/api/job/searchJob", { params }),
};

// Candidate profile
export const candidateApi = {
    create: (data: unknown) =>
        api.post("/api/candidate-profile/create", data),

    getMyProfile: () =>
        api.get("/api/candidate-profile/myProfile"),

    updateMyProfile: (data: unknown) =>
        api.put("/api/candidate-profile/updateProfile", data),
};

// Applications
// Applications
export const applicationApi = {
    create: (data: { jobId: number }) =>
        api.post("/api/application/createApplication", data),

    getById: (id: number) =>
        api.get(`/api/application/${id}`),

    updateStatus: (id: number, data: unknown) =>
        api.put(`/api/application/update/${id}`, data),

    getByJob: (jobId: number) =>
        api.get(`/api/application/job/${jobId}`),

    getMyApplications: () =>
        api.get("/api/application/myApplications"),

    getAll: () =>
        api.get("/api/application/all"),
};


export const interviewApi = {
    schedule: (data: {
        applicationId: number;
        interviewerId: number;
        interviewAt: string;
        interviewMode: "ONLINE" | "OFFLINE" | "PHONE";
        interviewRound: "HR" | "TECHNICAL" | "MANAGER" | "FINAL";
        meetingLink?: string;
        location?: string;
        notes?: string;
    }) => api.post("/api/interview/schedule-interview", data),

    getById: (interviewId: number) =>
        api.get(`/api/interview/${interviewId}`),

    getMine: () =>
        api.get("/api/interview/getMyInterviews"),

    accept: (interviewId: number) =>
        api.put(`/api/interview/acceptInterview/${interviewId}`),

    reject: (interviewId: number) =>
        api.put(`/api/interview/rejectInterview/${interviewId}`),

    complete: (
        interviewId: number,
        data: { interviewResult: "PASSED" | "FAILED" | "ON_HOLD" }
    ) =>
        api.put(
            `/api/interview/completeInterview/${interviewId}`,
            data
        ),

    cancel: (interviewId: number) =>
        api.put(`/api/interview/cancelInterview/${interviewId}`),

    getDashboard: () =>
        api.get("/api/interview/dashboard"),
};


// Notifications
export const notificationApi = {
    getMine: () => api.get("/api/notification/myNotification"),

    markAsRead: (id: number) =>
        api.put(`/api/notification/markAsRead/${id}`),

    create: (data: unknown) =>
        api.post("/api/notification/create-notification", data),
};

export function getApiErrorMessage(error: unknown): {
    message: string;
    status?: number;
} {
    if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const data = error.response?.data as
            | {
            message?: string;
            error?: string;
            errors?: unknown;
        }
            | string
            | undefined;

        let message = "";

        if (typeof data === "string") {
            message = data;
        } else if (data && typeof data === "object") {
            if (typeof data.message === "string" && data.message.trim()) {
                message = data.message;
            } else if (typeof data.error === "string" && data.error.trim()) {
                message = data.error;
            } else if (data.errors) {
                message =
                    typeof data.errors === "string"
                        ? data.errors
                        : JSON.stringify(data.errors);
            }
        }

        if (!message && status === 404) {
            message = "The requested resource was not found.";
        } else if (!message && status === 401) {
            message = "Your session is invalid or has expired. Please sign in again.";
        } else if (!message && status === 403) {
            message = "You don't have permission to perform this action.";
        } else if (!message && status === 400) {
            message = "The request contains invalid data. Please check your inputs.";
        } else if (!message && status === 500) {
            message = "A server error occurred. Please try again later.";
        } else if (!message && error.request) {
            message = "Unable to reach the server. Check your connection and try again.";
        } else if (!message) {
            message = error.message || "An unexpected error occurred.";
        }

        return { message, status };
    }

    return {
        message: error instanceof Error ? error.message : "An unexpected error occurred.",
    };
}

api.interceptors.response.use(
    (response) => response,
    (error: unknown) => {
        const detail = getApiErrorMessage(error);

        window.dispatchEvent(
            new CustomEvent("hirecrux:api-error", { detail })
        );

        return Promise.reject(error);
    }
);

export default api;
