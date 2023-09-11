package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    @Query("from Book book order by book.pubDate asc")
    Page<Book> getAllBooksOrderingByPubDate(Pageable nextPage);

    Page<Book> findByPubDateBetweenOrderByPubDateAsc(Date dateFrom, Date dateTo, Pageable nextPage);

    Page<Book> findByPubDateBeforeOrderByPubDateAsc(Date dateTo, Pageable nextPage);

    Page<Book> findByPubDateAfterOrderByPubDateAsc(Date dateFrom, Pageable nextPage);

    @Query(value = "SELECT * FROM books " +
                   "ORDER BY purchase_number + (0.7 * cart_number) + (0.4 * postponed_number) DESC",
                   nativeQuery = true)
    Page<Book> getPopularBooks(Pageable nextPage);

    Page<Book> findBookByTag(Tag tag, Pageable nextPage);

    List<Book> findBookByTag(Tag tag);

    @Query("SELECT b FROM Book b " +
           "JOIN b.book2Genre b2g " +
           "JOIN b2g.genre g " +
           "WHERE g.id = :genreId")
    Page<Book> findByGenreId(@Param("genreId") Integer genreId, Pageable nextPage);

    @Query("SELECT b.id FROM Book b " +
            "JOIN b.book2Genre b2g " +
            "JOIN b2g.genre g " +
            "WHERE g.id = :genreId")
    List<Integer> findBookIdsByGenreId(@Param("genreId") Integer genreId);

    @Query("SELECT b FROM Book b WHERE b.id IN :bookIds ORDER BY b.pubDate DESC")
    Page<Book> findBooksByIds(@Param("bookIds") List<Integer> bookIds, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.id IN :bookIds")
    List<Book> findBooksByIds(@Param("bookIds") List<Integer> bookIds);

    @Query("SELECT b FROM Book b " +
           "JOIN b.book2Author b2a " +
           "JOIN b2a.author a " +
           "WHERE a.slug = :slug")
    Page<Book> findByAuthorSlug(String slug, Pageable nextPage);

    @Query("SELECT b FROM Book b " +
           "JOIN b.book2Author b2a " +
           "JOIN b2a.author a " +
           "WHERE a.id = :id ORDER BY b.pubDate DESC")
    Page<Book> findByAuthorId(Integer id, Pageable nextPage);

    @Query("SELECT COUNT(*) FROM Book b " +
           "JOIN b.book2Author b2a " +
           "JOIN b2a.author a " +
           "WHERE a.slug = :slug")
    Integer getCountOfBooksByAuthorSlug(String slug);

    Book findBySlug(String slug);

    List<Book> findBySlugIn(String[] slugs);

    @Query("SELECT b FROM Book b LEFT JOIN b.bookRating r GROUP BY b ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    Page<Book> findAllByRating(Pageable nextPage);

    @Query("SELECT b FROM Book b JOIN b.book2User b2u JOIN b2u.user u WHERE u.id = :userId")
    List<Book> getAllUserBookByUserId(Integer userId);
}
