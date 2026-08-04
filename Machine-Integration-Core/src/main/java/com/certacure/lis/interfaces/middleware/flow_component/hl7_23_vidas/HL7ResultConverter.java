package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HL7ResultConverter {
    
    private static final DateTimeFormatter HL7_DATETIME_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter HL7_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * Converts a custom pipe-delimited lab result string to HL7 ORU^R01 message
     * 
     * @param input The pipe-delimited result string
     * @return HL7 formatted message
     */
    public static String convertToHL7(String input) {
        // Parse the input string
        Map<String, String> data = parseInput(input);
        
        // Build HL7 message
        StringBuilder hl7 = new StringBuilder();
        
        // MSH - Message Header
        hl7.append(buildMSH(data));
        
        // PID - Patient Identification
        hl7.append(buildPID(data));
        
        // OBR - Observation Request
        hl7.append(buildOBR(data));
        
        // OBX - Observation Result
        hl7.append(buildOBX(data));
        
        return hl7.toString();
    }
    
    /**
     * Parses the pipe-delimited input into a map
     */
    private static Map<String, String> parseInput(String input) {
        Map<String, String> data = new HashMap<>();
        String[] parts = input.split("\\|");
        
        for (String part : parts) {
            if (part.length() >= 2) {
                String key = part.substring(0, 2);
                String value = part.substring(2).replaceAll("[\\r\\n]+", "").trim();
                data.put(key, value);
            }
        }
        
        return data;
    }
    
    /**
     * Builds MSH segment (Message Header)
     */
    private static String buildMSH(Map<String, String> data) {
        String timestamp = LocalDateTime.now().format(HL7_DATETIME_FORMAT);
        String msgControlId = generateMessageControlId();
        
        return String.format("MSH|^~\\&|%s|%s|LAB|LAB|%s||ORU^R01|%s|P|2.5\r",
            data.getOrDefault("id", "BMXHOST0"),
            data.getOrDefault("nc", "calinc"),
            timestamp,
            msgControlId);
    }
    
    /**
     * Builds PID segment (Patient Identification)
     */
    private static String buildPID(Map<String, String> data) {
        String patientId = data.getOrDefault("pi", "");
        String patientName = formatPatientName(data.getOrDefault("pn", ""));
        String dob = formatDate(data.getOrDefault("pb", ""));
        String sex = data.getOrDefault("ps", "");
        
        return String.format("PID|1||%s||%s||%s|%s\r",
            patientId,
            patientName,
            dob,
            sex.isEmpty() ? "U" : sex);
    }
    
    /**
     * Builds OBR segment (Observation Request)
     */
    private static String buildOBR(Map<String, String> data) {
        String orderId = data.getOrDefault("ci", "");
        String testCode = data.getOrDefault("rt", "");
        String testName = data.getOrDefault("rn", "");
        String observationDateTime = formatDateTime(
            data.getOrDefault("td", ""),
            data.getOrDefault("tt", "")
        );
        
        return String.format("OBR|1|%s||%s^%s|||%s\r",
            orderId,
            testCode,
            testName,
            observationDateTime);
    }
    
    /**
     * Builds OBX segment (Observation Result)
     */
    private static String buildOBX(Map<String, String> data) {
        String testCode = data.getOrDefault("rt", "");
        String testName = data.getOrDefault("rn", "");
        String result = extractNumericValue(data.getOrDefault("qn", ""));
        String units = data.getOrDefault("y3", "");
        String status = data.getOrDefault("qd", "F"); // F = Final
        String observationDateTime = formatDateTime(
            data.getOrDefault("td", ""),
            data.getOrDefault("tt", "")
        );
        
        return String.format("OBX|1|NM|%s^%s||%s|%s|||%s|||%s\r",
            testCode,
            testName,
            result,
            units,
            status,
            observationDateTime);
    }
    
    /**
     * Formats patient name from "dupond" to "dupond^"
     */
    private static String formatPatientName(String name) {
        return name.isEmpty() ? "" : name + "^";
    }
    
    /**
     * Formats date from "1975/10/10" to "19751010"
     */
    private static String formatDate(String date) {
        if (date.isEmpty()) return "";
        return date.replace("/", "");
    }
    
    /**
     * Formats date and time into HL7 datetime format
     */
    private static String formatDateTime(String date, String time) {
        String formattedDate = formatDate(date);
        String formattedTime = time.replace(":", "");
        return formattedDate + formattedTime + "00";
    }
    
    /**
     * Extracts numeric value from "0.80 uUI/ml"
     */
    private static String extractNumericValue(String value) {
        if (value.isEmpty()) return "";
        // Remove any newlines, carriage returns, and extra whitespace
        value = value.replaceAll("[\\r\\n]+", " ").trim();
        return value.split("\\s+")[0];
    }
    
    /**
     * Generates a unique message control ID
     */
    private static String generateMessageControlId() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        String input = "mtrsl|pi200609|pndupond|pb1975/10/10|ps-|so|si|ci11111111111122222|" +
                      "rtTSH|rnTSH|tt14:53|td2013/10/23|ql|qn0.80 uUI/ml|y3uUI/ml|qd1|" +
                      "nccalinc|idBMXHOST0|sn|m4db2bmx|";
        
        String hl7Message = convertToHL7(input);
        System.out.println("HL7 ORU^R01 Message:");
        System.out.println(hl7Message);
    }
}