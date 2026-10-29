
import { useCallback, useEffect, useMemo, useState } from "react";
import {
    CalendarDays,
    Clock3,
    Video,
    MapPin,
    Phone,
    Plus,
    Search,
    RefreshCw,
    CheckCircle2,
    XCircle,
    ClipboardCheck,
    CalendarClock,
    Users,
} from "lucide-react";
import { interviewApi, getApiErrorMessage } from "../lib/api";
import "./InterviewsPage.css";

type InterviewMode = "ONLINE" | "OFFLINE" | "PHONE";
type InterviewRound = "HR" | "TECHNICAL" | "MANAGER" | "FINAL";
type InterviewStatus =
    | "SCHEDULED"
    | "ACCEPTED"
    | "REJECTED"
    | "COMPLETED"
    | "CANCELLED"
    | "NO_SHOW";
type InterviewResult = "PENDING" | "PASSED" | "FAILED" | "ON_HOLD";

interface Interview {
    interviewId: number;
    applicationId: number;
    candidateId: number;
    candidateName: string;
    candidateEmail: string;
    hrId: number;
    interviewerId: number;
    interviewAt: string;
    interviewMode: InterviewMode;
    interviewRound: InterviewRound;
    interviewStatus: InterviewStatus;
    interviewResult: InterviewResult;
    meetingLink?: string;
    location?: string;
    notes?: string;
    createdAt?: string;
    updatedAt?: string;
}

interface ScheduleForm {
    applicationId: string;
    interviewerId: string;
    interviewAt: string;
    interviewMode: InterviewMode;
    interviewRound: InterviewRound;
    meetingLink: string;
    location: string;
    notes: string;
}

const initialForm: ScheduleForm = {
    applicationId: "",
    interviewerId: "",
    interviewAt: "",
    interviewMode: "ONLINE",
    interviewRound: "TECHNICAL",
    meetingLink: "",
    location: "",
    notes: "",
};

function readUserRole(): string {
    try {
        const user = JSON.parse(
            localStorage.getItem("hirecrux_user") || "{}"
        );
        return String(user.role || "")
            .replace(/^ROLE_/, "")
            .toUpperCase();
    } catch {
        return "";
    }
}

