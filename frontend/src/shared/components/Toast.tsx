import { ReactNode } from 'react';

export function Toast({ children, tone = 'success' }: { children: ReactNode; tone?: 'success' | 'error' | 'info' }) {
  return (
    <div className={`toast toast-${tone}`} role="status">
      {children}
    </div>
  );
}
