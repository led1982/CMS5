import { Upload } from 'lucide-react';
import { useState } from 'react';
import { Button } from '../../../shared/components/Button';

export function AttachmentUploader({ disabled = false }: { disabled?: boolean }) {
  const [fileName, setFileName] = useState('');
  return (
    <div className="section">
      <div className="section-header">
        <h2>첨부파일</h2>
        <Button icon={<Upload size={16} />} variant="secondary" disabled={disabled}>
          업로드
        </Button>
      </div>
      <input
        className="input"
        type="file"
        disabled={disabled}
        onChange={(event) => setFileName(event.target.files?.[0]?.name ?? '')}
      />
      <p className="muted">단일 파일 10MB, 요청 전체 20MB 제한</p>
      {fileName ? <BadgeReady filename={fileName} /> : null}
    </div>
  );
}

function BadgeReady({ filename }: { filename: string }) {
  return <p className="toast toast-info">{filename} 준비됨</p>;
}
