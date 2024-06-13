package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class InfoController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/port")
    public ResponseEntity<String> getPort() {
        return ResponseEntity.ok(port);
    }

    @GetMapping("/calculator")
    public ResponseEntity<Integer> calculator() {
        int limit = 1_000_000;
        int sum = Stream.iterate(1, a -> a < limit, a -> a + 1)
                .reduce(0, Integer::sum);
        return ResponseEntity.ok(sum);
    }
}