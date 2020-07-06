# friendly-barnacle

# Bookstore App Design Project - README

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app aims to help independent, local booksellers by recommending books to users based on a profile the user creates. The independent booksellers and small bookstores add details for their books that are used to match books with readers' preferences. The books are recommended based on the user's profile and availability at local bookstores. Users can then add these books to a wishlist to buy later or send the list to friends or family as gift ideas. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Books/shopping
- **Mobile:** Mobile makes the app easier to use more often for short amounts of time. The user could open the app on their commute home, for instance. In addition, when the user wants to go buy books, they can use the maps feature to navigate to the bookstore. The app's ability to know the user's location in real-time no matter where they go is also helpful for the navigation.
- **Story:** Buying from independent, local booksellers supports the user's community and local economy, and with so many people staying at home right now, some small booksellers are struggling.
- **Market:** Adults and young adults who enjoy reading or want to read more while stuck at home. It provides value for those who want to read but don't know what to read. In addition, independent bookstores would use this app often. 
- **Habit:** The home screen for users is an infinitely scrolling list of books that match the user's profile starting with the best match. In addition, the user could go back and change their profile to search for gifts for a family member or friend.
- **Scope:** This is a resonable scope project. The required features are similar to other apps in this course, especially Instagram. First, the app would allow users and bookstores to create an account and users can create a profile. Bookstores could add books that appear on the user's home screen. Then the app would incorporate logging in and out and an algorithm to match the user's profile to books based on their preferences and location. Next, I would add a way for users to add books to a wishlist and see details (including location) of the bookstore. V4 would incorporate sending wishlists to others, a bookstore's hours/curbside pick-up policy, contact info, website. Also using the Goodreads API for review and book ratings, and maybe the ability to "like" a book?

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The user can create an account and fill in a profile describing their preferences and location.
* The user can log in and out 
* The user can edit their preferences in their profile
* The user's home screen shows a list of books matching their preferences and near their location
* The user can add a book to their wishlist
* The user can view their wishlist
* The user can click on a book to be taken to a detail view of the book that includes the location of the store that sells the book in Google Maps
* The bookseller can create an account with their location
* The bookseller can log in and out of their account
* The bookseller can add books to the database
* ...

**Optional Nice-to-have Stories**

* The user can remove books from their wishlist
* The bookseller can add information to their profile including curbside pick-up, availible hours
* The user can send their wishlist to friends
* The user can rank their preferences
* The user can search for books by title
* The Goodreads API provides reviews and star-ratings for each book
* ...

### 2. Screen Archetypes

* User registration screen
   * The user can create an account and fill in a profile describing their preferences and location.
* Bookseller registration screen
   * The bookseller can create an account with their location
* Log in screen
   * The user/bookseller can log in and out 
* Profile screen
    * The user can edit their preferences in their profile
* Recommendations/"stream" screen
    * The user's home screen shows a list of books matching their preferences and near their location
* Book detail view
    * The user can click on a book to be taken to a detail view of the book that includes the location of the store that sells the book in Google Maps
    * The user can add a book to their wishlist
* Wishlist screen
    * The user can view their wishlist
* Add book screen
    * The bookseller can add books to the database

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home feed
* Search book (optional)
* View/edit profile

**Flow Navigation** (Screen to Screen)

* User registration screen
    * => Home/recommendations screen
* Bookseller registration screen
    * => Add book screen
* Log in screen
    * => Home/recommendations screen
* Profile screen
    * => Wishlist screen 
* Recommendations/"stream" screen
    * => Book detail screen
* Book detail screen
    * => None
* Wishlist screen
    * => None
* Add book screen
    * => Add book screen again
