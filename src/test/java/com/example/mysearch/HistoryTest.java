package com.example.mysearch;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class HistoryTest {

    @Autowired
    private HistoryRepository historyRepository;

    @Test
    public void testAddHistoryToDatabase() {
        // Create a new history object
        History history = new History();
        history.setUtilisateurId("user123");

        Map<String, Integer> searchCount = new HashMap<>();
        searchCount.put("search1", 1);
        history.setSearchCount(searchCount);

        Map<String, Integer> clickCount = new HashMap<>();
        clickCount.put("serie123", 1);
        history.setClickCount(clickCount);

        // Save the history to the database
        History savedHistory = historyRepository.save(history);

        // Verify that the history is saved successfully
        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getUtilisateurId()).isEqualTo("user123");
        assertThat(savedHistory.getSearchCount()).containsEntry("search1", 1);
        assertThat(savedHistory.getClickCount()).containsEntry("serie123", 1);
    }
}