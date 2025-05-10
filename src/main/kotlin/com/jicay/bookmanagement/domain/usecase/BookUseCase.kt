package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBookByTitle(title: String) {
        val book: Book? = bookPort.getBookByTitle(title)

        if (book == null) {
            throw IllegalArgumentException("Book not found")
        }

        if (book.reserved) {
            throw IllegalArgumentException("Book already reserved")
        }

        bookPort.reserveBookByTitle(title)
    }
}