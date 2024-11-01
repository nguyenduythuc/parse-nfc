package com.example.test.demo;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NFCController {
    private static final Logger logger = LoggerFactory.getLogger(NFCController.class);

    @PostMapping("/nfc-parser")
    public ResponseEntity<NFCParserResponse> nfcParser(@RequestBody NFCParserRequest nfcParserRequest) {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("dg1", nfcParserRequest.getDg1());
        jsonRequest.put("dg13", nfcParserRequest.getDg13());

        logger.info("User request: {}", jsonRequest);
        NFCParserResponse parsed = NFCParser.parsePassportData(jsonRequest);

        return ResponseEntity.ok(parsed);
    }
}