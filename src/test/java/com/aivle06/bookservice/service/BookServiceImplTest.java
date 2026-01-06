package com.aivle06.bookservice.service;

import com.aivle06.bookservice.domain.Book;
import com.aivle06.bookservice.dto.BookDetailResponseDTO;
import com.aivle06.bookservice.dto.BookListResponseDTO;
import com.aivle06.bookservice.dto.BookRequestDTO;
import com.aivle06.bookservice.exception.ResourceNotFoundException;
import com.aivle06.bookservice.mapper.BookMapper;
import com.aivle06.bookservice.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_mapsRequestAndSavesEntity() {
        BookRequestDTO.Create request = BookRequestDTO.Create.builder()
                .title("Test Title")
                .content("Content")
                .author("Author")
                .build();

        Book mappedBook = new Book();
        Book savedBook = new Book();
        savedBook.setId(1L);

        when(bookMapper.toEntity(request)).thenReturn(mappedBook);
        when(bookRepository.save(mappedBook)).thenReturn(savedBook);

        Book result = bookService.createBook(request);

        assertSame(savedBook, result);
        verify(bookMapper).toEntity(request);
        verify(bookRepository).save(mappedBook);
    }

    @Test
    void getBookById_returnsBookWhenPresent() {
        Book book = new Book();
        book.setId(5L);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(5L);

        assertSame(book, result);
        verify(bookRepository).findById(5L);
    }

    @Test
    void getBookById_throwsWhenBookMissing() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(99L));
        verify(bookRepository).findById(99L);
    }

    @Test
    void getBookListResponseDTOById_convertsEntityToListResponse() {
        Book book = new Book();
        BookListResponseDTO dto = new BookListResponseDTO();

        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));
        when(bookMapper.toListResponseDTO(book)).thenReturn(dto);

        BookListResponseDTO result = bookService.getBookListResponseDTOById(7L);

        assertSame(dto, result);
        verify(bookRepository).findById(7L);
        verify(bookMapper).toListResponseDTO(book);
    }

    @Test
    void getAllBookListResponseWithPaging_returnsMappedPage() {
        Book firstBook = new Book();
        firstBook.setId(1L);
        Book secondBook = new Book();
        secondBook.setId(2L);

        BookListResponseDTO firstDto = new BookListResponseDTO();
        BookListResponseDTO secondDto = new BookListResponseDTO();

        Page<Book> bookPage = new PageImpl<>(List.of(firstBook, secondBook), PageRequest.of(1, 5), 10);

        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);
        when(bookMapper.toListResponseDTO(firstBook)).thenReturn(firstDto);
        when(bookMapper.toListResponseDTO(secondBook)).thenReturn(secondDto);

        Page<BookListResponseDTO> result = bookService.getAllBookListResponseWithPaging(1, 5);

        ArgumentCaptor<PageRequest> pageableCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(bookRepository).findAll(pageableCaptor.capture());
        assertEquals(PageRequest.of(1, 5), pageableCaptor.getValue());

        assertEquals(List.of(firstDto, secondDto), result.getContent());
    }

    @Test
    void getBookDetailResponseDTOById_convertsEntityToDetailResponse() {
        Book book = new Book();
        BookDetailResponseDTO detailDto = new BookDetailResponseDTO();

        when(bookRepository.findById(3L)).thenReturn(Optional.of(book));
        when(bookMapper.toDetailResponseDTO(book)).thenReturn(detailDto);

        BookDetailResponseDTO result = bookService.getBookDetailResponseDTOById(3L);

        assertSame(detailDto, result);
        verify(bookRepository).findById(3L);
        verify(bookMapper).toDetailResponseDTO(book);
    }

    @Test
    void updateBook_updatesAndSaves() {
        Book existing = new Book();
        existing.setId(10L);
        BookRequestDTO.Update updateDto = BookRequestDTO.Update.builder()
                .title("Updated")
                .content("New content")
                .author("Author")
                .image_url("img")
                .build();

        Book saved = new Book();
        saved.setId(10L);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(saved);

        Book result = bookService.updateBook(10L, updateDto);

        assertSame(saved, result);
        verify(bookMapper).updateFromDTO(updateDto, existing);
        verify(bookRepository).save(existing);
    }

    @Test
    void deleteBook_removesEntity() {
        Book existing = new Book();
        existing.setId(12L);
        when(bookRepository.findById(12L)).thenReturn(Optional.of(existing));

        bookService.deleteBook(12L);

        verify(bookRepository).findById(12L);
        verify(bookRepository).delete(existing);
    }
}
