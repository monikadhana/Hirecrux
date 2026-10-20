
import { useCallback, useEffect, useState, type FormEvent } from "react";
import {
    ArrowUpRight,
    BriefcaseBusiness,
    CalendarDays,
    CheckCircle2,
    MapPin,
    Plus,
    RefreshCw,
    Search,
    SlidersHorizontal,
    Sparkles,
    X,
} from "lucide-react";
import { applicationApi, jobApi } from "../lib/api";
import "./JobsPage.css";

interface Job {
    jobId: number;
    title: string;
    description: string;
    experienceRequired: string;
    salaryRange: string;
    location: string;
    deadline?: string;
    status: string;
}

interface JobForm {
    title: string;
    description: string;
    experienceRequired: string;
    salaryRange: string;
    location: string;
    deadline: string;
}

const emptyForm: JobForm = {
    title: "",
    description: "",
    experienceRequired: "",
    salaryRange: "",
    location: "",
    deadline: "",
};

function getErrorMessage(error: unknown, fallback: string): string {
    if (typeof error === "object" && error !== null && "response" in error) {
        const response = (
            error as {
                response?: { data?: { message?: string; error?: string } };
            }
        ).response;

        return response?.data?.message ?? response?.data?.error ?? fallback;
    }

    return fallback;
}

