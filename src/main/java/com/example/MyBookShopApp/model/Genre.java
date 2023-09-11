package com.example.MyBookShopApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Transient
    @JsonIgnore
    private List<Genre> subGenres;

    @Transient
    @JsonIgnore
    private Integer booksCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Genre> getSubGenres() {
        return subGenres;
    }

    public void setSubGenres(List<Genre> subGenres) {
        this.subGenres = subGenres;
    }

    public Integer getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id) && Objects.equals(parentId, genre.parentId) && Objects.equals(slug, genre.slug) && Objects.equals(name, genre.name) && Objects.equals(subGenres, genre.subGenres) && Objects.equals(booksCount, genre.booksCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, slug, name, subGenres, booksCount);
    }
}
