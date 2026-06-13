import { RoleCode } from '../../shared/api/openapiClient';

export const demoProfiles: Record<string, { label: string; roles: RoleCode[] }> = {
  employee: { label: 'Employee', roles: ['EMPLOYEE'] },
  editor: { label: 'Editor', roles: ['EDITOR', 'EMPLOYEE'] },
  reviewer: { label: 'Reviewer', roles: ['REVIEWER', 'EMPLOYEE'] },
  admin: { label: 'Admin', roles: ['ADMIN', 'EMPLOYEE'] },
};

export function hasAnyRole(userRoles: RoleCode[], requiredRoles: RoleCode[]): boolean {
  return requiredRoles.some((role) => userRoles.includes(role));
}
