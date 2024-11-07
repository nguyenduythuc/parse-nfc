package vn.lottefinance.parsenfc.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import vn.lottefinance.parsenfc.util.NFCParser;
import vn.lottefinance.parsenfc.service.NFCService;
import vn.lottefinance.parsenfc.service.dto.NFCParserRequestDto;
import vn.lottefinance.parsenfc.service.dto.NFCParserResponseDto;

@Service
@Slf4j
public class NFCServiceImpl implements NFCService {
    private final ObjectMapper mapper;

    public NFCServiceImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public NFCParserResponseDto parseNFC(NFCParserRequestDto nfcParserRequestDto) throws JsonProcessingException {
        log.info("Entering Parsing NFC.... {}", mapper.writeValueAsString(nfcParserRequestDto));
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("dg1", nfcParserRequestDto.getDg1());
        jsonRequest.put("dg2", nfcParserRequestDto.getDg2());
        jsonRequest.put("dg13", nfcParserRequestDto.getDg13());

        log.info("User request: {}", jsonRequest);
        NFCParserResponseDto parsed = NFCParser.parsePassportData(jsonRequest);
        return parsed;
    }
}
