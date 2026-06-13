import { Eye, Save, Send } from "lucide-react";
import { useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { ContentTypeBadge, StatusBadge } from "../../components/cms/StatusBadge";
import { categories } from "../../data/mockCms";
import { getManagedContent } from "./contentApi";

type EditorTab = "edit" | "preview" | "toc";

export function ContentEditorPage() {
  const { contentId } = useParams();
  const source = getManagedContent(contentId);
  const [title, setTitle] = useState(contentId ? source.title : "");
  const [summary, setSummary] = useState(contentId ? source.summary : "");
  const [body, setBody] = useState(contentId ? source.body : "## 제목\n\n본문을 입력하세요.");
  const [tab, setTab] = useState<EditorTab>("edit");
  const headings = useMemo(() => body.split("\n").filter((line) => line.startsWith("#")).map((line) => line.replace(/^#+\s*/, "")), [body]);

  return (
    <>
      <section className="section-header">
        <div>
          <div className="badge-row">
            <ContentTypeBadge type={source.contentType} />
            <StatusBadge status={contentId ? source.status : "DRAFT"} />
          </div>
          <h1 className="page-title">{contentId ? "콘텐츠 편집" : "콘텐츠 작성"}</h1>
        </div>
        <div className="filters">
          <button className="btn ghost" type="button">
            <Eye size={18} aria-hidden="true" />
            Preview
          </button>
          <button className="btn primary" type="button">
            <Send size={18} aria-hidden="true" />
            Submit
          </button>
        </div>
      </section>

      <div className="editor-layout">
        <aside className="panel">
          <div className="editor-field">
            <label htmlFor="content-type">Type</label>
            <select id="content-type" defaultValue={source.contentType}>
              <option>ARTICLE</option>
              <option>DOCUMENT</option>
              <option>NOTICE</option>
            </select>
          </div>
          <div className="editor-field">
            <label htmlFor="category">Category</label>
            <select id="category" defaultValue={source.category.id}>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
          <div className="editor-field">
            <label htmlFor="audience">Audience</label>
            <select id="audience" defaultValue="ALL_EMPLOYEES">
              <option>ALL_EMPLOYEES</option>
              <option>DEPARTMENT</option>
              <option>ROLE</option>
              <option>USER</option>
            </select>
          </div>
          <div className="editor-field">
            <label htmlFor="change-summary">Change summary</label>
            <textarea id="change-summary" rows={4} placeholder="변경 요약" />
          </div>
        </aside>

        <section className="panel">
          <div className="editor-field">
            <label htmlFor="title">Title</label>
            <input id="title" value={title} onChange={(event) => setTitle(event.target.value)} />
          </div>
          <div className="editor-field">
            <label htmlFor="summary">Summary</label>
            <textarea id="summary" rows={3} value={summary} onChange={(event) => setSummary(event.target.value)} />
          </div>
          <div className="editor-tabs" role="tablist" aria-label="Editor views">
            {(["edit", "preview", "toc"] as EditorTab[]).map((item) => (
              <button key={item} className={`btn ${tab === item ? "secondary" : "ghost"}`} type="button" onClick={() => setTab(item)}>
                {item.toUpperCase()}
              </button>
            ))}
          </div>
          {tab === "edit" ? (
            <textarea className="preview-body" aria-label="Markdown editor" value={body} onChange={(event) => setBody(event.target.value)} />
          ) : null}
          {tab === "preview" ? (
            <div className="preview-body">
              {body.split("\n").map((line, index) => (line.startsWith("## ") ? <h2 key={index}>{line.replace("## ", "")}</h2> : <p key={index}>{line}</p>))}
            </div>
          ) : null}
          {tab === "toc" ? (
            <div className="preview-body">
              {headings.length ? headings.map((heading) => <p key={heading}>{heading}</p>) : <p className="muted">목차 없음</p>}
            </div>
          ) : null}
        </section>
      </div>

      <div className="save-bar">
        <span className="muted">마지막 저장: 방금 전</span>
        <button className="btn secondary" type="button">
          <Save size={18} aria-hidden="true" />
          Save Draft
        </button>
      </div>
    </>
  );
}
