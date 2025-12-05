package com.aivle06.bookservice.mapper;

import com.aivle06.bookservice.domain.Book;
import com.aivle06.bookservice.dto.BookDetailResponseDTO;
import com.aivle06.bookservice.dto.BookListResponseDTO;
import com.aivle06.bookservice.dto.BookRequestDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequestDTO.Create dto);

    BookListResponseDTO toListResponseDTO(Book book);

    BookDetailResponseDTO toDetailResponseDTO(Book book);


    // 업데이트 용
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(BookRequestDTO.Update dto, @MappingTarget Book book);
}
