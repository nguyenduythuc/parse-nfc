package vn.lottefinance.parsenfc.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NFCParserRequestDto {
    private String com;
    private String dg1;
    private String dg2;
    private String dg13;
    private String dg14;
    private String dg15;
}