package com.example.localink;

import android.content.Context;

import com.example.localink.Fragments.RecommendationsFragment;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.cglib.core.Local;
import org.mockito.runners.MockitoJUnitRunner;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    RecommendationsFragment mockRecFragment;


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    // Attempt at making unit test for the recommendations - not working yet
    @Test
    public void recommendations_areCorrect() {
        ParseObject.registerSubclass(Book.class);

        mockRecFragment = Mockito.mock(RecommendationsFragment.class);
        ParseUser user = Mockito.mock(ParseUser.class);

        List<Book> books = new ArrayList<>();

        Book book1 = new Book();
        book1.setGenres(Arrays.asList("Fantasy", "Adventure"));
        book1.setAgeRange("Young Adult");
        books.add(book1);

        Book book2 = new Book();
        book2.setGenres(Arrays.asList("Chic Lit", "Adventure"));
        book2.setAgeRange("Adult");
        books.add(book2);

        Book book3 = new Book();
        book3.setGenres(Arrays.asList("Fantasy"));
        book3.setAgeRange("Middle Grade");
        books.add(book3);

        List<Book> perfectMatchBooks = mockRecFragment.getPerfectMatchBooks(books, new LocalInkUser(user));

        String firstBookTitle = perfectMatchBooks.get(0).getTitle();

        assertEquals(firstBookTitle, "Children of Blood and Bone");
    }
}