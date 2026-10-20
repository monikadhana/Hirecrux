
import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, Sparkles } from "lucide-react";
import { authApi } from "../lib/api";
import "./AuthPage.css";
import "./RegisterPage.css";

export default function RegisterPage() {
    const navigate = useNavigate();

    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            await authApi.register({
                fullName: fullName.trim(),
                email: email.trim(),
                phone: phone.trim(),
                password,
            });

            // Registration does not return a JWT in our current API.
            // Let the user sign in after registering.
            navigate("/login", {
                replace: true,
                state: { registered: true },
            });
        } catch (err: unknown) {
            const message =
                typeof err === "object" &&
                err !== null &&
                "response" in err
                    ? (
                        err as {
                            response?: {
                                data?: { message?: string; error?: string };
                            };
                        }
                    ).response?.data?.message ??
                    (
                        err as {
                            response?: {
                                data?: { error?: string };
                            };
                        }
                    ).response?.data?.error
                    : undefined;

            setError(
                message ??
                "Unable to create your account. Check your details and try again."
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <main className="auth-page register-page">
            <section className="auth-visual">
                <Link to="/login" className="brand">
          <span className="brand-mark">
            <Sparkles size={22} />
          </span>
                    <span>HireCrux</span>
                </Link>

                <div className="auth-visual-content">
          <span className="eyebrow">
            <span className="status-dot" />
            YOUR NEXT CHAPTER STARTS HERE
          </span>

                    <h1>
                        Your talent.
                        <br />
                        <span>Your next opportunity.</span>
                    </h1>

                    <p>
                        Create your account to explore opportunities, complete
                        assessments, and move forward in your career with HireCrux.
                    </p>
                </div>

                <p className="visual-footer">
                    A smarter way to connect talent and opportunity.
                </p>
            </section>

            <section className="auth-form-panel">
                <div className="auth-form-wrap">
                    <div className="mobile-brand brand">
            <span className="brand-mark">
              <Sparkles size={20} />
            </span>
                        <span>HireCrux</span>
                    </div>

                    <span className="eyebrow">GET STARTED</span>
                    <h2>Create your account.</h2>
                    <p className="muted">
                        Join HireCrux and take the next step in your career.
                    </p>

                    {error && (
                        <div className="error-banner" role="alert">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="auth-form">
                        <label htmlFor="fullName">Full name</label>
                        <input
                            id="fullName"
                            type="text"
                            autoComplete="name"
                            placeholder="Enter your full name"
                            value={fullName}
                            onChange={(event) => setFullName(event.target.value)}
                            maxLength={100}
                            required
                        />

                        <label htmlFor="registerEmail">Email address</label>
                        <input
                            id="registerEmail"
                            type="email"
                            autoComplete="email"
                            placeholder="you@example.com"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            required
                        />

                        <label htmlFor="phone">Phone number</label>
                        <input
                            id="phone"
                            type="tel"
                            autoComplete="tel"
                            placeholder="Enter your phone number"
                            value={phone}
                            onChange={(event) => setPhone(event.target.value)}
                            required
                        />

                        <label htmlFor="registerPassword">Password</label>
                        <div className="password-input">
                            <input
                                id="registerPassword"
                                type={showPassword ? "text" : "password"}
                                autoComplete="new-password"
                                placeholder="Create a password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                required
                            />
                            <button
                                type="button"
                                className="icon-button"
                                aria-label={showPassword ? "Hide password" : "Show password"}
                                onClick={() => setShowPassword((current) => !current)}
                            >
                                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>

                        <button
                            type="submit"
                            className="primary-button login-button"
                            disabled={loading}
                        >
                            {loading ? "Creating account..." : "Create account"}
                            {!loading && <ArrowRight size={18} />}
                        </button>
                    </form>

                    <p className="auth-switch">
                        Already have an account? <Link to="/login">Sign in</Link>
                    </p>

                    <p className="auth-note">
                        Your next great opportunity starts here.
                    </p>
                </div>
            </section>
        </main>
    );
}
