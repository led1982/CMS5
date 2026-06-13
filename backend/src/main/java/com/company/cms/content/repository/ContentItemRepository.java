package com.company.cms.content.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.cms.content.domain.ContentItem;
import org.springframework.stereotype.Repository;

@Repository
public class ContentItemRepository {
    private final ConcurrentMap<UUID, ContentItem> store = new ConcurrentHashMap<>();

    public ContentItem save(ContentItem item) {
        store.put(item.getId(), item);
        return item;
    }

    public List<ContentItem> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<ContentItem> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(UUID id) {
        store.remove(id);
    }

    public boolean isEmpty() {
        return store.isEmpty();
    }
}
