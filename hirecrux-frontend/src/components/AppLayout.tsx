
import { useEffect, useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
    LayoutDashboard, BriefcaseBusiness, Users, FileText,
    ClipboardCheck, Bell, Sun, Moon, LogOut, Menu, X, Sparkles,
} from "lucide-react";

const navItems = [
    { label: "Overview", path: "/dashboard", icon: LayoutDashboard },
    { label: "Jobs", path: "/jobs", icon: BriefcaseBusiness },
    { label: "Applications", path: "/applications", icon: FileText },
    { label: "Assessments", path: "/assessments", icon: ClipboardCheck },
    { label: "Candidate Profile", path: "/profile", icon: Users },
    { label: "Notifications", path: "/notifications", icon: Bell },
];

export default function AppLayout() {
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("hirecrux_user") || "{}");

    const [theme, setTheme] = useState(
        () => localStorage.getItem("hirecrux_theme") || "dark"
    );
    const [mobileOpen, setMobileOpen] = useState(false);

    useEffect(() => {
        document.documentElement.dataset.theme = theme;
        localStorage.setItem("hirecrux_theme", theme);
    }, [theme]);

    function logout() {
        localStorage.removeItem("hirecrux_token");
        localStorage.removeItem("hirecrux_user");
        navigate("/login", { replace: true });
    }

    const initials = (user.fullName || "HireCrux")
        .split(" ")
        .map((part: string) => part[0])
        .slice(0, 2)
        .join("")
        .toUpperCase();

    return (
        <div className="app-shell">
            {mobileOpen && (
                <button
                    className="mobile-backdrop"
                    aria-label="Close navigation"
                    onClick={() => setMobileOpen(false)}
                />
            )}

            <aside className={`sidebar ${mobileOpen ? "sidebar-open" : ""}`}>
                <div className="sidebar-brand">
                    <span className="brand-mark"><Sparkles size={21} /></span>
                    <span>HireCrux</span>
                    <button
                        className="icon-button mobile-close"
                        onClick={() => setMobileOpen(false)}
                        aria-label="Close menu"
                    >
                        <X size={20} />
                    </button>
                </div>

                <div className="workspace-label">WORKSPACE</div>

                <nav className="side-nav">
                    {navItems.map(({ label, path, icon: Icon }) => (
                        <NavLink
                            key={path}
                            to={path}
                            onClick={() => setMobileOpen(false)}
                            className={({ isActive }) =>
                                `nav-link ${isActive ? "nav-link-active" : ""}`
                            }
                        >
                            <Icon size={19} strokeWidth={1.8} />
                            <span>{label}</span>
                        </NavLink>
                    ))}
                </nav>

                <div className="sidebar-bottom">
                    <div className="sidebar-tip">
                        <Sparkles size={18} />
                        <strong>Great teams start here.</strong>
                        <p>Every great hire begins with the right opportunity.</p>
                    </div>

                    <button className="logout-button" onClick={logout}>
                        <LogOut size={18} /> Sign out
                    </button>
                </div>
            </aside>

            <div className="main-column">
                <header className="topbar">
                    <button
                        className="icon-button mobile-menu"
                        onClick={() => setMobileOpen(true)}
                        aria-label="Open navigation"
                    >
                        <Menu size={22} />
                    </button>

                    <div className="topbar-label">
                        <span className="topbar-overline">YOUR WORKSPACE</span>
                        <span className="topbar-title">Recruitment, reimagined.</span>
                    </div>

                    <div className="topbar-actions">
                        <button
                            className="theme-button"
                            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
                            aria-label={`Switch to ${theme === "dark" ? "light" : "dark"} theme`}
                            title="Switch theme"
                        >
                            {theme === "dark" ? <Sun size={18} /> : <Moon size={18} />}
                            <span>{theme === "dark" ? "Light" : "Dark"}</span>
                        </button>

                        <div className="user-chip">
                            <div className="avatar">{initials}</div>
                            <div className="user-chip-text">
                                <strong>{user.fullName || "Welcome"}</strong>
                                <span>{user.role || "USER"}</span>
                            </div>
                        </div>
                    </div>
                </header>

                <main className="page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
