import { useEffect, useState } from "react";
import { useRoutes } from "react-router-dom";
import { api, UserSummary } from "../api/client";
import { AppLayout } from "../components/layout/AppLayout";
import { routes } from "./routes";

export function App() {
  const element = useRoutes(routes);
  const [user, setUser] = useState<UserSummary | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    const load = () => {
      api<UserSummary>("/me")
        .then((currentUser) => {
          if (mounted) {
            setUser(currentUser);
            setError(null);
          }
        })
        .catch((err: Error) => {
          if (mounted) {
            setError(err.message);
          }
        });
    };
    load();
    window.addEventListener("cms-session-changed", load);
    return () => {
      mounted = false;
      window.removeEventListener("cms-session-changed", load);
    };
  }, []);

  return (
    <AppLayout user={user} sessionError={error}>
      {element}
    </AppLayout>
  );
}
