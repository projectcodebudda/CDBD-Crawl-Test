package com.crawl.cdbd_crawl.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
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

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/examplePg")
    public String example1() {
        return "store1";
    }

    @PostMapping("/url")
    public ResponseEntity<JSONObject> crawlUrl(@RequestBody Map<String, String> request) throws JSONException {
        String url = request.get("url");
        String keyword = request.get("word");
        String identifier = request.get("tpword");
        String element = request.getOrDefault("element", ""); // element가 없을 경우 빈 문자열

        String log = runCrawler("src/main/resources/file/app.exe", url, keyword, identifier, element);

        JSONObject response = new JSONObject();
        try {
            // JSON 파싱 라이브러리를 이용해 JSON 문자열을 Map으로 변환
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(log);

            // JSON 결과를 Map으로 반환
            response.put("result", jsonNode);
        } catch (Exception e) {
            response.put("error", "Error parsing JSON: " + e.getMessage());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(response);
    }

    private String runCrawler(String... command) {
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
            int exitCode = process.waitFor();

            // 프로세스 종료 코드에 따른 반환값 설정
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "크롤링 중 오류 발생";
            }
        } catch (Exception e) {
            return "크롤링 중 예외 발생: " + e.getMessage();
        }
    }


    @PostMapping("/public-data")

    public ResponseEntity<Map<String, String>> crawlPublicData(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String apiKey = request.get("apiKey");

        String log = runCrawler("src/main/resources/public_data_crawler.exe", url, apiKey);

        Map<String, String> response = new HashMap<>();
        response.put("log", log);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                .body(response);
    }
}

