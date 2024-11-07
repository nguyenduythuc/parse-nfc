package vn.lottefinance.parsenfc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lottefinance.parsenfc.service.NFCService;
import vn.lottefinance.parsenfc.util.NFCParser;
import vn.lottefinance.parsenfc.service.dto.NFCParserRequestDto;
import vn.lottefinance.parsenfc.service.dto.NFCParserResponseDto;

@RestController
@RequestMapping("/api")
@Slf4j
public class NFCController {
    private final NFCService nfcService;

    public NFCController(NFCService nfcService) {
        this.nfcService = nfcService;
    }
    @PostMapping("/nfc-parser")
    public ResponseEntity<NFCParserResponseDto> nfcParser(@RequestBody NFCParserRequestDto nfcParserRequestDto) throws JsonProcessingException {
        return ResponseEntity.ok(this.nfcService.parseNFC(nfcParserRequestDto));
    }
}