function formatDate(value?: string) {
    if (!value) return "Not scheduled";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;

    return date.toLocaleDateString(undefined, {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}

function formatTime(value?: string) {
    if (!value) return "Time not available";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;

    return date.toLocaleTimeString(undefined, {
        hour: "2-digit",
        minute: "2-digit",
    });
}

function label(value?: string) {
    return value
        ? value
            .toLowerCase()
            .split("_")
            .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
            .join(" ")
        : "—";
}

function getModeIcon(mode?: InterviewMode) {
    if (mode === "ONLINE") return <Video size={17} />;
    if (mode === "PHONE") return <Phone size={17} />;
    return <MapPin size={17} />;
}

export default function InterviewsPage() {
    const [interviews, setInterviews] = useState<Interview[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [actionError, setActionError] = useState("");
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [showSchedule, setShowSchedule] = useState(false);
    const [saving, setSaving] = useState(false);
    const [workingId, setWorkingId] = useState<number | null>(null);
    const [completeId, setCompleteId] = useState<number | null>(null);
    const [result, setResult] = useState<
        "PASSED" | "FAILED" | "ON_HOLD"
    >("PASSED");
    const [form, setForm] = useState<ScheduleForm>(initialForm);

    const role = readUserRole();
    const isHR = role === "HR";
    const isCandidate = role === "CANDIDATE";
    const canComplete = isHR || role === "INTERVIEWER";

    const loadInterviews = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const response = await interviewApi.getMine();
            setInterviews(
                Array.isArray(response.data) ? response.data : []
            );
        } catch (err) {
            setError(getApiErrorMessage(err).message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void loadInterviews();
    }, [loadInterviews]);

    const counts = useMemo(
        () => ({
            total: interviews.length,
            upcoming: interviews.filter(
                (item) =>
                    ["SCHEDULED", "ACCEPTED"].includes(item.interviewStatus) &&
                    item.interviewAt &&
                    new Date(item.interviewAt).getTime() >= Date.now()
            ).length,
            completed: interviews.filter(
                (item) => item.interviewStatus === "COMPLETED"
            ).length,
            pending: interviews.filter(
                (item) => item.interviewStatus === "SCHEDULED"
            ).length,
        }),
        [interviews]
    );

    const filteredInterviews = useMemo(() => {
        const query = search.trim().toLowerCase();

        return interviews.filter((item) => {
            const matchesSearch =
                !query ||
                [
                    item.candidateName,
                    item.candidateEmail,
                    item.interviewRound,
                    item.interviewMode,
                    String(item.applicationId),
                    String(item.interviewId),
                ].some((value) => String(value || "").toLowerCase().includes(query));

            const matchesStatus =
                statusFilter === "ALL" ||
                item.interviewStatus === statusFilter;

            return matchesSearch && matchesStatus;
        });
    }, [interviews, search, statusFilter]);

    function updateForm<K extends keyof ScheduleForm>(
        field: K,
        value: ScheduleForm[K]
    ) {
        setForm((previous) => ({ ...previous, [field]: value }));
    }

    async function handleSchedule(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setActionError("");

        if (!form.applicationId || !form.interviewerId || !form.interviewAt) {
            setActionError(
                "Application ID, interviewer ID, and interview date/time are required."
            );
            return;
        }

        if (
            form.interviewMode === "ONLINE" &&
            !form.meetingLink.trim()
        ) {
            setActionError("Please enter the online meeting link.");
            return;
        }

        if (
            form.interviewMode === "OFFLINE" &&
            !form.location.trim()
        ) {
            setActionError("Please enter the interview location.");
            return;
        }

        setSaving(true);

        try {
            await interviewApi.schedule({
                applicationId: Number(form.applicationId),
                interviewerId: Number(form.interviewerId),
                interviewAt: form.interviewAt,
                interviewMode: form.interviewMode,
                interviewRound: form.interviewRound,
                meetingLink: form.meetingLink.trim() || undefined,
                location: form.location.trim() || undefined,
                notes: form.notes.trim() || undefined,
            });

            setShowSchedule(false);
            setForm(initialForm);
            await loadInterviews();
        } catch (err) {
            setActionError(getApiErrorMessage(err).message);
        } finally {
            setSaving(false);
        }
    }

    async function runAction(
        interviewId: number,
        action: "accept" | "reject" | "cancel"
    ) {
        setActionError("");
        setWorkingId(interviewId);

        try {
            if (action === "accept") await interviewApi.accept(interviewId);
            if (action === "reject") await interviewApi.reject(interviewId);
            if (action === "cancel") await interviewApi.cancel(interviewId);

            await loadInterviews();
        } catch (err) {
            setActionError(getApiErrorMessage(err).message);
        } finally {
            setWorkingId(null);
        }
    }

    async function handleComplete() {
        if (completeId === null) return;

        setActionError("");
        setWorkingId(completeId);

        try {
            await interviewApi.complete(completeId, {
                interviewResult: result,
            });

            setCompleteId(null);
            await loadInterviews();
        } catch (err) {
            setActionError(getApiErrorMessage(err).message);
        } finally {
            setWorkingId(null);
        }
    }

    return (
        <main className="interviews-page">
            <header className="interviews-header">
                <div>
                    <span className="interviews-eyebrow">TALENT OPERATIONS</span>
                    <h1>Interviews</h1>
                    <p>
                        {isHR
                            ? "Schedule interviews and manage your hiring pipeline."
                            : "Keep track of your interview schedule and updates."}
                    </p>
                </div>

                <div className="interviews-header-actions">
                    <button
                        className="interview-button interview-button-secondary"
                        onClick={() => void loadInterviews()}
                        disabled={loading}
                    >
                        <RefreshCw size={17} />
                        Refresh
                    </button>

                    {isHR && (
                        <button
                            className="interview-button interview-button-primary"
                            onClick={() => {
                                setActionError("");
                                setShowSchedule(true);
                            }}
                        >
                            <Plus size={18} />
                            Schedule interview
                        </button>
                    )}
                </div>
            </header>

            <section className="interview-stats">
                <article className="interview-stat-card">
          <span className="interview-stat-icon">
            <CalendarDays size={20} />
          </span>
                    <div>
                        <p>Total interviews</p>
                        <strong>{counts.total}</strong>
                    </div>
                </article>

                <article className="interview-stat-card">
          <span className="interview-stat-icon">
            <CalendarClock size={20} />
          </span>
                    <div>
                        <p>Upcoming</p>
                        <strong>{counts.upcoming}</strong>
                    </div>
                </article>

                <article className="interview-stat-card">
          <span className="interview-stat-icon">
            <ClipboardCheck size={20} />
          </span>
                    <div>
                        <p>Completed</p>
                        <strong>{counts.completed}</strong>
                    </div>
                </article>

                <article className="interview-stat-card">
          <span className="interview-stat-icon">
            <Clock3 size={20} />
          </span>
                    <div>
                        <p>Awaiting response</p>
                        <strong>{counts.pending}</strong>
                    </div>
                </article>
            </section>

            {actionError && (
                <div className="interview-alert interview-alert-error" role="alert">
                    {actionError}
                    <button onClick={() => setActionError("")} aria-label="Dismiss">
                        ×
                    </button>
                </div>
            )}

            <section className="interview-list-panel">
                <div className="interview-list-heading">
                    <div>
                        <h2>Interview schedule</h2>
                        <p>Review interview details and current status.</p>
                    </div>
                    <span className="interview-total-pill">
            {filteredInterviews.length} results
          </span>
                </div>

                <div className="interview-toolbar">
                    <label className="interview-search">
                        <Search size={18} />
                        <input
                            value={search}
                            onChange={(event) => setSearch(event.target.value)}
                            placeholder="Search candidate, round, or ID..."
                        />
                    </label>

                    <select
                        aria-label="Filter by interview status"
                        value={statusFilter}
                        onChange={(event) => setStatusFilter(event.target.value)}
                    >
                        <option value="ALL">All statuses</option>
                        {(
                            [
                                "SCHEDULED",
                                "ACCEPTED",
                                "REJECTED",
                                "COMPLETED",
                                "CANCELLED",
                                "NO_SHOW",
                            ] as InterviewStatus[]
                        ).map((status) => (
                            <option key={status} value={status}>
                                {label(status)}
                            </option>
                        ))}
                    </select>
                </div>

                {loading ? (
                    <div className="interview-state">
                        <span className="interview-spinner" />
                        <p>Loading interviews...</p>
                    </div>
                ) : error ? (
                    <div className="interview-state">
                        <XCircle size={34} />
                        <h3>Unable to load interviews</h3>
                        <p>{error}</p>
                        <button
                            className="interview-button interview-button-secondary"
                            onClick={() => void loadInterviews()}
                        >
                            Try again
                        </button>
                    </div>
                ) : filteredInterviews.length === 0 ? (
                    <div className="interview-state">
                        <Users size={38} />
                        <h3>No interviews found</h3>
                        <p>
                            {interviews.length === 0
                                ? "Your interview schedule is empty right now."
                                : "Try changing your search or status filter."}
                        </p>
                    </div>
                ) : (
                    <div className="interview-cards">
                        {filteredInterviews.map((item) => (
                            <article className="interview-card" key={item.interviewId}>
                                <div className="interview-card-top">
                                    <div className="interview-avatar">
                                        {(item.candidateName || "C")
                                            .trim()
                                            .charAt(0)
                                            .toUpperCase()}
                                    </div>

                                    <div className="interview-person">
                                        <h3>{item.candidateName || `Interview #${item.interviewId}`}</h3>
                                        <p>{item.candidateEmail || "Candidate details unavailable"}</p>
                                    </div>

                                    <span
                                        className={`interview-status status-${item.interviewStatus.toLowerCase().replace("_", "-")}`}
                                    >
                    {label(item.interviewStatus)}
                  </span>
                                </div>

                                <div className="interview-details-grid">
                                    <div className="interview-detail">
                                        <CalendarDays size={17} />
                                        <div>
                                            <span>Date</span>
                                            <strong>{formatDate(item.interviewAt)}</strong>
                                        </div>
                                    </div>

                                    <div className="interview-detail">
                                        <Clock3 size={17} />
                                        <div>
                                            <span>Time</span>
                                            <strong>{formatTime(item.interviewAt)}</strong>
                                        </div>
                                    </div>

                                    <div className="interview-detail">
                                        {getModeIcon(item.interviewMode)}
                                        <div>
                                            <span>Interview mode</span>
                                            <strong>{label(item.interviewMode)}</strong>
                                        </div>
                                    </div>

                                    <div className="interview-detail">
                                        <ClipboardCheck size={17} />
                                        <div>
                                            <span>Round</span>
                                            <strong>{label(item.interviewRound)}</strong>
                                        </div>
                                    </div>
                                </div>

                                <div className="interview-meta">
                                    <span>Interview #{item.interviewId}</span>
                                    <span>Application #{item.applicationId}</span>
                                    <span>Result: {label(item.interviewResult)}</span>
                                </div>

                                {(item.meetingLink || item.location || item.notes) && (
                                    <div className="interview-extra">
                                        {item.meetingLink && (
                                            <p>
                                                <Video size={15} />
                                                <a
                                                    href={item.meetingLink}
                                                    target="_blank"
                                                    rel="noreferrer"
                                                >
                                                    Join meeting
                                                </a>
                                            </p>
                                        )}
                                        {item.location && (
                                            <p>
                                                <MapPin size={15} />
                                                {item.location}
                                            </p>
                                        )}
                                        {item.notes && <p className="interview-notes">{item.notes}</p>}
                                    </div>
                                )}

                                <div className="interview-card-actions">
                                    {isCandidate && item.interviewStatus === "SCHEDULED" && (
                                        <>
                                            <button
                                                className="interview-button interview-button-primary"
                                                disabled={workingId === item.interviewId}
                                                onClick={() =>
                                                    void runAction(item.interviewId, "accept")
                                                }
                                            >
                                                <CheckCircle2 size={16} />
                                                Accept
                                            </button>
                                            <button
                                                className="interview-button interview-button-danger"
                                                disabled={workingId === item.interviewId}
                                                onClick={() =>
                                                    void runAction(item.interviewId, "reject")
                                                }
                                            >
                                                <XCircle size={16} />
                                                Reject
                                            </button>
                                        </>
                                    )}

                                    {isHR &&
                                        ["SCHEDULED", "ACCEPTED"].includes(item.interviewStatus) && (
                                            <button
                                                className="interview-button interview-button-danger"
                                                disabled={workingId === item.interviewId}
                                                onClick={() =>
                                                    void runAction(item.interviewId, "cancel")
                                                }
                                            >
                                                Cancel interview
                                            </button>
                                        )}

                                    {canComplete &&
                                        ["SCHEDULED", "ACCEPTED"].includes(item.interviewStatus) && (
                                            <button
                                                className="interview-button interview-button-secondary"
                                                onClick={() => {
                                                    setCompleteId(item.interviewId);
                                                    setResult("PASSED");
                                                }}
                                            >
                                                <ClipboardCheck size={16} />
                                                Complete interview
                                            </button>
                                        )}
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </section>

            {showSchedule && (
                <div
                    className="interview-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            setShowSchedule(false);
                            setActionError("");
                        }
                    }}
                >
                    <section
                        className="interview-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="schedule-interview-title"
                    >
                        <div className="interview-modal-heading">
                            <div>
                                <span className="interviews-eyebrow">HIRING PIPELINE</span>
                                <h2 id="schedule-interview-title">Schedule interview</h2>
                                <p>Enter the application and interview details.</p>
                            </div>
                            <button
                                className="interview-modal-close"
                                onClick={() => {
                                    setShowSchedule(false);
                                    setActionError("");
                                }}
                                aria-label="Close schedule dialog"
                            >
                                ×
                            </button>
                        </div>

                        <form className="interview-form" onSubmit={handleSchedule}>
                            <div className="interview-form-grid">
                                <label>
                                    Application ID *
                                    <input
                                        type="number"
                                        min="1"
                                        required
                                        value={form.applicationId}
                                        onChange={(event) =>
                                            updateForm("applicationId", event.target.value)
                                        }
                                        placeholder="e.g. 12"
                                    />
                                </label>

                                <label>
                                    Interviewer user ID *
                                    <input
                                        type="number"
                                        min="1"
                                        required
                                        value={form.interviewerId}
                                        onChange={(event) =>
                                            updateForm("interviewerId", event.target.value)
                                        }
                                        placeholder="e.g. 4"
                                    />
                                </label>

                                <label className="interview-form-full">
                                    Interview date and time *
                                    <input
                                        type="datetime-local"
                                        required
                                        value={form.interviewAt}
                                        onChange={(event) =>
                                            updateForm("interviewAt", event.target.value)
                                        }
                                    />
                                </label>

                                <label>
                                    Interview mode *
                                    <select
                                        value={form.interviewMode}
                                        onChange={(event) =>
                                            updateForm("interviewMode", event.target.value as InterviewMode)
                                        }
                                    >
                                        <option value="ONLINE">Online</option>
                                        <option value="OFFLINE">Offline</option>
                                        <option value="PHONE">Phone</option>
                                    </select>
                                </label>

                                <label>
                                    Interview round *
                                    <select
                                        value={form.interviewRound}
                                        onChange={(event) =>
                                            updateForm("interviewRound", event.target.value as InterviewRound)
                                        }
                                    >
                                        <option value="HR">HR</option>
                                        <option value="TECHNICAL">Technical</option>
                                        <option value="MANAGER">Manager</option>
                                        <option value="FINAL">Final</option>
                                    </select>
                                </label>

                                {form.interviewMode === "ONLINE" && (
                                    <label className="interview-form-full">
                                        Meeting link *
                                        <input
                                            type="url"
                                            required
                                            value={form.meetingLink}
                                            onChange={(event) =>
                                                updateForm("meetingLink", event.target.value)
                                            }
                                            placeholder="https://meet.example.com/..."
                                        />
                                    </label>
                                )}

                                {form.interviewMode === "OFFLINE" && (
                                    <label className="interview-form-full">
                                        Interview location *
                                        <input
                                            required
                                            value={form.location}
                                            onChange={(event) =>
                                                updateForm("location", event.target.value)
                                            }
                                            placeholder="Office address or meeting room"
                                        />
                                    </label>
                                )}

                                <label className="interview-form-full">
                                    Notes
                                    <textarea
                                        rows={3}
                                        value={form.notes}
                                        onChange={(event) => updateForm("notes", event.target.value)}
                                        placeholder="Add instructions or interview details..."
                                    />
                                </label>
                            </div>

                            {actionError && (
                                <p className="interview-form-error" role="alert">
                                    {actionError}
                                </p>
                            )}

                            <div className="interview-form-actions">
                                <button
                                    type="button"
                                    className="interview-button interview-button-secondary"
                                    onClick={() => {
                                        setShowSchedule(false);
                                        setActionError("");
                                    }}
                                    disabled={saving}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="interview-button interview-button-primary"
                                    disabled={saving}
                                >
                                    {saving ? "Scheduling..." : "Schedule interview"}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}

            {completeId !== null && (
                <div className="interview-modal-backdrop">
                    <section
                        className="interview-modal interview-complete-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="complete-interview-title"
                    >
                        <div className="interview-modal-heading">
                            <div>
                                <span className="interviews-eyebrow">INTERVIEW FEEDBACK</span>
                                <h2 id="complete-interview-title">Complete interview</h2>
                                <p>Select the final result for this interview.</p>
                            </div>
                            <button
                                className="interview-modal-close"
                                onClick={() => setCompleteId(null)}
                                aria-label="Close completion dialog"
                            >
                                ×
                            </button>
                        </div>

                        <label className="interview-result-label">
                            Interview result
                            <select
                                value={result}
                                onChange={(event) =>
                                    setResult(event.target.value as "PASSED" | "FAILED" | "ON_HOLD")
                                }
                            >
                                <option value="PASSED">Passed</option>
                                <option value="FAILED">Failed</option>
                                <option value="ON_HOLD">On hold</option>
                            </select>
                        </label>

                        {actionError && (
                            <p className="interview-form-error" role="alert">
                                {actionError}
                            </p>
                        )}

                        <div className="interview-form-actions">
                            <button
                                className="interview-button interview-button-secondary"
                                onClick={() => setCompleteId(null)}
                                disabled={workingId === completeId}
                            >
                                Cancel
                            </button>
                            <button
                                className="interview-button interview-button-primary"
                                onClick={() => void handleComplete()}
                                disabled={workingId === completeId}
                            >
                                {workingId === completeId ? "Saving..." : "Save result"}
                            </button>
                        </div>
                    </section>
                </div>
            )}
        </main>
    );
}
