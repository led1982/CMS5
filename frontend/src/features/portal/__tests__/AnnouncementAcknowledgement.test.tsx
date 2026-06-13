import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { AcknowledgementButton } from '../components/AcknowledgementButton';
import { PinnedAnnouncementList } from '../components/PinnedAnnouncementList';
import { MemoryRouter } from 'react-router-dom';

describe('AnnouncementAcknowledgement', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('updates acknowledgement button state after click', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(null, { status: 204 })));
    render(<AcknowledgementButton contentId="content-1" acknowledged={false} />);
    await userEvent.click(screen.getByRole('button', { name: '확인' }));
    await waitFor(() => expect(screen.getByRole('button', { name: '확인 완료' })).toBeDisabled());
  });

  it('renders pinned announcement state', () => {
    render(
      <MemoryRouter>
        <PinnedAnnouncementList
          announcements={[
            {
              id: '1',
              type: 'ANNOUNCEMENT',
              status: 'PUBLISHED',
              title: '보안 교육 안내',
              summary: '필수 확인',
              category: { id: 'c', name: '공지', slug: 'notice', sortOrder: 1, active: true },
              tags: [],
              author: { id: 'u', displayName: '관리자', email: 'admin@example.com', roles: ['ADMIN'] },
              pinned: true,
              requiresAcknowledgement: true,
              acknowledged: false,
              updatedAt: new Date().toISOString(),
            },
          ]}
        />
      </MemoryRouter>,
    );
    expect(screen.getByText('확인 필요')).toBeInTheDocument();
  });
});
