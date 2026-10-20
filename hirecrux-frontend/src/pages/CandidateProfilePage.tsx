
import { useEffect, useState, type FormEvent } from "react";
import {
    UserRound,
    Mail,
    BriefcaseBusiness,
    GraduationCap,
    FileText,
    Link,
    Save,
    Pencil,
    RefreshCw,
    ExternalLink,
} from "lucide-react";
import { candidateApi } from "../lib/api";
import "./CandidateProfilePage.css";

interface CandidateProfile {
    candidateId?: number;
    fullName?: string;
    email?: string;
    resumeUrl: string;
    totalExperience: number;
    education: string;
    linkedinUrl: string;
    githubUrl: string;
}

const emptyProfile: CandidateProfile = {
    resumeUrl: "",
    totalExperience: 0,
    education: "",
    linkedinUrl: "",
    githubUrl: "",
};

function getErrorMessage(error: unknown, fallback: string) {
    if (
        typeof error === "object" &&
        error !== null &&
        "response" in error
    ) {
        const response = (
            error as {
                response?: {
                    data?: { message?: string; error?: string };
                };
            }
        ).response;

        return response?.data?.message ?? response?.data?.error ?? fallback;
    }

    return fallback;
}

export default function CandidateProfilePage() {
    const [profile, setProfile] = useState<CandidateProfile>(emptyProfile);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [editing, setEditing] = useState(false);
    const [exists, setExists] = useState(false);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");

    async function loadProfile() {
        setLoading(true);
        setError("");
        setNotice("");

        try {
            const response = await candidateApi.getMyProfile();
            setProfile({ ...emptyProfile, ...response.data });
            setExists(true);
            setEditing(false);
        } catch (err: unknown) {
            setProfile(emptyProfile);
            setExists(false);

            const message = getErrorMessage(
                err,
                "Unable to load your profile."
            );

            // The backend currently throws when a profile does not exist.
            if (
                message.toLowerCase().includes("profile not found") ||
                message.toLowerCase().includes("candidate not found")
            ) {
                setEditing(true);
            } else {
                setError(message);
            }
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        void loadProfile();
    }, []);

    function updateField<K extends keyof CandidateProfile>(
        field: K,
        value: CandidateProfile[K]
    ) {
        setProfile((current) => ({ ...current, [field]: value }));
    }

    async function handleSave(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSaving(true);
        setError("");
        setNotice("");

        const payload = {
            resumeUrl: profile.resumeUrl.trim(),
            totalExperience: Number(profile.totalExperience),
            education: profile.education.trim(),
            linkedinUrl: profile.linkedinUrl.trim(),
            githubUrl: profile.githubUrl.trim(),
        };

        try {
            const response = exists
                ? await candidateApi.updateMyProfile(payload)
                : await candidateApi.create(payload);

            setProfile({ ...emptyProfile, ...response.data });
            setExists(true);
            setEditing(false);
            setNotice(
                exists
                    ? "Your profile was updated successfully."
                    : "Your profile was created successfully."
            );
        } catch (err: unknown) {
            setError(
                getErrorMessage(err, "Unable to save your profile.")
            );
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return (
            <main className="candidate-profile-page">
                <div className="cp-state">
                    <RefreshCw className="cp-spin" size={25} />
                    <p>Loading your profile...</p>
                </div>
            </main>
        );
    }

    return (
        <main className="candidate-profile-page">
            <header className="cp-header">
                <div>
          <span className="cp-eyebrow">
            <UserRound size={14} />
            CANDIDATE WORKSPACE
          </span>
                    <h1>My Profile</h1>
                    <p>
                        Keep your professional details updated to help recruiters
                        understand your experience and skills.
                    </p>
                </div>

                {!editing && exists && (
                    <button
                        className="cp-primary-button"
                        type="button"
                        onClick={() => {
                            setError("");
                            setNotice("");
                            setEditing(true);
                        }}
                    >
                        <Pencil size={16} />
                        Edit profile
                    </button>
                )}
            </header>

            {error && (
                <div className="cp-message cp-error" role="alert">
                    {error}
                </div>
            )}

            {notice && (
                <div className="cp-message cp-success" role="status">
                    {notice}
                </div>
            )}

            <section className="cp-identity-card">
                <div className="cp-avatar">
                    <UserRound size={32} />
                </div>
                <div className="cp-identity-details">
                    <h2>{profile.fullName || "Candidate"}</h2>
                    <p>
                        <Mail size={15} />
                        {profile.email || "Email from your account"}
                    </p>
                    <span className={`cp-profile-status ${exists ? "cp-complete" : ""}`}>
            {exists ? "Profile created" : "Profile not created"}
          </span>
                </div>
            </section>

            {!exists && (
                <div className="cp-info-message">
                    Complete the form below to create your candidate profile.
                </div>
            )}

            <form className="cp-form-card" onSubmit={handleSave}>
                <div className="cp-section-heading">
                    <div>
                        <h2>Professional information</h2>
                        <p>Share your background and professional links.</p>
                    </div>
                    <BriefcaseBusiness size={21} />
                </div>

                <div className="cp-form-grid">
                    <label className="cp-field">
                        <span>Total experience (years) *</span>
                        <input
                            type="number"
                            min="0"
                            max="60"
                            step="0.1"
                            required
                            disabled={!editing}
                            value={profile.totalExperience}
                            onChange={(event) =>
                                updateField("totalExperience", Number(event.target.value))
                            }
                        />
                    </label>

                    <label className="cp-field">
                        <span>Education *</span>
                        <div className="cp-input-wrap">
                            <GraduationCap size={17} />
                            <input
                                required
                                disabled={!editing}
                                value={profile.education}
                                onChange={(event) =>
                                    updateField("education", event.target.value)
                                }
                                placeholder="e.g. B.E. Computer Science"
                            />
                        </div>
                    </label>

                    <label className="cp-field cp-full-width">
                        <span>Resume URL *</span>
                        <div className="cp-input-wrap">
                            <FileText size={17} />
                            <input
                                type="url"
                                required
                                disabled={!editing}
                                value={profile.resumeUrl}
                                onChange={(event) =>
                                    updateField("resumeUrl", event.target.value)
                                }
                                placeholder="https://..."
                            />
                            {!editing && profile.resumeUrl && (
                                <a
                                    href={profile.resumeUrl}
                                    target="_blank"
                                    rel="noreferrer"
                                    aria-label="Open resume"
                                >
                                    <ExternalLink size={16} />
                                </a>
                            )}
                        </div>
                    </label>

                    <label className="cp-field">
                        <span>LinkedIn URL *</span>
                        <div className="cp-input-wrap">
                            <Link size={17} />
                            <input
                                type="url"
                                required
                                disabled={!editing}
                                value={profile.linkedinUrl}
                                onChange={(event) =>
                                    updateField("linkedinUrl", event.target.value)
                                }
                                placeholder="https://linkedin.com/in/..."
                            />
                        </div>
                    </label>

                    <label className="cp-field">
                        <span>GitHub URL *</span>
                        <div className="cp-input-wrap">
                            <Link size={17} />
                            <input
                                type="url"
                                required
                                disabled={!editing}
                                value={profile.githubUrl}
                                onChange={(event) =>
                                    updateField("githubUrl", event.target.value)
                                }
                                placeholder="https://github.com/..."
                            />
                        </div>
                    </label>
                </div>

                {editing && (
                    <div className="cp-form-actions">
                        {exists && (
                            <button
                                className="cp-secondary-button"
                                type="button"
                                disabled={saving}
                                onClick={() => {
                                    setEditing(false);
                                    setError("");
                                    setNotice("");
                                    void loadProfile();
                                }}
                            >
                                Cancel
                            </button>
                        )}

                        <button
                            className="cp-primary-button"
                            type="submit"
                            disabled={saving}
                        >
                            <Save size={16} />
                            {saving
                                ? "Saving..."
                                : exists
                                    ? "Save changes"
                                    : "Create profile"}
                        </button>
                    </div>
                )}
            </form>
        </main>
    );
}
