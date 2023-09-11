package com.example.MyBookShopApp.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String hash;

    @Column(name = "reg_time", nullable = false)
    private Date regTime;

    @Column(name = "balance", nullable = false)
    private Integer balance = 0;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Book2User> bookList;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookReview> bookReview;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookReviewLike> likes;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserContact> userContacts;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Book2User> book2User;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(hash, user.hash) && Objects.equals(regTime, user.regTime) && Objects.equals(balance, user.balance) && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(bookList, user.bookList) && Objects.equals(bookReview, user.bookReview) && Objects.equals(likes, user.likes) && Objects.equals(userContacts, user.userContacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hash, regTime, balance, name, password);
    }
}
