package com.example.mysearch.service;
import com.example.mysearch.repository.SerieRepository;
import org.springframework.stereotype.Service;
@Service
public class SerieService {
    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }
}
