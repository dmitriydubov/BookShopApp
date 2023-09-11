package com.example.MyBookShopApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "book_review")
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String text;

    @OneToMany(mappedBy = "bookReview")
    @JsonIgnore
    private List<BookReviewLike> likes;

    @Transient
    @JsonIgnore
    private List<String> ratingStars;

    //==================================================================================================================
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<BookReviewLike> getLikes() {
        return likes;
    }

    public void setLikes(List<BookReviewLike> likes) {
        this.likes = likes;
    }

    public List<String> getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(List<String> ratingStars) {
        this.ratingStars = ratingStars;
    }
}
