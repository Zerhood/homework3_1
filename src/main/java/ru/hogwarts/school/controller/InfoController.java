package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;
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
        int sum = IntStream.range(1, 1_000_000).parallel().sum();
        return ResponseEntity.ok(sum);
    }
}