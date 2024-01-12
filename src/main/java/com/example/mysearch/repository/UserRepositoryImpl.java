package com.example.mysearch.repository;

import com.example.mysearch.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void addSearchToHistory(String userId, String searchTerm) {
        mongoTemplate.updateFirst(
                query(where("id").is(userId)),
                new Update().push("historiqueRecherches", searchTerm),
                User.class
        );
    }
}

