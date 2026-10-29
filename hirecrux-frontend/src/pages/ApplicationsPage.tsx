
import { useCallback, useEffect, useMemo, useState } from "react";
import {
    BriefcaseBusiness,
    CalendarDays,
    Search,
    RefreshCw,
    FileText,
    Clock3,
    CheckCircle2,
    XCircle,
    Eye,
    X,
    UserRound,
} from "lucide-react";
import { applicationApi, getApiErrorMessage } from "../lib/api";
import "./ApplicationsPage.css";

interface Application {
    applicationId: number;
    candidateId: number;
    candidateName: string;
    candidateEmail: string;
    jobId: number;
    jobTitle: string;
    applicationStatus: string;
    resumeScore: number;
    appliedAt: string;
}

type ApplicationStatus =
    | "APPLIED"
    | "SHORTLISTED"
    | "INTERVIEW_SCHEDULED"
    | "SELECTED"
    | "HIRED"
    | "REJECTED";

const statuses: ApplicationStatus[] = [
    "APPLIED",
    "SHORTLISTED",
    "INTERVIEW_SCHEDULED",
    "SELECTED",
    "HIRED",
    "REJECTED",
];

function readRole(): string {
    try {
        const user = JSON.parse(localStorage.getItem("hirecrux_user") || "{}");
        return String(user.role || "").replace(/^ROLE_/, "").toUpperCase();
    } catch {
        return "";
    }
}

function formatLabel(value?: string) {
    return value
        ? value.toLowerCase().split("_")
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
            .join(" ")
        : "Not available";
}

function formatDate(value?: string) {
    if (!value) return "Not available";
    const date = new Date(value);

    return Number.isNaN(date.getTime())
        ? "Not available"
        : date.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
}

