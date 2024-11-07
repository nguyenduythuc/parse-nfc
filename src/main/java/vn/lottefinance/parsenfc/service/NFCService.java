package vn.lottefinance.parsenfc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.lottefinance.parsenfc.service.dto.NFCParserRequestDto;
import vn.lottefinance.parsenfc.service.dto.NFCParserResponseDto;

public interface NFCService {
    NFCParserResponseDto parseNFC(NFCParserRequestDto nfcParserRequestDto) throws JsonProcessingException;
}
