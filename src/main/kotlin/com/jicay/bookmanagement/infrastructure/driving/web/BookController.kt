package com.jicay.bookmanagement.infrastructure.driving.web

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.domain.usecase.ErrorResponse
import com.jicay.bookmanagement.infrastructure.driving.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.toDomain())
    }

    @CrossOrigin
    @GetMapping("/{title}/reserve")
    fun reserveBook(@PathVariable title: String): ResponseEntity<ErrorResponse?> {
        try {
            bookUseCase.reserveBookByTitle(title)
            return ResponseEntity.ok(null)
        } catch (e : IllegalArgumentException) {
            if (e.message == "Book not found") {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(
                    status  = HttpStatus.NOT_FOUND.value(),
                    error   = "Not Found",
                    message = e.message!!
                ))
            }
            if (e.message == "Book already reserved") {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(
                    status  = HttpStatus.CONFLICT.value(),
                    error   = "Conflict",
                    message = e.message!!
                ))
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(
                status  = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error   = "Internal Server Error",
                message = "Internal Server Error"
            ))
        }
    }
}