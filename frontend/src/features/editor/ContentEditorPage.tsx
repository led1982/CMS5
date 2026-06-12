import { Archive, CheckCircle2, Eye, FilePlus2, Rocket, Save, Send } from "lucide-react";
import { FormEvent, useEffect, useMemo, useState } from "react";
import { AudienceSummary, CategoryDto, ContentDetail, ContentPage, ContentType, TagDto, UserSummary, api } from "../../api/client";
import { can } from "../../auth/session";
import {
  approveContent,
  archiveContent,
  createDraft,
  getManagedContent,
  listAudiences,
  listCategories,
  listManagedContent,
  listTags,
  publishContent,
  submitForReview,
  updateDraft
} from "./contentEditorApi";

interface FormState {
  type: ContentType;
  title: string;
  summary: string;
  body: string;
  categoryId: string;
  tagIds: string[];
  audienceIds: string[];
  noticePriority: "LOW" | "NORMAL" | "HIGH" | "URGENT";
  requiresAcknowledgement: boolean;
}

const emptyForm: FormState = {
  type: "KNOWLEDGE",
  title: "",
  summary: "",
  body: "",
  categoryId: "",
  tagIds: [],
  audienceIds: [],
  noticePriority: "NORMAL",
  requiresAcknowledgement: false
};

