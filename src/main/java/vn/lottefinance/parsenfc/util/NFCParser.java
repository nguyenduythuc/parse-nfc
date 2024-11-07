package vn.lottefinance.parsenfc.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.lottefinance.parsenfc.service.dto.NFCParserResponseDto;

public class NFCParser {
    private static final Logger logger = LoggerFactory.getLogger(NFCParser.class);

    private NFCParser() {
        throw new IllegalStateException("Utility class");
    }

    // Main parsing method
    public static NFCParserResponseDto parsePassportData(JSONObject passportData) {
        try {
            logger.info("Starting passport data parsing");
            NFCParserResponseDto result = new NFCParserResponseDto();

            // Process DG1 - return MRZ data
            try {
                byte[] dg1Bytes = Base64.getDecoder().decode(passportData.getString("dg1"));
                String dg1Decoded = new String(dg1Bytes, StandardCharsets.US_ASCII);
                if (dg1Decoded.length() >= 5) {
                    result.setDocumentNumber(dg1Decoded.substring(5));
                }
            } catch (Exception e) {
                logger.warn("Error processing DG1: {}", e.getMessage());
            }

            // Process DG2 - return image data as base64
            try {
                String dg2Decoded = parseDG2(passportData.getString("dg2"));
                result.setPassportImage("data:image/jpeg;base64," + dg2Decoded);
            } catch (Exception e) {
                logger.warn("Error processing DG2: {}", e.getMessage());
            }

            // Process DG13 - return rest information of ID Card
            try {
                byte[] dg13Bytes = Base64.getDecoder().decode(passportData.getString("dg13"));
                String dg13Decoded = new String(dg13Bytes, StandardCharsets.UTF_8);
                Map<String, String> dg13Fields = parseDG13Fields(dg13Decoded);
                mapFieldsToPassportData(result, dg13Fields);
            } catch (Exception e) {
                logger.error("Error processing DG13: {}", e.getMessage());
            }

            return result;

        } catch (Exception e) {
            logger.error("Error parsing passport data: {}", e.getMessage());
            throw new RuntimeException("Error parsing passport data: " + e.getMessage(), e);
        }
    }

    // DG2 parsing method
    private static String parseDG2(String dg2Data) {
        try {
            byte[] binaryData = Base64.getDecoder().decode(dg2Data);

            // Find JPEG start marker (FF D8 FF)
            int jpegStart = -1;
            for (int i = 0; i < binaryData.length - 2; i++) {
                if ((binaryData[i] & 0xFF) == 0xFF &&
                        (binaryData[i + 1] & 0xFF) == 0xD8 &&
                        (binaryData[i + 2] & 0xFF) == 0xFF) {
                    jpegStart = i;
                    break;
                }
            }

            if (jpegStart == -1) {
                throw new IllegalArgumentException("JPEG data not found in DG2");
            }

            // Find JPEG end marker (FF D9)
            int jpegEnd = -1;
            for (int i = jpegStart; i < binaryData.length - 1; i++) {
                if ((binaryData[i] & 0xFF) == 0xFF &&
                        (binaryData[i + 1] & 0xFF) == 0xD9) {
                    jpegEnd = i + 1;
                    break;
                }
            }

            if (jpegEnd == -1) {
                throw new IllegalArgumentException("JPEG end marker not found");
            }

            // Extract JPEG data
            byte[] jpegData = new byte[jpegEnd - jpegStart + 1];
            System.arraycopy(binaryData, jpegStart, jpegData, 0, jpegData.length);

            return Base64.getEncoder().encodeToString(jpegData);
        } catch (Exception e) {
            logger.error("Error parsing DG2 data: {}", e.getMessage());
            throw new RuntimeException("Error parsing DG2 data", e);
        }
    }

