## Library Management System

Library Management System Project for INT 2204 7 N1. 
Contributor include: Tran Thanh Dat, Le Minh Dat, Hoang Ngoc Nhi and Nguyen Tue Minh
# LMS - Library Management System

A robust Library Management System (LMS) developed in Java with JavaFX and SQLite, aimed at managing books, authors, users, and book borrowing processes through an intuitive graphical interface.

## Features

![Dashboard](https://drive.google.com/uc?export=view&id=1HxvqRwREs-BwgWAuSmX72Cx9FySriP9j)

**1. Book Management**

![Document List](https://drive.google.com/uc?export=view&id=1o55ldHAVOgeipG23r0NEccdWVnwlQqpz)

![Document Info](https://drive.google.com/uc?export=view&id=1elYSNrfcPBq4so9n4CNpxfwrUkQU7ZXC)

- Add New Books: Allows librarians to add books to the database with details such as:
  - Title.
  - ISBN-13 or ISBN-10.
  - Publisher.
  - Published date.
- Update Book Information: Modify the details of existing books.
- Delete Books: Remove books that are no longer needed.
- Search for Books: Find books by title, ISBN, or publisher.

**Supporting Technology:**
- DatabaseConnection.java: Manages the SQLite database to store book information (tables Documents and Authors)​.

**2. User Management**

![User List](https://drive.google.com/uc?export=view&id=1ywG0BWhBDDLOlkPjJJoF87kOg7WWeU7X)

- Register New Users: Add library users with details like name, email, and user ID.
- Monitor User Activity: 
  - View borrowing and returning history.
  - Manage user account information.
- User Roles: Distinguishes between librarians and readers, enforcing different permissions.

**Supporting Technology:**
- UserDAO and TransactionDAO: Manages user data and borrowing transactions​.

**3. Borrowing and Returning Transactions**

![Add transaction](https://drive.google.com/uc?export=view&id=1w3GvMISbpI_x5onITkvHw3mcTIv39BQS)


- Borrow Books: Record the details of borrowed books, including user ID and borrowing time.
- Return Books: Update the book's status when returned.

![Transaction List](https://drive.google.com/uc?export=view&id=1vrVkpzKkhEMoKhjOrgu2XkUsy0b90r96)


- Transaction History: Maintains a complete log of borrowing and returning activities for audit and reference.

**Supporting Technology:**
- Transaction data is stored in a relational structure linking Documents and Users​.

**4.Smart Book Recommendations**
- Personalized Book Suggestions: Based on users' borrowing history and the behavior of other users, the system calculates similarity scores to suggest the most relevant books.
- Algorithm:
- Cosine Similarity: Applied to compare borrowing patterns between users and select books not yet borrowed by the current user but popular among similar users​.
**Supporting Technology:** 
- RecommendationService.java: Handles recommendation logic, integrating data from transaction records and book information​.

**5. External API Integration**
- Google Chat API: 
  - Connects to Google Chat to provide instant responses to user queries.
  - Interacts with the API via HTTP POST requests, using JSON payloads.
  - Processes API responses to deliver appropriate answers.
**Supporting Technology:**
  - GoogleChatAPI.java: Integrates with Google's REST API for chatbot functionality in the library system​.

**6. User Interface and Language Configuration**

![Setting1](https://drive.google.com/uc?export=view&id=1VXPPPlU8C3LHMB50rUALg9PF1g0bGqjE)

![Setting2](https://drive.google.com/uc?export=view&id=1WJYEyd6iO-Hld8cqAtTl_sJ29vB7HJ6x)


- Theme Switching: Users can switch between available themes (e.g., dark mode, light mode).
- Language Switching: Supports multilingual settings, allowing users to change between supported languages (e.g., English, Vietnamese).
- Save Configuration: Settings are saved and automatically applied on the next use.
**Supporting Technology:**
- SettingController.java: Handles events for changing themes and languages via tools like ThemeNavigator and Localization​.

**7. Auto-Completion for Search Fields**

![Document List 2](https://drive.google.com/uc?export=view&id=1Kb2Ifl5UtBnR34W_xF7_E3eeN3QKtqWO)

- Enhanced User Experience:
  - Provides auto-suggestions in real-time as users type in the search bar.
  - Filters suggestions dynamically based on the input text.
- Highlighted Matches: Highlights the part of the suggestion that matches the input for better clarity.

**Supporting Technology:**
- AutoCompletionTextField.java: Implements dynamic auto-completion for text fields, utilizing JavaFX's TextField and ContextMenu for suggestions​.

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

## Detailed Project Structure

![Structure diagram](https://drive.google.com/uc?export=view&id=17_qBSDHPAEWxewdNjUwfWY8haKKTgwtd)


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