export function ContentEditorPage() {
  const [contentPage, setContentPage] = useState<ContentPage | null>(null);
  const [selected, setSelected] = useState<ContentDetail | null>(null);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [tags, setTags] = useState<TagDto[]>([]);
  const [audiences, setAudiences] = useState<AudienceSummary[]>([]);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [tab, setTab] = useState<"edit" | "preview" | "toc">("edit");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  function loadList() {
    listManagedContent()
      .then(setContentPage)
      .catch((err: Error) => setError(err.message));
  }

  useEffect(() => {
    api<UserSummary>("/me")
      .then((user) => {
        if (!can(user.permissions, "CONTENT_WRITE")) {
          throw new Error("Permission required: CONTENT_WRITE");
        }
        loadList();
        return Promise.all([listCategories(), listTags(), listAudiences()]);
      })
      .then(([categoryResult, tagResult, audienceResult]) => {
        setCategories(categoryResult);
        setTags(tagResult);
        setAudiences(audienceResult);
        setForm((current) => ({
          ...current,
          categoryId: current.categoryId || categoryResult[0]?.id || "",
          audienceIds: current.audienceIds.length ? current.audienceIds : audienceResult[0] ? [audienceResult[0].id] : []
        }));
      })
      .catch((err: Error) => setError(err.message));
  }, []);

  function selectContent(contentId: string) {
    getManagedContent(contentId)
      .then((detail) => {
        setSelected(detail);
        setForm({
          type: detail.type,
          title: detail.title,
          summary: detail.summary,
          body: detail.body,
          categoryId: detail.category.id,
          tagIds: tags.filter((tag) => detail.tags.includes(tag.name)).map((tag) => tag.id),
          audienceIds: audiences.filter((audience) => detail.audiences.includes(audience.name)).map((audience) => audience.id),
          noticePriority: detail.notice?.priority ?? "NORMAL",
          requiresAcknowledgement: detail.notice?.requiresAcknowledgement ?? false
        });
        setMessage(null);
      })
      .catch((err: Error) => setError(err.message));
  }

  async function save(event: FormEvent) {
    event.preventDefault();
    const request = {
      type: form.type,
      title: form.title,
      summary: form.summary,
      body: form.body,
      categoryId: form.categoryId,
      tagIds: form.tagIds,
      audienceIds: form.audienceIds,
      notice: form.type === "NOTICE" ? { priority: form.noticePriority, requiresAcknowledgement: form.requiresAcknowledgement } : undefined
    };
    const detail = selected
      ? await updateDraft(selected.id, { ...request, changeNote: "Saved from editor" })
      : await createDraft(request);
    setSelected(detail);
    setMessage("Draft saved");
    loadList();
  }

  async function runAction(action: "submit" | "approve" | "publish" | "archive") {
    if (!selected) {
      return;
    }
    const detail =
      action === "submit" ? await submitForReview(selected.id) :
      action === "approve" ? await approveContent(selected.id) :
      action === "publish" ? await publishContent(selected.id) :
      await archiveContent(selected.id);
    setSelected(detail);
    setMessage(`Content ${action} complete`);
    loadList();
  }

  const headings = useMemo(() => form.body.split("\n").filter((line) => line.startsWith("#")).map((line) => line.replace(/^#+\s*/, "")), [form.body]);

  return (
    <div className="editor-layout">
      <aside className="panel">
        <div className="section-header">
          <h1 className="page-title">CMS</h1>
          <button type="button" className="secondary" onClick={() => { setSelected(null); setForm(emptyForm); }}>
            <FilePlus2 size={16} /> New
          </button>
        </div>
        {error && <p className="error-text" role="alert">{error}</p>}
        <div className="list">
          {!contentPage && <p className="meta">Loading content...</p>}
          {contentPage?.items.map((item) => (
            <button key={item.id} type="button" className="secondary" onClick={() => selectContent(item.id)}>
              <span>{item.title}</span>
              <span className={`badge ${item.status === "PUBLISHED" ? "success" : item.status === "DRAFT" ? "" : "warning"}`}>{item.status}</span>
            </button>
          ))}
        </div>
      </aside>

      <section className="panel">
        <div className="section-header">
          <div>
            <h2>{selected ? selected.title : "Create content"}</h2>
            <div className="meta">{selected ? `${selected.type} · ${selected.status} · v${selected.versionNumber}` : "Draft, submit, approve, publish, and archive content"}</div>
          </div>
          <div className="toolbar">
            <button type="button" className="secondary" onClick={() => setTab("edit")}><Save size={16} /> Edit</button>
            <button type="button" className="secondary" onClick={() => setTab("preview")}><Eye size={16} /> Preview</button>
            <button type="button" className="secondary" onClick={() => setTab("toc")}>TOC</button>
          </div>
        </div>

        {message && <div className="banner">{message}</div>}

        <form className="form-grid" onSubmit={save}>
          {tab === "edit" && (
            <>
              <label>
                <span>Title</span>
                <input value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} required />
              </label>
              <div className="grid three">
                <label>
                  <span>Type</span>
                  <select value={form.type} onChange={(event) => setForm({ ...form, type: event.target.value as ContentType })}>
                    <option value="KNOWLEDGE">Knowledge</option>
                    <option value="DOCUMENT">Document</option>
                    <option value="NOTICE">Notice</option>
                  </select>
                </label>
                <label>
                  <span>Category</span>
                  <select value={form.categoryId} onChange={(event) => setForm({ ...form, categoryId: event.target.value })} required>
                    {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
                  </select>
                </label>
                <label>
                  <span>Audience</span>
                  <select multiple value={form.audienceIds} onChange={(event) => setForm({ ...form, audienceIds: Array.from(event.target.selectedOptions).map((option) => option.value) })}>
                    {audiences.map((audience) => <option key={audience.id} value={audience.id}>{audience.name}</option>)}
                  </select>
                </label>
              </div>
              <label>
                <span>Summary</span>
                <input value={form.summary} onChange={(event) => setForm({ ...form, summary: event.target.value })} required />
              </label>
              <label>
                <span>Tags</span>
                <select multiple value={form.tagIds} onChange={(event) => setForm({ ...form, tagIds: Array.from(event.target.selectedOptions).map((option) => option.value) })}>
                  {tags.map((tag) => <option key={tag.id} value={tag.id}>{tag.name}</option>)}
                </select>
              </label>
              {form.type === "NOTICE" && (
                <div className="grid two">
                  <label>
                    <span>Priority</span>
                    <select value={form.noticePriority} onChange={(event) => setForm({ ...form, noticePriority: event.target.value as FormState["noticePriority"] })}>
                      <option value="LOW">Low</option>
                      <option value="NORMAL">Normal</option>
                      <option value="HIGH">High</option>
                      <option value="URGENT">Urgent</option>
                    </select>
                  </label>
                  <label>
                    <span>Acknowledgement</span>
                    <select value={form.requiresAcknowledgement ? "yes" : "no"} onChange={(event) => setForm({ ...form, requiresAcknowledgement: event.target.value === "yes" })}>
                      <option value="no">Not required</option>
                      <option value="yes">Required</option>
                    </select>
                  </label>
                </div>
              )}
              <label>
                <span>Markdown Body</span>
                <textarea value={form.body} onChange={(event) => setForm({ ...form, body: event.target.value })} required />
              </label>
            </>
          )}

          {tab === "preview" && <div className="markdown-preview">{form.body || "Nothing to preview."}</div>}
          {tab === "toc" && (
            <ol className="panel">
              {headings.length === 0 && <li>No headings detected.</li>}
              {headings.map((heading) => <li key={heading}>{heading}</li>)}
            </ol>
          )}

          <div className="toolbar">
            <button type="submit"><Save size={16} /> Save Draft</button>
            <button type="button" className="secondary" disabled={!selected} onClick={() => runAction("submit")}><Send size={16} /> Submit</button>
            <button type="button" className="secondary" disabled={!selected} onClick={() => runAction("approve")}><CheckCircle2 size={16} /> Approve</button>
            <button type="button" className="secondary" disabled={!selected} onClick={() => runAction("publish")}><Rocket size={16} /> Publish</button>
            <button type="button" className="danger" disabled={!selected} onClick={() => runAction("archive")}><Archive size={16} /> Archive</button>
          </div>
        </form>
      </section>
    </div>
  );
}
