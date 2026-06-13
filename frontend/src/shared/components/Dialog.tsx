import { ReactNode } from 'react';
import { X } from 'lucide-react';
import { Button } from './Button';

export function Dialog({
  title,
  open,
  onClose,
  children,
}: {
  title: string;
  open: boolean;
  onClose: () => void;
  children: ReactNode;
}) {
  if (!open) {
    return null;
  }

  return (
    <div className="dialog-backdrop" role="presentation">
      <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="dialog-title">
        <header>
          <h2 id="dialog-title">{title}</h2>
          <Button icon={<X size={16} />} variant="ghost" onClick={onClose} aria-label="닫기">
            닫기
          </Button>
        </header>
        {children}
      </section>
    </div>
  );
}
