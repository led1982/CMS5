import { Eye, Save, Send } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CategoryDto, ContentMutationRequest, ContentType } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Field, TextArea, TextInput } from '../../../shared/components/Form';
import { Toast } from '../../../shared/components/Toast';
import { createContent, listCategories, submitContent } from '../api/cmsContentApi';
import { AttachmentUploader } from '../components/AttachmentUploader';

const defaultCategoryId = '40000000-0000-0000-0000-000000000002';

export function ContentEditorPage() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<CategoryDto[]>([]);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => setCategories([]));
  }, []);

  return (
    <ContentEditorForm
      categories={categories}
      onSaved={(contentId) => navigate(`/cms/contents/${contentId}/edit`)}
    />
  );
}

export function ContentEditorForm({
  categories,
  onSaved,
}: {
  categories: CategoryDto[];
  onSaved?: (contentId: string) => void;
}) {
  const [type, setType] = useState<ContentType>('KNOWLEDGE');
  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [body, setBody] = useState('');
  const [tags, setTags] = useState('HR, 업무');
  const [requiresAcknowledgement, setRequiresAcknowledgement] = useState(false);
  const [pinned, setPinned] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [preview, setPreview] = useState(false);

  const categoryId = categories[0]?.id ?? defaultCategoryId;
  const canSubmit = title.trim().length > 0 && body.trim().length > 0;

  function payload(): ContentMutationRequest {
    return {
      type,
      title,
      summary,
      body,
      categoryId,
      tags: tags.split(',').map((tag) => tag.trim()).filter(Boolean),
      audiences: [{ visibilityType: 'ALL_EMPLOYEES' }],
      pinned,
      requiresAcknowledgement: type === 'ANNOUNCEMENT' && requiresAcknowledgement,
      changeNote: 'Initial draft',
    };
  }

  async function save(event: FormEvent) {
    event.preventDefault();
    if (!canSubmit) {
      setError('제목과 본문은 필수입니다.');
      return;
    }
    const content = await createContent(payload());
    setMessage(`초안 저장 완료: ${content.status}`);
    setError('');
    onSaved?.(content.id);
  }

  async function saveAndSubmit() {
    if (!canSubmit) {
      setError('제목과 본문은 필수입니다.');
      return;
    }
    const content = await createContent(payload());
    const submitted = await submitContent(content.id, 'Ready for review');
    setMessage(`검토 요청 완료: ${submitted.status}`);
    setError('');
    onSaved?.(submitted.id);
  }

  return (
    <form className="page" onSubmit={save}>
      {message ? <Toast>{message}</Toast> : null}
      {error ? <Toast tone="error">{error}</Toast> : null}
      <div className="grid-2">
        <section className="section">
          <div className="section-header">
            <h2>콘텐츠 작성</h2>
            <div className="toolbar">
              <Button icon={<Eye size={16} />} type="button" onClick={() => setPreview((value) => !value)}>
                미리보기
              </Button>
              <Button icon={<Save size={16} />} type="submit" variant="secondary">
                저장
              </Button>
              <Button icon={<Send size={16} />} type="button" variant="primary" onClick={saveAndSubmit}>
                검토 요청
              </Button>
            </div>
          </div>
          <div className="form-grid">
            <Field label="제목" error={!title.trim() && error ? '필수 입력값입니다.' : undefined}>
              <TextInput value={title} onChange={(event) => setTitle(event.target.value)} maxLength={150} />
            </Field>
            <Field label="요약">
              <TextInput value={summary} onChange={(event) => setSummary(event.target.value)} maxLength={500} />
            </Field>
            <Field label="본문">
              <TextArea value={body} onChange={(event) => setBody(event.target.value)} />
            </Field>
          </div>
        </section>

        <aside className="section">
          <h2>메타데이터</h2>
          <div className="form-grid">
            <Field label="유형">
              <select className="select" value={type} onChange={(event) => setType(event.target.value as ContentType)}>
                <option value="KNOWLEDGE">KNOWLEDGE</option>
                <option value="DOCUMENT">DOCUMENT</option>
                <option value="ANNOUNCEMENT">ANNOUNCEMENT</option>
              </select>
            </Field>
            <Field label="카테고리">
              <select className="select" value={categoryId} onChange={() => undefined}>
                <option value={categoryId}>{categories[0]?.name ?? '업무가이드'}</option>
              </select>
            </Field>
            <Field label="태그">
              <TextInput value={tags} onChange={(event) => setTags(event.target.value)} />
            </Field>
            <label className="toolbar">
              <input type="checkbox" checked={pinned} onChange={(event) => setPinned(event.target.checked)} />
              상단 고정
            </label>
            <label className="toolbar">
              <input
                type="checkbox"
                checked={requiresAcknowledgement}
                disabled={type !== 'ANNOUNCEMENT'}
                onChange={(event) => setRequiresAcknowledgement(event.target.checked)}
              />
              확인 필수
            </label>
          </div>
        </aside>
      </div>
      <AttachmentUploader />
      {preview ? (
        <section className="section">
          <div className="section-header">
            <h2>미리보기</h2>
          </div>
          <div className="markdown-preview">{body || '본문이 없습니다.'}</div>
        </section>
      ) : null}
    </form>
  );
}
