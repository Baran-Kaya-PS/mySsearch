package com.example.mysearch;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.ArrayList;
import java.util.List;
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
        history.setDate(new ArrayList<>());
        history.getRecherche().add("search1");
        history.getResultats().add("result1");
        history.setSerieId("serie123");

        // Save the history to the database
        History savedHistory = historyRepository.save(history);

        // Verify that the history is saved successfully
        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getUtilisateurId()).isEqualTo("user123");
        assertThat(savedHistory.getRecherche()).contains("search1");
        assertThat(savedHistory.getResultats()).contains("result1");
        assertThat(savedHistory.getSerieId()).isEqualTo("serie123");
    }
}
