import { Plus } from "lucide-react";
import { categories } from "../../data/mockCms";

export function TaxonomyManagerPage() {
  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Taxonomy</h1>
          <p className="page-subtitle">카테고리와 태그 활성 상태를 관리합니다.</p>
        </div>
        <button className="btn primary" type="button">
          <Plus size={18} aria-hidden="true" />
          Category
        </button>
      </section>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Slug</th>
              <th>Description</th>
              <th>Status</th>
              <th>Sort</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>{category.name}</td>
                <td>{category.slug}</td>
                <td>{category.description}</td>
                <td>
                  <span className={category.isActive ? "badge success" : "badge neutral"}>{category.isActive ? "ACTIVE" : "INACTIVE"}</span>
                </td>
                <td>{category.sortOrder}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
