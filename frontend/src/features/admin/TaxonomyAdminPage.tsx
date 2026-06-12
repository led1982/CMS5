import { Plus, Tags } from "lucide-react";
import { FormEvent, useEffect, useState } from "react";
import { CategoryDto, TagDto, api } from "../../api/client";

export function TaxonomyAdminPage() {
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [tags, setTags] = useState<TagDto[]>([]);
  const [name, setName] = useState("");
  const [slug, setSlug] = useState("");
  const [error, setError] = useState<string | null>(null);

  function load() {
    Promise.all([api<CategoryDto[]>("/admin/categories"), api<TagDto[]>("/admin/tags")])
      .then(([categoryResult, tagResult]) => {
        setCategories(categoryResult);
        setTags(tagResult);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }

  useEffect(load, []);

  async function createCategory(event: FormEvent) {
    event.preventDefault();
    await api<CategoryDto>("/admin/categories", {
      method: "POST",
      body: JSON.stringify({ name, slug, sortOrder: categories.length * 10 })
    });
    setName("");
    setSlug("");
    load();
  }

  return (
    <div className="grid">
      <h1 className="page-title">Taxonomy</h1>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <div className="grid two">
        <section className="panel">
          <div className="section-header">
            <h2>Categories</h2>
            <span className="meta">{categories.length} active</span>
          </div>
          <form className="toolbar" onSubmit={createCategory}>
            <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Category name" required />
            <input value={slug} onChange={(event) => setSlug(event.target.value)} placeholder="category-slug" required />
            <button type="submit"><Plus size={16} /> Add</button>
          </form>
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Name</th><th>Slug</th><th>Sort</th><th>Status</th></tr>
              </thead>
              <tbody>
                {categories.map((category) => (
                  <tr key={category.id}>
                    <td>{category.name}</td>
                    <td>{category.slug}</td>
                    <td>{category.sortOrder}</td>
                    <td><span className={`badge ${category.active ? "success" : "danger"}`}>{category.active ? "Active" : "Inactive"}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
        <section className="panel">
          <div className="section-header">
            <h2>Tags</h2>
            <Tags size={18} aria-hidden="true" />
          </div>
          <div className="list">
            {tags.map((tag) => (
              <div className="card" key={tag.id}>
                <strong>{tag.name}</strong>
                <div className="meta">{tag.slug}</div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
