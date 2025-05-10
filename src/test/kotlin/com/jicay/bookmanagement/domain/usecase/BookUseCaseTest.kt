package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo", false),
            Book("Hamlet", "William Shakespeare", false),
            Book("Le Petit Prince", "Antoine de Saint-Exupéry", true)
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainAll(
            Book("Les Misérables", "Victor Hugo", false),
            Book("Hamlet", "William Shakespeare", false),
            Book("Le Petit Prince", "Antoine de Saint-Exupéry", true)
        )
    }

    test("add book should create a book") {
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo", false)

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("reserve book by title should reserve the book") {
        val title = "Les Misérables"
        val book = Book(title, "Victor Hugo", false)

        every { bookPort.getBookByTitle(title) } returns book
        justRun { bookPort.reserveBookByTitle(title) }

        bookUseCase.reserveBookByTitle(title)

        verify(exactly = 1) { bookPort.reserveBookByTitle(title) }
    }

    test("reserve book by title should throw exception if book not found") {
        val title = "Les autres"

        every { bookPort.getBookByTitle(title) } returns null

        val exception = shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBookByTitle(title)
        }

        exception.message shouldBe "Book not found"
    }

    test("reserve book by title should throw exception if book already reserved") {
        val title = "le petit prince"
        val book = Book(title, "Antoine de Saint-Exupéry", true)

        every { bookPort.getBookByTitle(title) } returns book

        val exception = shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBookByTitle(title)
        }
        exception.message shouldBe "Book already reserved"
    }
})