
import { useEffect, useState } from "react";
import {
    ArrowUpRight, BriefcaseBusiness, Bell, Sparkles,
    Clock3, RefreshCw,
} from "lucide-react";
import { Link } from "react-router-dom";
import { jobApi, notificationApi } from "../lib/api";
import "./DashboardPage.css";

interface Job {
    jobId: number;
    title: string;
    location: string;
    status: string;
    deadline?: string;
}

interface Notification {
    notificationId: number;
    title: string;
    message: string;
    isRead: boolean;
    createdAt?: string;
}

export default function DashboardPage() {
    const user = JSON.parse(localStorage.getItem("hirecrux_user") || "{}");

    const [jobs, setJobs] = useState<Job[]>([]);
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    async function loadDashboard() {
        setLoading(true);
        setError("");

        const results = await Promise.allSettled([
            jobApi.getAll(),
            notificationApi.getMine(),
        ]);

        if (results[0].status === "fulfilled") {
            setJobs(results[0].value.data);
        }

        if (results[1].status === "fulfilled") {
            setNotifications(results[1].value.data);
        }

        if (results.every((result) => result.status === "rejected")) {
            setError(
                "Unable to load dashboard data. Check your backend and JWT token."
            );
        }

        setLoading(false);
    }

    useEffect(() => {
        void loadDashboard();
    }, []);

    const openJobs = jobs.filter((job) => job.status === "OPEN").length;
    const unreadCount = notifications.filter((item) => !item.isRead).length;

    return (
        <div className="dashboard">
            <section className="welcome-row">
                <div>
                    <span className="eyebrow">YOUR RECRUITMENT HUB</span>
                    <h1>
                        Welcome back, {(user.fullName || "there").split(" ")[0]}{" "}
                        <span className="wave">✦</span>
                    </h1>
                    <p className="muted">
                        Here's what's happening across your HireCrux workspace.
                    </p>
                </div>

                <button
                    className="secondary-button"
                    onClick={() => void loadDashboard()}
                    disabled={loading}
                >
                    <RefreshCw size={16} />
                    Refresh
                </button>
            </section>

            <section className="hero-banner">
                <div className="hero-copy">
          <span className="hero-tag">
            <Sparkles size={14} /> YOUR NEXT CHAPTER
          </span>
                    <h2>Good people build great things.</h2>
                    <p>
                        Manage opportunities, connect with talent and make
                        every step of recruitment count.
                    </p>
                    <Link className="hero-link" to="/jobs">
                        Explore opportunities <ArrowUpRight size={17} />
                    </Link>
                </div>

                <div className="hero-art" aria-hidden="true">
                    <div className="hero-ring ring-a" />
                    <div className="hero-ring ring-b" />
                    <div className="hero-star"><Sparkles size={38} /></div>
                </div>
            </section>

            {error && <div className="error-banner">{error}</div>}

            <section className="stats-grid">
                <article className="stat-card">
                    <div className="stat-top">
            <span className="stat-icon purple">
              <BriefcaseBusiness size={20} />
            </span>
                        <span className="stat-caption">LIVE DATA</span>
                    </div>
                    <span className="stat-label">Total jobs</span>
                    <strong className="stat-value">
                        {loading ? "—" : jobs.length}
                    </strong>
                    <span className="stat-foot">Across your job listings</span>
                </article>

                <article className="stat-card">
                    <div className="stat-top">
            <span className="stat-icon green">
              <BriefcaseBusiness size={20} />
            </span>
                        <span className="stat-caption">LIVE DATA</span>
                    </div>
                    <span className="stat-label">Open positions</span>
                    <strong className="stat-value">
                        {loading ? "—" : openJobs}
                    </strong>
                    <span className="stat-foot">Currently marked OPEN</span>
                </article>

                <article className="stat-card">
                    <div className="stat-top">
            <span className="stat-icon amber">
              <Bell size={20} />
            </span>
                        <span className="stat-caption">LIVE DATA</span>
                    </div>
                    <span className="stat-label">Unread notifications</span>
                    <strong className="stat-value">
                        {loading ? "—" : unreadCount}
                    </strong>
                    <span className="stat-foot">Waiting for your attention</span>
                </article>
            </section>

            <section className="dashboard-grid">
                <article className="panel jobs-panel">
                    <div className="panel-heading">
                        <div>
                            <h3>Recent opportunities</h3>
                            <p className="muted">Latest jobs from your backend</p>
                        </div>
                        <Link to="/jobs" className="text-link">
                            View all <ArrowUpRight size={15} />
                        </Link>
                    </div>

                    {loading ? (
                        <p className="empty-state">Loading jobs…</p>
                    ) : jobs.length === 0 ? (
                        <div className="empty-state">
                            <BriefcaseBusiness size={28} />
                            <strong>No job listings yet</strong>
                            <p>Your job listings will appear here.</p>
                        </div>
                    ) : (
                        <div className="job-list">
                            {jobs.slice(0, 5).map((job) => (
                                <div className="job-row" key={job.jobId}>
                                    <div className="job-avatar">
                                        <BriefcaseBusiness size={18} />
                                    </div>
                                    <div className="job-row-main">
                                        <strong>{job.title}</strong>
                                        <span>{job.location || "Location not specified"}</span>
                                    </div>
                                    <span
                                        className={`status-pill ${
                                            job.status === "OPEN" ? "status-open" : "status-other"
                                        }`}
                                    >
                    {job.status}
                  </span>
                                </div>
                            ))}
                        </div>
                    )}
                </article>

                <article className="panel activity-panel">
                    <div className="panel-heading">
                        <div>
                            <h3>Recent activity</h3>
                            <p className="muted">Your latest notifications</p>
                        </div>
                        <Bell size={18} className="muted-icon" />
                    </div>

                    {loading ? (
                        <p className="empty-state">Loading activity…</p>
                    ) : notifications.length === 0 ? (
                        <div className="empty-state">
                            <Clock3 size={26} />
                            <strong>You're all caught up</strong>
                            <p>New notifications will appear here.</p>
                        </div>
                    ) : (
                        <div className="activity-list">
                            {notifications.slice(0, 5).map((item) => (
                                <div className="activity-item" key={item.notificationId}>
                  <span
                      className={`activity-dot ${item.isRead ? "read" : ""}`}
                  />
                                    <div>
                                        <strong>{item.title}</strong>
                                        <p>{item.message}</p>
                                        <span className="activity-time">
                      {item.createdAt
                          ? new Date(item.createdAt).toLocaleString()
                          : "Recent notification"}
                    </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    <Link to="/notifications" className="activity-footer">
                        Open notifications <ArrowUpRight size={16} />
                    </Link>
                </article>
            </section>
        </div>
    );
}
