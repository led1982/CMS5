package com.company.cms.admin.domain;

import java.util.UUID;

public class Category {
    private UUID id = UUID.randomUUID();
    private UUID parentId;
    private String name;
    private String slug;
    private String description;
    private int sortOrder;
    private boolean active = true;

    public Category() {
    }

    public Category(UUID id, String name, String slug, String description, int sortOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public UUID getId() {
        return id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class Tag {
        private UUID id = UUID.randomUUID();
        private String name;
        private String normalizedName;
        private boolean active = true;

        public Tag() {
        }

        public Tag(String name) {
            this.name = name;
            this.normalizedName = name == null ? null : name.trim().toLowerCase();
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getNormalizedName() {
            return normalizedName;
        }

        public boolean isActive() {
            return active;
        }
    }
}
