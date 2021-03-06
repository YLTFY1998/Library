package com.yltfy.repository;

import com.yltfy.entity.Book;

import java.util.List;

public interface BookRepository {
    public List<Book> findAll(int page, int limit);
    public int getPages();
}
