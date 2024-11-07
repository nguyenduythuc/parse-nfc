package vn.lottefinance.parsenfc.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NFCParserResponseDto {
    private String fullname;
    private String documentNumber;
    private String idNumber;
    private String oldIdNumber;
    private String dob;
    private String gender;
    private String nationality;
    private String ethnicity;
    private String religion;
    private String origin;
    private String address;
    private String identifyingCharacteristics;
    private String doi;
    private String dueDate;
    private String fatherName;
    private String motherName;
    private String spouseName;
    private String passportNumber;
    private String passportImage;
}