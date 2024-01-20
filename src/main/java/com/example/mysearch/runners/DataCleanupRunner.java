package com.example.mysearch.runners;

import com.example.mysearch.service.HistoryService;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Order(1)
@Component
public class DataCleanupRunner implements CommandLineRunner {

    private final UserService userService;
    private final HistoryService historyService;
    private final SerieService serieService;

    public DataCleanupRunner(UserService userService, HistoryService historyService, SerieService serieService) {
        this.userService = userService;
        this.historyService = historyService;
        this.serieService = serieService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        userService.getAllUsers().forEach(user -> userService
                .deleteUser(user.getId()));
        historyService.getAllHistoryRecords().forEach(history -> historyService.deleteHistoryRecord(history.getId()));
        serieService.resetSeriesStats();
         */
    }
}