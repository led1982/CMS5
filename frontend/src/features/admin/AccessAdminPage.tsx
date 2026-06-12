import { ShieldCheck } from "lucide-react";
import { useEffect, useState } from "react";
import { AudienceSummary, RoleSummary, api } from "../../api/client";

export function AccessAdminPage() {
  const [roles, setRoles] = useState<RoleSummary[]>([]);
  const [audiences, setAudiences] = useState<AudienceSummary[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([api<RoleSummary[]>("/admin/roles"), api<AudienceSummary[]>("/admin/audiences")])
      .then(([roleResult, audienceResult]) => {
        setRoles(roleResult);
        setAudiences(audienceResult);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }, []);

  return (
    <div className="grid">
      <div className="section-header">
        <h1 className="page-title">Access</h1>
        <ShieldCheck size={22} aria-hidden="true" />
      </div>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <div className="grid two">
        <section className="panel">
          <h2>Roles</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Code</th><th>Name</th><th>Permissions</th></tr>
              </thead>
              <tbody>
                {roles.map((role) => (
                  <tr key={role.id}>
                    <td>{role.code}</td>
                    <td>{role.name}</td>
                    <td>{role.permissions.join(", ")}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
        <section className="panel">
          <h2>Audiences</h2>
          <div className="list">
            {audiences.map((audience) => (
              <div className="card" key={audience.id}>
                <strong>{audience.name}</strong>
                <div className="meta">
                  <span>{audience.code}</span>
                  <span>{audience.type}</span>
                  <span>{audience.active ? "Active" : "Inactive"}</span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
