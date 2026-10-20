
import { useState, type FormEvent } from "react";
import { useNavigate, Link } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, Sparkles } from "lucide-react";
import { authApi } from "../lib/api";
import "./AuthPage.css";
import "./LoginPage.css";

export default function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await authApi.login({ email, password });
            const user = response.data;

            localStorage.setItem("hirecrux_token", user.token);
            localStorage.setItem(
                "hirecrux_user",
                JSON.stringify({
                    userId: user.userId,
                    fullName: user.fullName,
                    email: user.email,
                    role: user.role,
                    isVerified: user.isVerified,
                })
            );

            navigate("/dashboard", { replace: true });
        } catch (err: any) {
            setError(
                err.response?.data?.message ??
                "Unable to sign in. Please check your credentials and backend."
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <main className="auth-page login-page" >
            <section className="auth-visual">
                <Link to="/login" className="brand">
                    <span className="brand-mark"><Sparkles size={22} /></span>
                    <span>HireCrux</span>
                </Link>

                <div className="auth-visual-content">
          <span className="eyebrow">
            <span className="status-dot" />
            THE FUTURE OF HIRING
          </span>

                    <h1>
                        Find the right
                        <br />
                        talent. <span>Build what’s next.</span>
                    </h1>

                    <p>
                        One intelligent workspace for jobs, candidates,
                        assessments and better hiring decisions.
                    </p>

                    <div className="visual-orbit">
                        <div className="orbit orbit-one" />
                        <div className="orbit orbit-two" />
                        <div className="orbit-core">
                            <Sparkles size={36} />
                        </div>
                        <span className="orbit-chip chip-one">Talent</span>
                        <span className="orbit-chip chip-two">Potential</span>
                        <span className="orbit-chip chip-three">Growth</span>
                    </div>
                </div>

                <p className="visual-footer">
                    Designed for ambitious teams and exceptional talent.
                </p>
            </section>

            <section className="auth-form-panel">
                <div className="auth-form-wrap">
                    <div className="mobile-brand brand">
                        <span className="brand-mark"><Sparkles size={20} /></span>
                        <span>HireCrux</span>
                    </div>

                    <span className="eyebrow">WELCOME BACK</span>
                    <h2>Let's get you in.</h2>
                    <p className="muted">
                        Sign in to continue your hiring journey.
                    </p>

                    {error && (
                        <div className="error-banner" role="alert">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="auth-form">
                        <label htmlFor="email">Email address</label>
                        <input
                            id="email"
                            type="email"
                            autoComplete="email"
                            placeholder="you@company.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />

                        <div className="password-label">
                            <label htmlFor="password">Password</label>
                        </div>

                        <div className="password-input">
                            <input
                                id="password"
                                type={showPassword ? "text" : "password"}
                                autoComplete="current-password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                            <button
                                type="button"
                                className="icon-button"
                                aria-label={showPassword ? "Hide password" : "Show password"}
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>

                        <button className="primary-button login-button" disabled={loading}>
                            {loading ? "Signing in..." : "Sign in to HireCrux"}
                            {!loading && <ArrowRight size={18} />}
                        </button>
                    </form>

                    <p className="auth-switch">
                        New to HireCrux? <Link to="/register">Create an account</Link>
                    </p>
                    <p className="auth-note">
                        Your next great opportunity starts here.
                    </p>
                </div>
            </section>
        </main>
    );
}