export default function ApplicationsPage() {
    const role = readRole();
    const isHR = role === "HR";

    const [applications, setApplications] = useState<Application[]>([]);
    const [selectedApplication, setSelectedApplication] =
        useState<Application | null>(null);
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [jobId, setJobId] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [actionError, setActionError] = useState("");
    const [editStatus, setEditStatus] =
        useState<ApplicationStatus>("APPLIED");

    const loadApplications = useCallback(async (filterJobId?: number) => {
        setLoading(true);
        setError("");

        try {
            let response;

            if (isHR) {
                response =
                    filterJobId !== undefined
                        ? await applicationApi.getByJob(filterJobId)
                        : await applicationApi.getAll();
            } else {
                response = await applicationApi.getMyApplications();
            }

            setApplications(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError(getApiErrorMessage(err).message);
            setApplications([]);
        } finally {
            setLoading(false);
        }
    }, [isHR]);

    useEffect(() => {
        void loadApplications();
    }, [loadApplications]);

    const filteredApplications = useMemo(() => {
        const query = search.trim().toLowerCase();

        return applications.filter((item) => {
            const matchesSearch =
                String(item.applicationId).includes(query) ||
                String(item.jobId).includes(query) ||
                (item.jobTitle || "").toLowerCase().includes(query) ||
                (item.candidateName || "").toLowerCase().includes(query) ||
                (item.candidateEmail || "").toLowerCase().includes(query);

            const matchesStatus =
                statusFilter === "ALL" || item.applicationStatus === statusFilter;

            return matchesSearch && matchesStatus;
        });
    }, [applications, search, statusFilter]);

    async function openApplication(item: Application) {
        setActionError("");

        // The candidate's list already contains their application details.
        // HR can load the complete record using the get-by-ID endpoint.
        if (isHR) {
            try {
                const response = await applicationApi.getById(item.applicationId);
                setSelectedApplication(response.data);
                setEditStatus(response.data.applicationStatus as ApplicationStatus);
            } catch (err) {
                setActionError(getApiErrorMessage(err).message);
            }
        } else {
            setSelectedApplication(item);
        }
    }

    async function updateApplication() {
        if (!selectedApplication || !isHR) return;

        setSaving(true);
        setActionError("");

        try {
            await applicationApi.updateStatus(selectedApplication.applicationId, {
                applicationStatus: editStatus,
            });

            const updated = {
                ...selectedApplication,
                applicationStatus: editStatus,
            };

            setSelectedApplication(updated);
            await loadApplications(
                jobId.trim() ? Number(jobId) : undefined
            );
        } catch (err) {
            setActionError(getApiErrorMessage(err).message);
        } finally {
            setSaving(false);
        }
    }

    async function filterByJob() {
        if (!isHR) return;

        if (!jobId.trim()) {
            await loadApplications();
            return;
        }

        const id = Number(jobId);

        if (!Number.isInteger(id) || id <= 0) {
            setError("Enter a valid positive job ID.");
            return;
        }

        await loadApplications(id);
    }

    async function clearJobFilter() {
        setJobId("");
        await loadApplications();
    }

    const shortlistedCount = applications.filter(
        (item) => item.applicationStatus === "SHORTLISTED"
    ).length;

    const rejectedCount = applications.filter(
        (item) => item.applicationStatus === "REJECTED"
    ).length;

    return (
        <main className="applications-page">
            <header className="applications-header">
                <div>
                    <div className="applications-eyebrow">HIRING PIPELINE</div>
                    <h1>{isHR ? "Applications Management" : "My Applications"}</h1>
                    <p>
                        {isHR
                            ? "Review candidates, filter by job, and manage application statuses."
                            : "Track your applications and their latest status."}
                    </p>
                </div>

                <button
                    className="applications-refresh-button"
                    type="button"
                    disabled={loading}
                    onClick={() =>
                        void loadApplications(jobId.trim() ? Number(jobId) : undefined)
                    }
                >
                    <RefreshCw size={16} />
                    Refresh
                </button>
            </header>

            <section className="application-stats">
                <article className="application-stat-card">
                    <div className="application-stat-icon purple">
                        <FileText size={20} />
                    </div>
                    <div>
                        <span>Total applications</span>
                        <strong>{applications.length}</strong>
                    </div>
                </article>

                <article className="application-stat-card">
                    <div className="application-stat-icon blue">
                        <Clock3 size={20} />
                    </div>
                    <div>
                        <span>In progress</span>
                        <strong>
                            {applications.filter((item) =>
                                ["APPLIED", "SHORTLISTED", "INTERVIEW_SCHEDULED"].includes(
                                    item.applicationStatus
                                )
                            ).length}
                        </strong>
                    </div>
                </article>

                <article className="application-stat-card">
                    <div className="application-stat-icon green">
                        <CheckCircle2 size={20} />
                    </div>
                    <div>
                        <span>Shortlisted</span>
                        <strong>{shortlistedCount}</strong>
                    </div>
                </article>

                <article className="application-stat-card">
                    <div className="application-stat-icon red">
                        <XCircle size={20} />
                    </div>
                    <div>
                        <span>Rejected</span>
                        <strong>{rejectedCount}</strong>
                    </div>
                </article>
            </section>

            <section className="applications-content">
                <div className="applications-toolbar">
                    <div className="applications-search">
                        <Search size={18} />
                        <input
                            value={search}
                            onChange={(event) => setSearch(event.target.value)}
                            placeholder="Search job, candidate, email, or application ID..."
                        />
                    </div>

                    <select
                        value={statusFilter}
                        onChange={(event) => setStatusFilter(event.target.value)}
                        aria-label="Filter by application status"
                    >
                        <option value="ALL">All statuses</option>
                        {statuses.map((status) => (
                            <option key={status} value={status}>
                                {formatLabel(status)}
                            </option>
                        ))}
                    </select>
                </div>

                {isHR && (
                    <div className="applications-job-filter">
                        <label htmlFor="application-job-id">Applications for job ID</label>
                        <input
                            id="application-job-id"
                            type="number"
                            min="1"
                            value={jobId}
                            onChange={(event) => setJobId(event.target.value)}
                            placeholder="Enter job ID"
                        />
                        <button
                            className="applications-primary-button"
                            type="button"
                            disabled={loading}
                            onClick={() => void filterByJob()}
                        >
                            Filter job
                        </button>
                        <button
                            className="applications-refresh-button"
                            type="button"
                            disabled={loading}
                            onClick={() => void clearJobFilter()}
                        >
                            Show all
                        </button>
                    </div>
                )}

                {error && (
                    <div className="applications-error" role="alert">
                        {error}
                        <button type="button" onClick={() => setError("")}>
                            Dismiss
                        </button>
                    </div>
                )}

                {loading ? (
                    <div className="applications-state">
                        <div className="applications-loader" />
                        <p>Loading applications...</p>
                    </div>
                ) : filteredApplications.length === 0 ? (
                    <div className="applications-state">
                        <BriefcaseBusiness size={32} />
                        <h3>No applications found</h3>
                        <p>
                            {isHR
                                ? "Try another job ID or clear the filters."
                                : "Applications you submit will appear here."}
                        </p>
                    </div>
                ) : (
                    <div className="applications-list">
                        {filteredApplications.map((item) => (
                            <article className="application-card" key={item.applicationId}>
                                <div className="application-card-main">
                                    <div className="application-job-icon">
                                        <BriefcaseBusiness size={22} />
                                    </div>

                                    <div className="application-job-details">
                                        <h3>{item.jobTitle || `Job #${item.jobId}`}</h3>
                                        <span className="application-reference">
                      Application #{item.applicationId} · Job #{item.jobId}
                    </span>

                                        {isHR && (
                                            <p className="application-candidate">
                                                <UserRound size={15} />
                                                {item.candidateName} · {item.candidateEmail}
                                            </p>
                                        )}

                                        <div className="application-date">
                                            <CalendarDays size={14} />
                                            Applied {formatDate(item.appliedAt)}
                                        </div>
                                    </div>
                                </div>

                                <div className="application-card-footer">
                  <span
                      className={`application-status ${item.applicationStatus
                          ?.toLowerCase()
                          .replaceAll("_", "-")}`}
                  >
                    {formatLabel(item.applicationStatus)}
                  </span>

                                    <button
                                        className="applications-primary-button"
                                        type="button"
                                        onClick={() => void openApplication(item)}
                                    >
                                        <Eye size={16} />
                                        View details
                                    </button>
                                </div>
                            </article>
                        ))}
                    </div>
                )}

                {!loading && filteredApplications.length > 0 && (
                    <div className="applications-results-count">
                        Showing {filteredApplications.length} of {applications.length} applications
                    </div>
                )}
            </section>

            {selectedApplication && (
                <div
                    className="application-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            setSelectedApplication(null);
                            setActionError("");
                        }
                    }}
                >
                    <section
                        className="application-detail-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="application-detail-title"
                    >
                        <header className="application-detail-header">
                            <div>
                                <span className="applications-eyebrow">APPLICATION DETAILS</span>
                                <h2 id="application-detail-title">
                                    Application #{selectedApplication.applicationId}
                                </h2>
                            </div>
                            <button
                                type="button"
                                className="application-modal-close"
                                aria-label="Close details"
                                onClick={() => {
                                    setSelectedApplication(null);
                                    setActionError("");
                                }}
                            >
                                <X size={20} />
                            </button>
                        </header>

                        <div className="application-detail-grid">
                            <div>
                                <span>Candidate name</span>
                                <strong>{selectedApplication.candidateName || "Not available"}</strong>
                            </div>
                            <div>
                                <span>Candidate email</span>
                                <strong>{selectedApplication.candidateEmail || "Not available"}</strong>
                            </div>
                            <div>
                                <span>Candidate ID</span>
                                <strong>{selectedApplication.candidateId}</strong>
                            </div>
                            <div>
                                <span>Job title</span>
                                <strong>{selectedApplication.jobTitle || "Not available"}</strong>
                            </div>
                            <div>
                                <span>Job ID</span>
                                <strong>{selectedApplication.jobId}</strong>
                            </div>
                            <div>
                                <span>Applied date</span>
                                <strong>{formatDate(selectedApplication.appliedAt)}</strong>
                            </div>
                            <div>
                                <span>Resume score</span>
                                <strong>{selectedApplication.resumeScore ?? 0}</strong>
                            </div>
                            <div>
                                <span>Current status</span>
                                <strong>{formatLabel(selectedApplication.applicationStatus)}</strong>
                            </div>
                        </div>

                        {isHR && (
                            <div className="application-update-status">
                                <label htmlFor="application-status">Update application status</label>
                                <select
                                    id="application-status"
                                    value={editStatus}
                                    onChange={(event) =>
                                        setEditStatus(event.target.value as ApplicationStatus)
                                    }
                                >
                                    {statuses.map((status) => (
                                        <option key={status} value={status}>
                                            {formatLabel(status)}
                                        </option>
                                    ))}
                                </select>

                                {actionError && (
                                    <p className="applications-error" role="alert">
                                        {actionError}
                                    </p>
                                )}

                                <button
                                    className="applications-primary-button"
                                    type="button"
                                    disabled={saving}
                                    onClick={() => void updateApplication()}
                                >
                                    {saving ? "Saving..." : "Save status"}
                                </button>
                            </div>
                        )}

                        {!isHR && actionError && (
                            <p className="applications-error" role="alert">{actionError}</p>
                        )}
                    </section>
                </div>
            )}
        </main>
    );
}
