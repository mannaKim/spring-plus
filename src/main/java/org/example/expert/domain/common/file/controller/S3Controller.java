package org.example.expert.domain.common.file.controller;

import org.example.expert.domain.common.file.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/s3/presigned-url")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/upload")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestParam String fileName) {
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", s3Service.generatePresignedUrl(fileName));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<Map<String, String>> getPresignedDownloadUrl(@RequestParam String fileName) {
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", s3Service.generatePresignedDownloadUrl(fileName));
        return ResponseEntity.ok(response);
    }
}
