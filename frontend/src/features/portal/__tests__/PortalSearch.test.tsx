import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { RoleGuard } from '../../auth/RoleGuard';
import { AuthProvider } from '../../auth/AuthContext';
import { PortalSearchPage } from '../pages/PortalSearchPage';

describe('PortalSearch', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    localStorage.clear();
  });

  it('shows filter controls and empty state', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 0 }), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        }),
      ),
    );
    render(
      <MemoryRouter initialEntries={['/portal/search?q=none']}>
        <Routes>
          <Route path="/portal/search" element={<PortalSearchPage />} />
        </Routes>
      </MemoryRouter>,
    );
    await waitFor(() => expect(screen.getByText('검색 결과가 없습니다')).toBeInTheDocument());
    await userEvent.selectOptions(screen.getByLabelText('유형'), 'DOCUMENT');
    expect(screen.getByDisplayValue('DOCUMENT')).toBeInTheDocument();
  });

  it('shows access denied state for protected routes', () => {
    localStorage.setItem('cms5-demo-token', 'employee');
    render(
      <AuthProvider>
        <MemoryRouter>
          <RoleGuard roles={['ADMIN']}>
            <div>secret</div>
          </RoleGuard>
        </MemoryRouter>
      </AuthProvider>,
    );
    expect(screen.getByText('접근 권한이 없습니다')).toBeInTheDocument();
  });
});
