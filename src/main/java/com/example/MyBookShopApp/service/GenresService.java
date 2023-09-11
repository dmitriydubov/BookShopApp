package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Genre;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenresService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CalculateDiscountService calculateDiscountService;
    private final AuthorService authorService;

    @Autowired
    public GenresService(GenreRepository genreRepository,
                         BookRepository bookRepository,
                         CalculateDiscountService calculateDiscountService,
                         AuthorService authorService) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.calculateDiscountService = calculateDiscountService;
        this.authorService = authorService;
    }

    public List<Genre> getGenres() {
        List<Genre> genreList = genreRepository.findAll();
        genreList.forEach(genre -> {
            genre.setSubGenres(genreRepository.findByParentId(genre.getId()));
            genre.setBooksCount(genresCount(genre.getId()));
        });
        return genreList;
    }

    public Integer genresCount(Integer genreId) {
        List<Integer> booksIds = bookRepository.findBookIdsByGenreId(genreId);
        List<Integer> subGenresIdsList = getSubGenresIds(genreId);

        subGenresIdsList.forEach(subGenreId -> booksIds.addAll(bookRepository.findBookIdsByGenreId(subGenreId)));
        return bookRepository.findBooksByIds(booksIds).size();
    }

    public Genre getGenre(String slug) {
        return genreRepository.findBySlug(slug);
    }

    public List<String> getParentTitleGenres(String slug) {
        List<String> parentsGenres = new ArrayList<>();

        class ParentSearcher {
            void findParentGenre(Genre genre) {
                if (genre.getParentId() != null) {
                    Genre parentGenre = genreRepository.findGenreById(genre.getParentId());
                    parentsGenres.add(parentGenre.getName());
                    findParentGenre(parentGenre);
                }
            }
        }

        ParentSearcher parentSearcher = new ParentSearcher();
        parentSearcher.findParentGenre(genreRepository.findBySlug(slug));
        Collections.reverse(parentsGenres);

        return parentsGenres;
    }

    public Page<Book> getBooksByGenre(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(
                bookRepository.findByGenreId(genreRepository.findBySlug(slug).getId(), nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Page<Book> getBooksByGenreId(Integer genreId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> booksIds = bookRepository.findBookIdsByGenreId(genreId);
        List<Integer> subGenresIdsList = getSubGenresIds(genreId);

        subGenresIdsList.forEach(subGenreId -> booksIds.addAll(bookRepository.findBookIdsByGenreId(subGenreId)));

        Page<Book> bookPage = authorService.setBookAuthor(bookRepository.findBooksByIds(booksIds, nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    private List<Integer> getSubGenresIds(Integer genreId) {
        List<Genre> subGenres = new ArrayList<>();
        Optional<Genre> optional = genreRepository.findById(genreId);
        Genre genre = new Genre();
        if (optional.isPresent()) {
            genre = optional.get();
        }

        genre.setSubGenres(genreRepository.findByParentId(genre.getId()));

        class RecursiveSubGenresSearcher {
            void recursiveSearch(Genre subGenre) {
                subGenre.setSubGenres(genreRepository.findByParentId(subGenre.getId()));
                subGenres.add(subGenre);
                if (subGenre.getSubGenres().size() != 0) {
                    subGenre.getSubGenres().forEach(this::recursiveSearch);
                }
            }
        }
        RecursiveSubGenresSearcher recursiveSubGenresSearcher = new RecursiveSubGenresSearcher();
        genre.getSubGenres().forEach(recursiveSubGenresSearcher::recursiveSearch);

        return subGenres.stream().map(Genre::getId).collect(Collectors.toList());
    }
}
