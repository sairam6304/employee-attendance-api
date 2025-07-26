package com.example.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredController {

    @GetMapping("/secure-data")
    public ResponseEntity<String> securedEndpoint() {
        return ResponseEntity.ok("This is a secured endpoint");
    }
}

@RestController
class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
