package com.example.mysearch;

import com.example.mysearch.model.Serie;
import com.example.mysearch.repository.SerieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MySearchApplicationTests {

    @Autowired // injecte le bean dans le test
    private SerieRepository serieRepository;

    @Test
    public void shouldFetchData() {
        String expectedTitle = "breakingbad";


        List<Serie> series = serieRepository.findByTitre(expectedTitle);


        assertThat(series).isNotEmpty();
        assertThat(series.get(0).getTitre()).isEqualTo(expectedTitle);
    }
}
