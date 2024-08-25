package com.crawl.cdbd_crawl.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/crawl")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/examplePg")
    public String example1() {
        return "store1";
    }

    @PostMapping("/url")
    public ResponseEntity<Void> crawlUrl(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            String keyword = request.get("word");
            String identifier = request.get("tpword");
            String element = request.getOrDefault("element", ""); // element가 없을 경우 빈 문자열

            runCrawler("src/main/resources/file/app.exe", url, keyword, identifier, element);
            return ResponseEntity.ok().build(); // 성공적인 응답 반환
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).build(); // 오류 발생 시 500 상태 코드 반환
        }
    }

    private void runCrawler(String... command) {
//    private String runCrawler(String... command) {
        try {
            // ProcessBuilder를 사용하여 명령어 실행
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 표준 출력 스트림을 UTF-8로 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            if (process.waitFor() != 0)
                logger.error("크롤링 중 오류 발생");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    @PostMapping("/public-data")

    public void crawlPublicData(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String apiKey = request.get("apiKey");

        runCrawler("src/main/resources/public_data_crawler.exe", url, apiKey);

    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody String json) {
        System.out.println(json);
    }
}

