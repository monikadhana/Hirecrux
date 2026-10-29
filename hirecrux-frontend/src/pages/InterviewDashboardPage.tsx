
import { useCallback, useEffect, useState } from "react";
import {
    CalendarDays,
    CalendarClock,
    CheckCircle2,
    Clock3,
    RefreshCw,
    XCircle,
    UserCheck,
    TrendingUp,
} from "lucide-react";
import { interviewApi, getApiErrorMessage } from "../lib/api";
import "./InterviewDashboardPage.css";

interface InterviewDashboard {
    todayInterviews: number;
    upcomingInterviews: number;
    completedInterviews: number;
    scheduledCount: number;
    acceptedCount: number;
    rejectedCount: number;
    cancelledCount: number;
}

const emptyDashboard: InterviewDashboard = {
    todayInterviews: 0,
    upcomingInterviews: 0,
    completedInterviews: 0,
    scheduledCount: 0,
    acceptedCount: 0,
    rejectedCount: 0,
    cancelledCount: 0,
};

export default function InterviewDashboardPage() {
    const [dashboard, setDashboard] =
        useState<InterviewDashboard>(emptyDashboard);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadDashboard = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const response = await interviewApi.getDashboard();
            setDashboard({
                ...emptyDashboard,
                ...(response.data || {}),
            });
        } catch (err) {
            setError(getApiErrorMessage(err).message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void loadDashboard();
    }, [loadDashboard]);

    const cards = [
        {
            label: "Today's interviews",
            value: dashboard.todayInterviews,
            description: "Interviews scheduled for today",
            icon: CalendarDays,
            tone: "lavender",
        },
        {
            label: "Upcoming interviews",
            value: dashboard.upcomingInterviews,
            description: "Interviews scheduled ahead",
            icon: CalendarClock,
            tone: "blue",
        },
        {
            label: "Completed interviews",
            value: dashboard.completedInterviews,
            description: "Interviews marked completed",
            icon: CheckCircle2,
            tone: "green",
        },
        {
            label: "Scheduled",
            value: dashboard.scheduledCount,
            description: "Awaiting candidate response",
            icon: Clock3,
            tone: "amber",
        },
    ];

    const statusRows = [
        {
            label: "Accepted",
            value: dashboard.acceptedCount,
            icon: UserCheck,
            tone: "green",
        },
        {
            label: "Scheduled",
            value: dashboard.scheduledCount,
            icon: Clock3,
            tone: "lavender",
        },
        {
            label: "Rejected",
            value: dashboard.rejectedCount,
            icon: XCircle,
            tone: "red",
        },
        {
            label: "Cancelled",
            value: dashboard.cancelledCount,
            icon: CalendarClock,
            tone: "amber",
        },
    ];

    const totalTracked = statusRows.reduce(
        (sum, item) => sum + item.value,
        0
    );

    return (
        <main className="interview-dashboard-page">
            <header className="interview-dashboard-header">
                <div>
                    <span className="interviews-eyebrow">HIRING OVERVIEW</span>
                    <h1>Interview dashboard</h1>
                    <p>
                        Review interview activity and candidate response status.
                    </p>
                </div>

                <button
                    className="interview-button interview-button-secondary"
                    onClick={() => void loadDashboard()}
                    disabled={loading}
                >
                    <RefreshCw size={17} />
                    Refresh data
                </button>
            </header>

            {error && (
                <div className="interview-dashboard-error" role="alert">
                    <div>
                        <strong>Dashboard could not be loaded</strong>
                        <p>{error}</p>
                    </div>
                    <button
                        className="interview-button interview-button-secondary"
                        onClick={() => void loadDashboard()}
                    >
                        Try again
                    </button>
                </div>
            )}

            <section className="interview-dashboard-intro">
                <div className="interview-dashboard-intro-icon">
                    <TrendingUp size={23} />
                </div>
                <div>
                    <h2>Your interview pipeline</h2>
                    <p>
                        Monitor the schedule, completion activity, and interview responses
                        in one place.
                    </p>
                </div>
            </section>

            <section className="interview-dashboard-stats">
                {cards.map((card) => {
                    const Icon = card.icon;

                    return (
                        <article
                            className="interview-dashboard-stat"
                            key={card.label}
                        >
                            <div className="interview-dashboard-stat-top">
                <span className={`dashboard-icon tone-${card.tone}`}>
                  <Icon size={20} />
                </span>
                                <span className="interview-dashboard-stat-label">
                  {card.label}
                </span>
                            </div>

                            <strong className="interview-dashboard-number">
                                {loading ? "—" : card.value}
                            </strong>

                            <p>{card.description}</p>
                        </article>
                    );
                })}
            </section>

            <section className="interview-dashboard-breakdown">
                <div className="interview-dashboard-section-heading">
                    <div>
                        <h2>Interview status breakdown</h2>
                        <p>See how interviews are progressing through the pipeline.</p>
                    </div>
                    <span className="interview-dashboard-total">
            {loading ? "Loading..." : `${totalTracked} tracked`}
          </span>
                </div>

                <div className="interview-dashboard-status-list">
                    {statusRows.map((item) => {
                        const Icon = item.icon;
                        const percentage =
                            totalTracked > 0
                                ? Math.round((item.value / totalTracked) * 100)
                                : 0;

                        return (
                            <article
                                className="interview-dashboard-status-row"
                                key={item.label}
                            >
                                <div className="interview-dashboard-status-title">
                  <span className={`dashboard-icon tone-${item.tone}`}>
                    <Icon size={18} />
                  </span>
                                    <div>
                                        <strong>{item.label}</strong>
                                        <span>
                      {loading ? "Loading" : `${percentage}% of tracked statuses`}
                    </span>
                                    </div>
                                </div>

                                <div className="interview-dashboard-progress">
                                    <div className="interview-dashboard-progress-track">
                    <span
                        className={`interview-dashboard-progress-fill tone-${item.tone}`}
                        style={{
                            width: loading ? "0%" : `${percentage}%`,
                        }}
                    />
                                    </div>
                                </div>

                                <strong className="interview-dashboard-status-count">
                                    {loading ? "—" : item.value}
                                </strong>
                            </article>
                        );
                    })}
                </div>
            </section>

            <p className="interview-dashboard-footnote">
                Statistics reflect the data returned by your backend dashboard API.
                The status breakdown excludes statuses not included in its response,
                such as <code>NO_SHOW</code>.
            </p>
        </main>
    );
}
