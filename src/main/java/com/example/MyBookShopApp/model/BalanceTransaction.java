package com.example.MyBookShopApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ser.std.SqlDateSerializer;
import com.fasterxml.jackson.databind.ser.std.SqlTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class BalanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(columnDefinition = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private Integer value = 0;

    @ManyToOne
    @JoinColumn(columnDefinition = "book_id", referencedColumnName = "id")
    private Book book;

    @Column(nullable = false)
    private String description;

    @Transient
    @JsonIgnore
    private List<Book> purchasedBookList;

    @Transient
    @JsonIgnore
    public String getPurchaseBookDescription() {
        if (description.contains("Покупка")) {
            String[] descriptionArr = description.split(" ");
            return descriptionArr[0] + " " + descriptionArr[1];
        }
        return "";
    }

    @Transient
    @JsonIgnore
    public String getDateForLocaleRu() {;
        Timestamp timestamp = new Timestamp(time.getTime());
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new java.util.Locale("ru"));
        return localDateTime.format(dateTimeFormatter);
    }

    @Transient
    @JsonIgnore
    public String getUserTransactionValue() {
        return value > 0 ? "+" + value + " р." : value + " р.";
    }
}
