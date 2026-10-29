
import { useEffect, useMemo, useState } from "react";
import {
    Bell,
    Check,
    CheckCheck,
    Clock3,
    Inbox,
    RefreshCw,
    AlertCircle,
} from "lucide-react";
import { notificationApi } from "../lib/api";
import "./NotificationsPage.css";

interface NotificationItem {
    notificationId: number;
    title: string;
    message: string;
    isRead: boolean;
    createdAt: string;
}

type NotificationFilter = "ALL" | "UNREAD" | "READ";

const formatDate = (date?: string) => {
    if (!date) return "Date unavailable";

    const parsed = new Date(date);

    if (Number.isNaN(parsed.getTime())) {
        return "Date unavailable";
    }

    return parsed.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

export default function NotificationsPage() {
    const [notifications, setNotifications] = useState<NotificationItem[]>([]);
    const [filter, setFilter] = useState<NotificationFilter>("ALL");
    const [loading, setLoading] = useState(true);
    const [markingId, setMarkingId] = useState<number | null>(null);
    const [markingAll, setMarkingAll] = useState(false);
    const [error, setError] = useState("");

    const loadNotifications = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await notificationApi.getMine();
            const data = response.data;

            setNotifications(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Failed to load notifications:", err);
            setError("Unable to load notifications. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        void loadNotifications();
    }, []);

    const unreadCount = notifications.filter(
        (notification) => !notification.isRead
    ).length;

    const readCount = notifications.length - unreadCount;

    const filteredNotifications = useMemo(() => {
        if (filter === "UNREAD") {
            return notifications.filter((notification) => !notification.isRead);
        }

        if (filter === "READ") {
            return notifications.filter((notification) => notification.isRead);
        }

        return notifications;
    }, [notifications, filter]);

    const handleMarkAsRead = async (notificationId: number) => {
        try {
            setMarkingId(notificationId);
            setError("");

            const response = await notificationApi.markAsRead(notificationId);
            const updated = response.data as NotificationItem;

            setNotifications((previous) =>
                previous.map((notification) =>
                    notification.notificationId === notificationId
                        ? { ...notification, ...updated, isRead: true }
                        : notification
                )
            );
        } catch (err) {
            console.error("Failed to mark notification as read:", err);
            setError("Could not mark this notification as read. Please try again.");
        } finally {
            setMarkingId(null);
        }
    };

    const handleMarkAllAsRead = async () => {
        const unreadNotifications = notifications.filter(
            (notification) => !notification.isRead
        );

        if (unreadNotifications.length === 0) return;

        setMarkingAll(true);
        setError("");

        let failedCount = 0;

        try {
            for (const notification of unreadNotifications) {
                try {
                    const response = await notificationApi.markAsRead(
                        notification.notificationId
                    );

                    const updated = response.data as NotificationItem;

                    setNotifications((previous) =>
                        previous.map((item) =>
                            item.notificationId === notification.notificationId
                                ? { ...item, ...updated, isRead: true }
                                : item
                        )
                    );
                } catch (err) {
                    failedCount++;
                    console.error(
                        `Failed to mark notification ${notification.notificationId} as read:`,
                        err
                    );
                }
            }

            if (failedCount > 0) {
                setError(
                    `${failedCount} notification(s) could not be marked as read. Please try again.`
                );
            }
        } finally {
            setMarkingAll(false);
        }
    };

    return (
        <main className="notifications-page">
            <header className="notifications-header">
                <div>
                    <span className="notifications-eyebrow">STAY INFORMED</span>
                    <h1>Notifications</h1>
                    <p>
                        Keep track of your application updates and important messages.
                    </p>
                </div>

                <button
                    type="button"
                    className="notifications-refresh"
                    onClick={() => void loadNotifications()}
                    disabled={loading || markingAll || markingId !== null}
                >
                    <RefreshCw
                        size={16}
                        className={loading ? "notifications-spinning" : ""}
                    />
                    Refresh
                </button>
            </header>

            <section className="notifications-summary">
                <article className="notification-summary-card">
                    <div className="notification-summary-icon purple">
                        <Bell size={21} />
                    </div>
                    <div>
                        <span>Total notifications</span>
                        <strong>{notifications.length}</strong>
                    </div>
                </article>

                <article className="notification-summary-card">
                    <div className="notification-summary-icon blue">
                        <Inbox size={21} />
                    </div>
                    <div>
                        <span>Unread</span>
                        <strong>{unreadCount}</strong>
                    </div>
                </article>

                <article className="notification-summary-card">
                    <div className="notification-summary-icon green">
                        <CheckCheck size={21} />
                    </div>
                    <div>
                        <span>Read</span>
                        <strong>{readCount}</strong>
                    </div>
                </article>
            </section>

            <section className="notifications-panel">
                <div className="notifications-panel-header">
                    <div>
                        <h2>Recent notifications</h2>
                        <p>Your latest updates in one place.</p>
                    </div>

                    {unreadCount > 0 && (
                        <button
                            type="button"
                            className="notifications-mark-all"
                            onClick={() => void handleMarkAllAsRead()}
                            disabled={markingAll || markingId !== null}
                        >
                            <CheckCheck size={16} />
                            {markingAll ? "Marking as read..." : "Mark all as read"}
                        </button>
                    )}
                </div>

                <div className="notifications-filters">
                    {(
                        [
                            ["ALL", "All"],
                            ["UNREAD", "Unread"],
                            ["READ", "Read"],
                        ] as const
                    ).map(([value, label]) => (
                        <button
                            type="button"
                            key={value}
                            className={`notification-filter ${
                                filter === value ? "active" : ""
                            }`}
                            onClick={() => setFilter(value)}
                        >
                            {label}
                            {value === "UNREAD" && unreadCount > 0 && (
                                <span className="notification-filter-count">
                  {unreadCount}
                </span>
                            )}
                        </button>
                    ))}
                </div>

                {error && (
                    <div className="notifications-error" role="alert">
                        <AlertCircle size={17} />
                        <span>{error}</span>
                        <button
                            type="button"
                            onClick={() => void loadNotifications()}
                            disabled={loading || markingAll}
                        >
                            Retry
                        </button>
                    </div>
                )}

                {loading ? (
                    <div className="notifications-state">
                        <div className="notifications-loader" />
                        <p>Loading notifications...</p>
                    </div>
                ) : filteredNotifications.length === 0 ? (
                    <div className="notifications-state">
                        <div className="notifications-empty-icon">
                            <Inbox size={30} />
                        </div>
                        <h3>
                            {notifications.length === 0
                                ? "You're all caught up"
                                : `No ${filter.toLowerCase()} notifications`}
                        </h3>
                        <p>
                            {notifications.length === 0
                                ? "New application updates and messages will appear here."
                                : "Try selecting another filter to see your notifications."}
                        </p>
                    </div>
                ) : (
                    <div className="notifications-list">
                        {filteredNotifications.map((notification) => (
                            <article
                                key={notification.notificationId}
                                className={`notification-item ${
                                    notification.isRead ? "is-read" : "is-unread"
                                }`}
                            >
                                <div className="notification-item-icon">
                                    {notification.isRead ? (
                                        <Check size={19} />
                                    ) : (
                                        <Bell size={19} />
                                    )}
                                </div>

                                <div className="notification-item-content">
                                    <div className="notification-item-heading">
                                        <h3>{notification.title}</h3>
                                        {!notification.isRead && (
                                            <span className="notification-unread-dot" />
                                        )}
                                    </div>

                                    <p>{notification.message}</p>

                                    <div className="notification-item-date">
                                        <Clock3 size={14} />
                                        <span>{formatDate(notification.createdAt)}</span>
                                    </div>
                                </div>

                                <div className="notification-item-actions">
                                    {notification.isRead ? (
                                        <span className="notification-read-label">Read</span>
                                    ) : (
                                        <button
                                            type="button"
                                            className="notification-read-button"
                                            onClick={() =>
                                                void handleMarkAsRead(notification.notificationId)
                                            }
                                            disabled={markingAll || markingId !== null}
                                        >
                                            <Check size={15} />
                                            {markingId === notification.notificationId
                                                ? "Saving..."
                                                : "Mark as read"}
                                        </button>
                                    )}
                                </div>
                            </article>
                        ))}
                    </div>
                )}

                {!loading && filteredNotifications.length > 0 && (
                    <div className="notifications-footer">
                        Showing {filteredNotifications.length} of {notifications.length}{" "}
                        notifications
                    </div>
                )}
            </section>
        </main>
    );
}
