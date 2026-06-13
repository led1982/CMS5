import { Save } from 'lucide-react';
import { useEffect, useState } from 'react';
import { RoleCode, UserSummary } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Table } from '../../../shared/components/Table';
import { listUsers, replaceUserRoles } from '../api/adminApi';

const roles: RoleCode[] = ['ADMIN', 'EDITOR', 'REVIEWER', 'EMPLOYEE'];

export function UserRoleManagementPage() {
  const [users, setUsers] = useState<UserSummary[]>([]);

  function load() {
    listUsers({}).then(setUsers);
  }

  useEffect(load, []);

  async function toggle(user: UserSummary, role: RoleCode) {
    const next = user.roles.includes(role) ? user.roles.filter((item) => item !== role) : [...user.roles, role];
    await replaceUserRoles(user.id, next.length === 0 ? ['EMPLOYEE'] : next);
    load();
  }

  return (
    <section className="section">
      <div className="section-header">
        <h2>사용자·역할 관리</h2>
      </div>
      <Table columns={['사용자', '부서', '역할', '작업']}>
        {users.map((user) => (
          <tr key={user.id}>
            <td>
              <strong>{user.displayName}</strong>
              <div className="muted">{user.email}</div>
            </td>
            <td>{user.department?.name}</td>
            <td>
              <div className="toolbar">
                {roles.map((role) => (
                  <label className="toolbar" key={role}>
                    <input type="checkbox" checked={user.roles.includes(role)} onChange={() => toggle(user, role)} />
                    {role}
                  </label>
                ))}
              </div>
            </td>
            <td>
              <Button icon={<Save size={16} />} onClick={load}>
                새로고침
              </Button>
            </td>
          </tr>
        ))}
      </Table>
    </section>
  );
}