function formatDeadline(deadline?: string): string {
    if (!deadline) return "Not specified";

    const date = new Date(deadline);

    if (Number.isNaN(date.getTime())) return deadline;

    return date.toLocaleDateString(undefined, {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}

export default function JobsPage() {
    const storedUser = localStorage.getItem("hirecrux_user");
    const user = storedUser ? JSON.parse(storedUser) : {};
    const role = String(user.role ?? "").replace(/^ROLE_/, "").toUpperCase();
    const isHR = role === "HR";
    const isCandidate = role === "CANDIDATE";

    const [jobs, setJobs] = useState<Job[]>([]);
    const [appliedJobIds, setAppliedJobIds] = useState<number[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");

    const [titleFilter, setTitleFilter] = useState("");
    const [locationFilter, setLocationFilter] = useState("");
    const [experienceFilter, setExperienceFilter] = useState("");
    const [searchApplied, setSearchApplied] = useState(false);

    const [formOpen, setFormOpen] = useState(false);
    const [editingJobId, setEditingJobId] = useState<number | null>(null);
    const [form, setForm] = useState<JobForm>(emptyForm);
    const [saving, setSaving] = useState(false);
    const [applyingJobId, setApplyingJobId] = useState<number | null>(null);
    const [closingJobId, setClosingJobId] = useState<number | null>(null);

    const loadJobs = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const response = await jobApi.getAll();
            setJobs(Array.isArray(response.data) ? response.data : []);
        } catch (err: unknown) {
            setJobs([]);
            setError(
                getErrorMessage(
                    err,
                    "Unable to load jobs. Check your backend connection and login session."
                )
            );
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void loadJobs();
    }, [loadJobs]);

    async function handleSearch(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setLoading(true);
        setError("");
        setNotice("");
        setSearchApplied(true);

        try {
            const response = await jobApi.search({
                title: titleFilter.trim() || undefined,
                location: locationFilter.trim() || undefined,
                experienceRequired: experienceFilter.trim() || undefined,
            });

            setJobs(Array.isArray(response.data) ? response.data : []);
        } catch (err: unknown) {
            setError(getErrorMessage(err, "Unable to search jobs."));
        } finally {
            setLoading(false);
        }
    }

    async function clearSearch() {
        setTitleFilter("");
        setLocationFilter("");
        setExperienceFilter("");
        setSearchApplied(false);
        setNotice("");
        setError("");
        await loadJobs();
    }

    function openCreateForm() {
        setEditingJobId(null);
        setForm(emptyForm);
        setError("");
        setNotice("");
        setFormOpen(true);
    }

    async function openEditForm(job: Job) {
        setError("");
        setNotice("");

        try {
            const response = await jobApi.getById(job.jobId);
            const currentJob: Job = response.data;

            setEditingJobId(currentJob.jobId);
            setForm({
                title: currentJob.title ?? "",
                description: currentJob.description ?? "",
                experienceRequired: currentJob.experienceRequired ?? "",
                salaryRange: currentJob.salaryRange ?? "",
                location: currentJob.location ?? "",
                deadline: currentJob.deadline
                    ? currentJob.deadline.slice(0, 16)
                    : "",
            });
            setFormOpen(true);
        } catch (err: unknown) {
            setError(getErrorMessage(err, "Unable to load this job for editing."));
        }
    }

    async function handleSaveJob(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSaving(true);
        setError("");
        setNotice("");

        const payload = {
            ...form,
            deadline: form.deadline || null,
        };

        try {
            if (editingJobId !== null) {
                await jobApi.update(editingJobId, payload);
                setNotice("Job updated successfully.");
            } else {
                await jobApi.create(payload);
                setNotice("Job created successfully.");
            }

            setFormOpen(false);
            setEditingJobId(null);

            // Reload the full list, including closed jobs for HR.
            await loadJobs();
        } catch (err: unknown) {
            setError(
                getErrorMessage(
                    err,
                    "Unable to save this job. Check the required fields and your HR permissions."
                )
            );
        } finally {
            setSaving(false);
        }
    }

    async function handleCloseJob(job: Job) {
        const confirmed = window.confirm(
            `Close "${job.title}"? Candidates will no longer see it in open-job search results.`
        );

        if (!confirmed) return;

        setClosingJobId(job.jobId);
        setError("");
        setNotice("");

        try {
            await jobApi.close(job.jobId);
            setNotice(`"${job.title}" has been closed.`);
            await loadJobs();
        } catch (err: unknown) {
            setError(getErrorMessage(err, "Unable to close this job."));
        } finally {
            setClosingJobId(null);
        }
    }

    async function handleApply(job: Job) {
        if (appliedJobIds.includes(job.jobId)) {
            return;
        }

        const confirmed = window.confirm(`Apply for "${job.title}"?`);
        if (!confirmed) return;

        setApplyingJobId(job.jobId);
        setError("");
        setNotice("");

        try {
            await applicationApi.create({ jobId: job.jobId });

            setAppliedJobIds((current) =>
                current.includes(job.jobId)
                    ? current
                    : [...current, job.jobId]
            );

            setNotice(`Your application for "${job.title}" was submitted.`);
        } catch (err: unknown) {
            setError(
                getErrorMessage(
                    err,
                    "Unable to submit your application. You may already have applied."
                )
            );
        } finally {
            setApplyingJobId(null);
        }
    }

    const openJobCount = jobs.filter(
        (job) => job.status === "OPEN"
    ).length;

    return (
        <main className="jobs-page">
            <header className="jobs-header">
                <div>
          <span className="jobs-eyebrow">
            <Sparkles size={14} />
            OPPORTUNITY EXPLORER
          </span>

                    <h1>{isHR ? "Manage jobs" : "Find your next opportunity"}</h1>

                    <p>
                        {isHR
                            ? "Create and manage job postings for your hiring team."
                            : "Explore opportunities that match your skills and career goals."}
                    </p>
                </div>

                <div className="jobs-header-actions">
                    <button
                        type="button"
                        className="jobs-secondary-button"
                        onClick={() => void loadJobs()}
                        disabled={loading}
                    >
                        <RefreshCw size={16} />
                        Refresh
                    </button>

                    {isHR && (
                        <button
                            type="button"
                            className="jobs-primary-button"
                            onClick={openCreateForm}
                        >
                            <Plus size={17} />
                            Create job
                        </button>
                    )}
                </div>
            </header>

            <section className="jobs-summary">
                <div className="jobs-summary-icon">
                    <BriefcaseBusiness size={23} />
                </div>
                <div>
                    <span>{loading ? "Loading jobs..." : `${jobs.length} jobs found`}</span>
                    <strong>
                        {loading ? "—" : `${openJobCount} open positions`}
                    </strong>
                </div>
            </section>

            <form className="jobs-search" onSubmit={handleSearch}>
                <div className="jobs-search-heading">
                    <div>
                        <SlidersHorizontal size={18} />
                        <strong>Find the right role</strong>
                    </div>
                    {searchApplied && (
                        <button
                            type="button"
                            className="jobs-clear-button"
                            onClick={() => void clearSearch()}
                        >
                            Clear filters
                        </button>
                    )}
                </div>

                <div className="jobs-filter-grid">
                    <label className="jobs-filter-field">
                        <span>Job title</span>
                        <div className="jobs-input-wrap">
                            <Search size={17} />
                            <input
                                value={titleFilter}
                                onChange={(event) => setTitleFilter(event.target.value)}
                                placeholder="e.g. Java Developer"
                            />
                        </div>
                    </label>

                    <label className="jobs-filter-field">
                        <span>Location</span>
                        <div className="jobs-input-wrap">
                            <MapPin size={17} />
                            <input
                                value={locationFilter}
                                onChange={(event) => setLocationFilter(event.target.value)}
                                placeholder="e.g. Chennai"
                            />
                        </div>
                    </label>

                    <label className="jobs-filter-field">
                        <span>Experience</span>
                        <div className="jobs-input-wrap">
                            <BriefcaseBusiness size={17} />
                            <input
                                value={experienceFilter}
                                onChange={(event) => setExperienceFilter(event.target.value)}
                                placeholder="e.g. 2 years"
                            />
                        </div>
                    </label>

                    <button
                        className="jobs-primary-button jobs-search-button"
                        type="submit"
                        disabled={loading}
                    >
                        <Search size={16} />
                        Search jobs
                    </button>
                </div>
            </form>

            {error && (
                <div className="jobs-alert jobs-alert-error" role="alert">
                    {error}
                    <button
                        type="button"
                        aria-label="Dismiss error"
                        onClick={() => setError("")}
                    >
                        <X size={16} />
                    </button>
                </div>
            )}

            {notice && (
                <div className="jobs-alert jobs-alert-success" role="status">
                    <CheckCircle2 size={17} />
                    <span>{notice}</span>
                    <button
                        type="button"
                        aria-label="Dismiss message"
                        onClick={() => setNotice("")}
                    >
                        <X size={16} />
                    </button>
                </div>
            )}

            <section className="jobs-results">
                <div className="jobs-results-heading">
                    <div>
                        <h2>{searchApplied ? "Search results" : "Available positions"}</h2>
                        <p>
                            {isHR
                                ? "Review your postings and update them when needed."
                                : "Explore roles and apply when you find the right fit."}
                        </p>
                    </div>
                    {!loading && (
                        <span className="jobs-result-count">
              {jobs.length} {jobs.length === 1 ? "result" : "results"}
            </span>
                    )}
                </div>

                {loading ? (
                    <div className="jobs-state">
                        <RefreshCw className="jobs-spin" size={27} />
                        <strong>Finding opportunities...</strong>
                        <p>Fetching the latest jobs from HireCrux.</p>
                    </div>
                ) : jobs.length === 0 ? (
                    <div className="jobs-state">
                        <div className="jobs-state-icon">
                            <BriefcaseBusiness size={27} />
                        </div>
                        <strong>
                            {searchApplied ? "No matching jobs" : "No jobs available yet"}
                        </strong>
                        <p>
                            {searchApplied
                                ? "Try different search terms or clear your filters."
                                : isHR
                                    ? "Create your first job posting to get started."
                                    : "Please check back later for new opportunities."}
                        </p>
                        {searchApplied && (
                            <button
                                type="button"
                                className="jobs-secondary-button"
                                onClick={() => void clearSearch()}
                            >
                                Clear filters
                            </button>
                        )}
                        {isHR && !searchApplied && (
                            <button
                                type="button"
                                className="jobs-primary-button"
                                onClick={openCreateForm}
                            >
                                <Plus size={17} />
                                Create first job
                            </button>
                        )}
                    </div>
                ) : (
                    <div className="jobs-card-grid">
                        {jobs.map((job) => (
                            <article className="job-card" key={job.jobId}>
                                <div className="job-card-top">
                                    <div className="job-card-icon">
                                        <BriefcaseBusiness size={21} />
                                    </div>
                                    <span
                                        className={`job-status ${
                                            job.status === "OPEN" ? "job-status-open" : "job-status-closed"
                                        }`}
                                    >
                    <span />
                                        {job.status}
                  </span>
                                </div>

                                <h3>{job.title}</h3>

                                <div className="job-meta">
                  <span>
                    <MapPin size={15} />
                      {job.location || "Location not specified"}
                  </span>
                                    <span>
                    <BriefcaseBusiness size={15} />
                                        {job.experienceRequired || "Experience not specified"}
                  </span>
                                    <span>
                    <CalendarDays size={15} />
                    Deadline: {formatDeadline(job.deadline)}
                  </span>
                                </div>

                                <p className="job-description">
                                    {job.description || "No description provided."}
                                </p>

                                <div className="job-card-bottom">
                                    <div className="job-salary">
                                        <span>Salary range</span>
                                        <strong>{job.salaryRange || "Not specified"}</strong>
                                    </div>

                                    <div className="job-card-actions">
                                        {isCandidate && job.status === "OPEN" && (
                                            <button
                                                type="button"
                                                className={
                                                    appliedJobIds.includes(job.jobId)
                                                        ? "jobs-applied-button"
                                                        : "jobs-primary-button"
                                                }
                                                disabled={
                                                    applyingJobId === job.jobId ||
                                                    appliedJobIds.includes(job.jobId)
                                                }
                                                onClick={() => void handleApply(job)}
                                            >
                                                {applyingJobId === job.jobId
                                                    ? "Applying..."
                                                    : appliedJobIds.includes(job.jobId)
                                                        ? "Applied"
                                                        : "Apply now"}

                                                {appliedJobIds.includes(job.jobId) ? (
                                                    <CheckCircle2 size={16} />
                                                ) : (
                                                    <ArrowUpRight size={16} />
                                                )}
                                            </button>
                                        )}

                                        {isHR && (
                                            <>
                                                <button
                                                    type="button"
                                                    className="jobs-secondary-button"
                                                    onClick={() => void openEditForm(job)}
                                                >
                                                    Edit
                                                </button>

                                                {job.status === "OPEN" && (
                                                    <button
                                                        type="button"
                                                        className="jobs-danger-button"
                                                        disabled={closingJobId === job.jobId}
                                                        onClick={() => void handleCloseJob(job)}
                                                    >
                                                        {closingJobId === job.jobId
                                                            ? "Closing..."
                                                            : "Close job"}
                                                    </button>
                                                )}
                                            </>
                                        )}

                                        {!isHR && !isCandidate && (
                                            <span className="job-role-note">
                        Sign in with a candidate account to apply.
                      </span>
                                        )}
                                    </div>
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </section>

            {formOpen && isHR && (
                <div
                    className="jobs-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget && !saving) {
                            setFormOpen(false);
                        }
                    }}
                >
                    <section
                        className="jobs-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="job-form-title"
                    >
                        <header className="jobs-modal-header">
                            <div>
                                <span className="jobs-eyebrow">HIRING WORKSPACE</span>
                                <h2 id="job-form-title">
                                    {editingJobId === null ? "Create a job" : "Edit job"}
                                </h2>
                                <p>Provide the details candidates need to know.</p>
                            </div>
                            <button
                                type="button"
                                className="jobs-modal-close"
                                aria-label="Close form"
                                disabled={saving}
                                onClick={() => setFormOpen(false)}
                            >
                                <X size={20} />
                            </button>
                        </header>

                        <form className="jobs-modal-form" onSubmit={handleSaveJob}>
                            <label>
                                Job title *
                                <input
                                    required
                                    maxLength={150}
                                    value={form.title}
                                    onChange={(event) =>
                                        setForm({ ...form, title: event.target.value })
                                    }
                                    placeholder="e.g. Java Backend Developer"
                                />
                            </label>

                            <label>
                                Description *
                                <textarea
                                    required
                                    rows={4}
                                    value={form.description}
                                    onChange={(event) =>
                                        setForm({ ...form, description: event.target.value })
                                    }
                                    placeholder="Describe the role and responsibilities..."
                                />
                            </label>

                            <div className="jobs-modal-two-col">
                                <label>
                                    Experience required *
                                    <input
                                        required
                                        value={form.experienceRequired}
                                        onChange={(event) =>
                                            setForm({
                                                ...form,
                                                experienceRequired: event.target.value,
                                            })
                                        }
                                        placeholder="e.g. 2+ years"
                                    />
                                </label>

                                <label>
                                    Salary range
                                    <input
                                        value={form.salaryRange}
                                        onChange={(event) =>
                                            setForm({ ...form, salaryRange: event.target.value })
                                        }
                                        placeholder="e.g. 5–8 LPA"
                                    />
                                </label>
                            </div>

                            <div className="jobs-modal-two-col">
                                <label>
                                    Location *
                                    <input
                                        required
                                        value={form.location}
                                        onChange={(event) =>
                                            setForm({ ...form, location: event.target.value })
                                        }
                                        placeholder="e.g. Chennai / Remote"
                                    />
                                </label>

                                <label>
                                    Application deadline
                                    <input
                                        type="datetime-local"
                                        value={form.deadline}
                                        onChange={(event) =>
                                            setForm({ ...form, deadline: event.target.value })
                                        }
                                    />
                                </label>
                            </div>

                            <div className="jobs-modal-actions">
                                <button
                                    type="button"
                                    className="jobs-secondary-button"
                                    disabled={saving}
                                    onClick={() => setFormOpen(false)}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="jobs-primary-button"
                                    disabled={saving}
                                >
                                    {saving
                                        ? "Saving..."
                                        : editingJobId === null
                                            ? "Create job"
                                            : "Save changes"}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </main>
    );
}
