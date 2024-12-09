## Library Management System

Library Management System Project for INT 2204 7 N1. 
Contributor include: Tran Thanh Dat, Le Minh Dat, Hoang Ngoc Nhi and Nguyen Tue Minh
# LMS - Library Management System

A robust Library Management System (LMS) developed in Java with JavaFX and SQLite, aimed at managing books, authors, users, and book borrowing processes through an intuitive graphical interface.

## Features

- **User Management**: Register, login, and manage user accounts.
- **Book Management**: Add, edit, delete, and search for books.
- **Author Management**: Assign authors to books and manage author profiles.
- **Borrowing System**: Track book loans and due dates for library users.

## Project Structure

The project follows a **Model-View-Controller (MVC)** pattern to organize code effectively. Below is a breakdown of each major package and its purpose:

### 1. `src/main/java/com/library`

- **`App.java`**: Main entry point for launching the application using JavaFX.
- **`Controller` Package**: Contains controllers that handle UI interactions for each primary screen in the app.
  - `LoginController.java`: Manages login view and handles authentication.
  - `AddNewBookController.java`: Handles adding new books, including author assignment.
  - `BorrowController.java`: Manages book borrowing functionality and user-book assignments.
- **`Model` Package**: Represents core data models of the system.
  - `Book.java`: Represents a book entity, with fields such as `title`, `author`, `ISBN`, etc.
  - `Author.java`: Defines author data with properties like `name` and `biography`.
  - `User.java`: Represents users who can borrow books, including admin and regular user types.
- **`Services` Package**: Data Access Objects for managing database interactions.
  - `BookDAO.java`: Contains CRUD operations for books in SQLite.
  - `AuthorDAO.java`: Handles author-related database interactions.
  - `UserDAO.java`: Manages user information, including user authentication.
- **`Utils` Package**: Utility classes and helper methods.
  - `DatabaseConnection.java`: Manages SQLite database connections.
  - `DateUtil.java`: Utility for handling date formatting for book borrowing.

### 2. `src/main/resources`

- **FXML Files**: Layout files defining the UI structure for JavaFX views.
  - `login.fxml`: Layout for user login screen.
  - `main.fxml`: Main dashboard UI layout for managing books, authors, and users.
  - `addBook.fxml`: Layout for adding or editing book entries.
- **Database**: SQLite database file (`library.db`) that stores all LMS data, located in the `resources` folder.

### 3. `src/test/java/com/library`

Contains unit tests for core functionalities:
- **DAO Tests**: Tests for CRUD operations in `BookDAO`, `UserDAO`, and `AuthorDAO`.
- **Controller Tests**: Tests to verify UI interactions in controllers like `LoginController`.

## Requirements

- **Java 11** or higher
- **JavaFX SDK**
- **SQLite**

## Setup Instructions

1. **Clone the repository**:
```bash
   git clone https://github.com/shoraaa/lms.git
   cd lms
```
2. **Install dependencies using Maven**:
```bash
   mvn install
```

## Usage

1. **Run the application** 
```bash
mvn javafx:run
```

## Contributing
1. Fork the repository
2. Create a feature branch
```bash
git checkout -b feature-branch
```
3. Commit changes
```bash
git commit -m "Feature description"
```
4. Push changes to your branch
```bash
git push origin feature-branch
```
5. Open a pull request for review

## License 
This project is licensed under the MIT License.

## Instructions for using the app
[Watch Video] (https://youtu.be/MCVHk-hX-1w)
