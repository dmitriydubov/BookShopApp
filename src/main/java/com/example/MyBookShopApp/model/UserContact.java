package com.example.MyBookShopApp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_contact")
@Getter
@Setter
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private ContactType type;

    @Column(nullable = false)
    private Integer approved;
//
//    private String code;
//
//    @Column(name = "code_trials")
//    private Integer codeTrials;
//
//    @Column(name = "code_time")
//    private Date codeTime;

    @Column(nullable = false)
    private String contact;
}
