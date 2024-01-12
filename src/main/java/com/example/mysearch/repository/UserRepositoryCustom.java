package com.example.mysearch.repository;

public interface UserRepositoryCustom {
    void addSearchToHistory(String userId, String searchTerm);
}