import { createContext, ReactNode, useContext, useMemo, useState } from 'react';
import { RoleCode } from '../../shared/api/openapiClient';
import { selectedToken, setSelectedToken } from '../../shared/api/httpClient';
import { demoProfiles } from './authModel';

interface AuthContextValue {
  token: string;
  label: string;
  roles: RoleCode[];
  setToken: (token: string) => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState(selectedToken());
  const profile = demoProfiles[token] ?? demoProfiles.employee;
  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      label: profile.label,
      roles: profile.roles,
      setToken(nextToken) {
        setSelectedToken(nextToken);
        setTokenState(nextToken);
      },
    }),
    [profile.label, profile.roles, token],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
