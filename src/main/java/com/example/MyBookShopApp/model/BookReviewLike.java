package com.example.MyBookShopApp.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "book_review_like")
public class BookReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id", nullable = false)
    private BookReview bookReview;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private Integer value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BookReview getBookReview() {
        return bookReview;
    }

    public void setBookReview(BookReview bookReview) {
        this.bookReview = bookReview;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
