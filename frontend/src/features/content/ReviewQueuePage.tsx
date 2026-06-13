import { CheckCircle2 } from "lucide-react";
import { useState } from "react";
import { StatusBadge } from "../../components/cms/StatusBadge";
import { listManagedContent } from "./contentApi";
import { ApprovalDecisionDialog } from "./ApprovalDecisionDialog";

export function ReviewQueuePage() {
  const queue = listManagedContent({ status: "IN_REVIEW" });
  const [selected, setSelected] = useState(queue[0]);
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Review Queue</h1>
          <p className="page-subtitle">검토 요청의 변경 요약, 첨부, 공개 대상을 확인하고 결정합니다.</p>
        </div>
      </section>
      <div className="split-detail">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Title</th>
                <th>Type</th>
                <th>Requester</th>
                <th>Changed</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {queue.map((item) => (
                <tr key={item.id} onClick={() => setSelected(item)}>
                  <td>{item.title}</td>
                  <td>{item.contentType}</td>
                  <td>{item.author}</td>
                  <td>{new Date(item.updatedAt).toLocaleDateString("ko-KR")}</td>
                  <td>
                    <StatusBadge status={item.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <aside className="panel">
          <h2>{selected?.title}</h2>
          <p className="muted">{selected?.summary}</p>
          <p>Audience: {selected?.audiences.join(", ")}</p>
          <p>Attachments: {selected?.attachments.length}</p>
          <button className="btn primary" type="button" onClick={() => setDialogOpen(true)}>
            <CheckCircle2 size={18} aria-hidden="true" />
            Decide
          </button>
        </aside>
      </div>
      <ApprovalDecisionDialog open={dialogOpen} onClose={() => setDialogOpen(false)} title={selected?.title ?? "검토 결정"} />
    </>
  );
}
