package com.example.application.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Random;

@Service
public class StockDataService {

    public Flux<BigDecimal> getStockPrice() {
        Random random = new Random();
        return Flux
                .<BigDecimal>generate(
                        sink -> {
                            sink.next(BigDecimal.valueOf(random.nextInt(10000), 2));
                        }
                )
                .delayElements(Duration.ofMillis(500))
                .take(50);
    }

    public String longRunningTask() {
        int LONG_RUNNING_TASK_DURATION = 6000;

        try {
            Thread.sleep(LONG_RUNNING_TASK_DURATION);
        } catch (InterruptedException e) {
            return "Error  - " + e.getMessage();
        }

        return String.format("ready in %d seconds", (LONG_RUNNING_TASK_DURATION / 1000));
    }
}
