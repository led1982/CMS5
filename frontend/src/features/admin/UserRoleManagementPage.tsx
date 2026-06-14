import type { RoleCode } from "../../data/mockCms";

const users: Array<{ email: string; name: string; department: string; roles: RoleCode[] }> = [
  { email: "admin@example.com", name: "관리자", department: "Platform", roles: ["ADMIN"] },
  { email: "editor@example.com", name: "콘텐츠 편집자", department: "Engineering", roles: ["EDITOR"] },
  { email: "reviewer@example.com", name: "검토자", department: "Security", roles: ["REVIEWER"] },
  { email: "employee@example.com", name: "일반 사용자", department: "Engineering", roles: ["EMPLOYEE"] }
];

export function UserRoleManagementPage() {
  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Users & Roles</h1>
          <p className="page-subtitle">역할 부여와 회수는 감사 로그에 기록됩니다.</p>
        </div>
      </section>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>User</th>
              <th>Email</th>
              <th>Department</th>
              <th>Roles</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.email}>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.department}</td>
                <td>
                  <div className="badge-row">
                    {user.roles.map((role) => (
                      <span key={role} className="badge secondary">
                        {role}
                      </span>
                    ))}
                  </div>
                </td>
                <td>
                  <button className="btn ghost" type="button">
                    Update
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
