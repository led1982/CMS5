import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { ContentStatusBadge } from '../components/ContentStatusBadge';
import { ContentEditorForm } from '../pages/ContentEditorPage';

describe('ContentEditor', () => {
  it('renders validation when required fields are missing', async () => {
    render(<ContentEditorForm categories={[]} />);
    await userEvent.click(screen.getByRole('button', { name: /저장/i }));
    expect(screen.getByText('제목과 본문은 필수입니다.')).toBeInTheDocument();
  });

  it('renders status badge text', () => {
    render(<ContentStatusBadge status="IN_REVIEW" />);
    expect(screen.getByText('IN_REVIEW')).toBeInTheDocument();
  });
});