    // DG13 parsing method
    private static Map<String, String> parseDG13Fields(String dg13Decoded) {
        Map<String, String> dg13Fields = new HashMap<>();
        if (dg13Decoded == null || dg13Decoded.isEmpty()) {
            return dg13Fields;
        }

        // logger.info("DG13 raw data: {}", dg13Decoded);
        String currentField = "";
        StringBuilder currentValue = new StringBuilder();
        int index = 0;

        for (int i = 0; i < dg13Decoded.length(); i++) {
            char c = dg13Decoded.charAt(i);
            int charCode = c;

            if (charCode == 2) {
                if (!currentField.isEmpty()) {
                    dg13Fields.put(index + "", currentValue.toString().trim());
                    index += 1;
                }

                currentField = String.valueOf(dg13Decoded.charAt(i + 2));
                currentValue = new StringBuilder();
                i += 3;
            } else if ((charCode >= 32 && charCode <= 126) || charCode > 127) {
                currentValue.append(c);
            }
        }

        // Add the last field-value pair
        if (!currentField.isEmpty() && currentValue.length() > 0) {
            dg13Fields.put(index + "", currentValue.toString().trim());
        }

        return dg13Fields;
    }

    private static void mapFieldsToPassportData(NFCParserResponseDto result, Map<String, String> fields) {

        // Handle Citizen Identity Card
        String idNumber = cleanField(getFieldValue(fields, "1"), false);
        result.setIdNumber(idNumber);

        // Handle Full name
        String fullname = cleanField(getFieldValue(fields, "2"));
        result.setFullname(fullname);

        // Handle Date of Birth
        String dob = cleanField(getFieldValue(fields, "3"), false);
        result.setDob(dob.substring(0, 10));

        // Handle Gender
        String gender = cleanField(getFieldValue(fields, "4"));
        result.setGender(gender);

        // Handle Nationality
        String nationality = cleanField(getFieldValue(fields, "5"));
        result.setNationality(nationality);

        // Handle Ethnicity
        String ethnicity = cleanField(getFieldValue(fields, "6"));
        result.setEthnicity(ethnicity);

        // Handle Religion
        String religion = cleanField(getFieldValue(fields, "7"));
        result.setReligion(religion);

        // Handle Origin
        String origin = cleanField(getFieldValue(fields, "8"));
        result.setOrigin(origin);

        // Handle Address
        String address = cleanField(getFieldValue(fields, "9"));
        result.setAddress(address);

        // Handle Characteristics
        String characteristics = cleanField(getFieldValue(fields, "10"));
        result.setIdentifyingCharacteristics(characteristics);

        // Handle Date of Expiry
        String doi = cleanField(getFieldValue(fields, "11"), false);
        result.setDoi(doi.substring(0, 10));

        // Handle Due Date
        String dueDate = cleanField(getFieldValue(fields, "12"), false);
        result.setDueDate(dueDate.substring(0, 10));

        // Handle parent names
        String parentNames = getFieldValue(fields, "13");
        if (!parentNames.isEmpty()) {
            String[] names = parentNames.split("0");
            result.setFatherName(cleanField(names[0]));
            result.setMotherName(names.length > 1 ? cleanField(names[1]) : "");
        }

        // Handle Spouse
        String spouse = cleanField(getFieldValue(fields, "14"));
        result.setOldIdNumber(spouse);

        // Handle Old ID Number
        String oldIDNumber = cleanField(getFieldValue(fields, "15"), false);
        result.setOldIdNumber(oldIDNumber);

        // Handle Passport Number
        String passportNumber = getFieldValue(fields, "16");
        result.setPassportNumber(passportNumber);
    }

    private static String cleanField(String value) {
        return cleanField(value, true);
    }

    private static String cleanField(String value, boolean removeTrailingNumbers) {
        if (value == null) {
            return "";
        }
        String result = value
                .replaceAll("0+$", "") // Remove trailing zeros
                .replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "") // Remove leading and trailing special characters
                .replaceAll("(?<=\\p{L})\\d+|\\d+(?=\\p{L})", "") // Remove numbers attached to letters
                .replaceAll("(?<=\\p{L})[^\\p{L}\\s]+(?=\\p{L})", "") // Remove special characters within words
                .replaceAll("\\s+", " ") // Normalize spaces
                .trim();

        if (removeTrailingNumbers) {
            result = result.replaceAll("\\d+$", ""); // Remove trailing numbers
        }

        return result;
    }

    private static String getFieldValue(Map<String, String> fields, String key) {
        return fields.getOrDefault(key, "");
    }
}