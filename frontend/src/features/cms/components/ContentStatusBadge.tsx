import { ContentStatus } from '../../../shared/api/openapiClient';
import { Badge } from '../../../shared/components/Badge';

const tones: Record<ContentStatus, 'neutral' | 'green' | 'amber' | 'red' | 'blue'> = {
  DRAFT: 'neutral',
  IN_REVIEW: 'amber',
  PUBLISHED: 'green',
  ARCHIVED: 'red',
};

export function ContentStatusBadge({ status }: { status: ContentStatus }) {
  return <Badge tone={tones[status]}>{status}</Badge>;
}
