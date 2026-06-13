import { Plus } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { CategoryDto, TagDto } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Field, TextInput } from '../../../shared/components/Form';
import { Table } from '../../../shared/components/Table';
import { createCategory, createTag, listCategories, listTags } from '../../cms/api/cmsContentApi';

export function TaxonomyManagementPage() {
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [tags, setTags] = useState<TagDto[]>([]);
  const [name, setName] = useState('');
  const [slug, setSlug] = useState('');
  const [tagName, setTagName] = useState('');

  function load() {
    listCategories().then(setCategories);
    listTags().then(setTags);
  }

  useEffect(load, []);

  async function submit(event: FormEvent) {
    event.preventDefault();
    await createCategory({ name, slug, sortOrder: categories.length + 1, active: true });
    setName('');
    setSlug('');
    load();
  }

  async function submitTag(event: FormEvent) {
    event.preventDefault();
    await createTag(tagName);
    setTagName('');
    load();
  }

  return (
    <div className="page">
      <section className="section">
        <div className="section-header">
          <h2>분류 관리</h2>
        </div>
        <form className="toolbar" onSubmit={submit}>
          <Field label="이름">
            <TextInput value={name} onChange={(event) => setName(event.target.value)} />
          </Field>
          <Field label="Slug">
            <TextInput value={slug} onChange={(event) => setSlug(event.target.value)} />
          </Field>
          <Button icon={<Plus size={16} />} variant="primary" type="submit">
            추가
          </Button>
        </form>
      </section>
      <section className="section">
        <Table columns={['이름', 'Slug', '정렬', '상태']}>
          {categories.map((category) => (
            <tr key={category.id}>
              <td>{category.name}</td>
              <td>{category.slug}</td>
              <td>{category.sortOrder}</td>
              <td>{category.active ? '활성' : '비활성'}</td>
            </tr>
          ))}
        </Table>
      </section>
      <section className="section">
        <div className="section-header">
          <h2>태그 관리</h2>
        </div>
        <form className="toolbar" onSubmit={submitTag}>
          <Field label="태그 이름">
            <TextInput value={tagName} onChange={(event) => setTagName(event.target.value)} />
          </Field>
          <Button icon={<Plus size={16} />} variant="primary" type="submit">
            태그 추가
          </Button>
        </form>
        <div className="toolbar">
          {tags.map((tag) => (
            <span className="badge badge-neutral" key={tag.id}>
              {tag.name}
            </span>
          ))}
        </div>
      </section>
    </div>
  );
}
