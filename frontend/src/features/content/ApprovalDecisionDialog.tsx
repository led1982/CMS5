import { Check, X } from "lucide-react";
import { useState } from "react";

export function ApprovalDecisionDialog({ open, onClose, title }: { open: boolean; onClose: () => void; title: string }) {
  const [decision, setDecision] = useState<"APPROVE" | "REJECT">("APPROVE");

  if (!open) {
    return null;
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <div className="dialog" role="dialog" aria-modal="true" aria-labelledby="approval-title">
        <h2 id="approval-title">{title}</h2>
        <div className="filters">
          <button className={`btn ${decision === "APPROVE" ? "secondary" : "ghost"}`} type="button" onClick={() => setDecision("APPROVE")}>
            <Check size={18} aria-hidden="true" />
            Approve
          </button>
          <button className={`btn ${decision === "REJECT" ? "destructive" : "ghost"}`} type="button" onClick={() => setDecision("REJECT")}>
            <X size={18} aria-hidden="true" />
            Reject
          </button>
        </div>
        <label className="editor-field" htmlFor="decision-comment">
          Decision comment
          <textarea id="decision-comment" rows={5} />
        </label>
        <div className="toolbar">
          <button className="btn ghost" type="button" onClick={onClose}>
            Cancel
          </button>
          <button className={decision === "APPROVE" ? "btn primary" : "btn destructive"} type="button" onClick={onClose}>
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
}

export function ApprovalDecisionDialogDemo() {
  const [open, setOpen] = useState(true);
  return (
    <>
      <button className="btn primary" type="button" onClick={() => setOpen(true)}>
        Open decision
      </button>
      <ApprovalDecisionDialog open={open} onClose={() => setOpen(false)} title="검토 결정" />
    </>
  );
}
