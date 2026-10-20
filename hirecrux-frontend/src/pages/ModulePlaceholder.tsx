
import { useLocation } from "react-router-dom";
import { Sparkles } from "lucide-react";

const moduleDetails: Record<string, { title: string; description: string }> = {
    "/jobs": {
        title: "Job management",
        description: "Browse opportunities and manage job postings.",
    },
    "/applications": {
        title: "Applications",
        description: "Track candidate applications and recruitment progress.",
    },
    "/assessments": {
        title: "Assessments",
        description: "Manage assessment assignments, questions and results.",
    },
    "/profile": {
        title: "Candidate profile",
        description: "Manage your experience, education and resume details.",
    },
    "/notifications": {
        title: "Notifications",
        description: "Stay informed about your recruitment activity.",
    },
};

export default function ModulePlaceholder() {
    const { pathname } = useLocation();
    const module = moduleDetails[pathname] ?? {
        title: "Coming soon",
        description: "This workspace is being prepared.",
    };

    return (
        <section className="placeholder-page">
            <div className="placeholder-icon"><Sparkles size={27} /></div>
            <span className="eyebrow">HIRECRUX WORKSPACE</span>
            <h1>{module.title}</h1>
            <p className="muted">{module.description}</p>
            <div className="placeholder-note">
                This page will be connected to your existing backend APIs
                as we build the module.
            </div>
        </section>
    );
}
