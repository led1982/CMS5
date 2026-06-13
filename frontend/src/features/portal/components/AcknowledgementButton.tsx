import { CheckCircle2 } from 'lucide-react';
import { useState } from 'react';
import { acknowledgeAnnouncement } from '../api/announcementApi';
import { Button } from '../../../shared/components/Button';

export function AcknowledgementButton({
  contentId,
  acknowledged,
}: {
  contentId: string;
  acknowledged: boolean;
}) {
  const [done, setDone] = useState(acknowledged);
  const [busy, setBusy] = useState(false);

  async function handleClick() {
    setBusy(true);
    await acknowledgeAnnouncement(contentId);
    setDone(true);
    setBusy(false);
  }

  return (
    <Button icon={<CheckCircle2 size={16} />} variant={done ? 'secondary' : 'primary'} disabled={done || busy} onClick={handleClick}>
      {done ? '확인 완료' : busy ? '처리 중' : '확인'}
    </Button>
  );
}
