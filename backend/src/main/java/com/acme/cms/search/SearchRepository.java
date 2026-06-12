package com.acme.cms.search;

import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.model.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<ContentItem> search(String query, ContentType type, UUID categoryId, String tag) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder jpql = new StringBuilder("""
            select distinct c
            from ContentItem c
            left join c.tags t
            where c.status = :status
              and (
                lower(c.title) like :query
                or lower(c.summary) like :query
                or exists (
                  select v.id from ContentVersion v
                  where v.contentItem = c and lower(v.body) like :query
                )
              )
            """);
        params.put("status", ContentStatus.PUBLISHED);
        params.put("query", "%" + query.toLowerCase() + "%");

        if (type != null) {
            jpql.append(" and c.type = :type");
            params.put("type", type);
        }
        if (categoryId != null) {
            jpql.append(" and c.category.id = :categoryId");
            params.put("categoryId", categoryId);
        }
        if (tag != null && !tag.isBlank()) {
            jpql.append(" and lower(t.name) = :tag");
            params.put("tag", tag.toLowerCase());
        }
        jpql.append(" order by c.updatedAt desc");

        var typedQuery = entityManager.createQuery(jpql.toString(), ContentItem.class);
        params.forEach(typedQuery::setParameter);
        typedQuery.setMaxResults(500);
        return typedQuery.getResultList();
    }
}
