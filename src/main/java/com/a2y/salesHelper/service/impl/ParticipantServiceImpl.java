package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.ParticipantEntity;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.pojo.Participant;
import com.a2y.salesHelper.service.interfaces.ParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final CompaniesRepository companiesRepository;

    // Fixed header mappings for Participant fields - customize these based on your Excel structure
    Map<String, Integer> headerMappings = new HashMap<>();
    private static final String[] EXPECTED_HEADERS_PARTICIPANTS = {
            "name", "designation", "organization", "email", "mobile", "attended", "assigned/unassigned" , "Event Name" , "Date" ,"Meeting Done"
    };

    public ParticipantServiceImpl(ParticipantRepository participantRepository, CompaniesRepository companiesRepository) {
        this.participantRepository = participantRepository;

        this.companiesRepository = companiesRepository;
    }

    @Override
    public Integer parseExcelFile(MultipartFile file) throws IOException {
        List<ParticipantEntity> participants = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(fileName, inputStream);

            // Process all sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                parseHeaders(sheet,headerMappings);
                log.info("Headers Parsed for sheet '{}': {}", sheetName, headerMappings);
                // Skip header row and process data rows
                for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null || isRowEmpty(row)) continue;
                    try {
                        ParticipantEntity participant = parseRowToParticipant(row, sheetName, fileName);
                        if (participant != null && isValidParticipant(participant)) {
                            participants.add(participant);
                            log.info("Parsed participant: {}", participant);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing row " + rowIndex + " in sheet " + sheetName +
                                " of file " + fileName + ": " + e.getMessage());
                    }
                }
            }
            workbook.close();
        }

        // Save all participants to database
        if (!participants.isEmpty()) {
            participants.removeIf(participant -> participantRepository.existsByNameAndDesignationAndOrganization(participant.getName(), participant.getDesignation(),participant.getOrganization()));
            participantRepository.saveAll(participants);
        }
        return participants.size();
    }

    @Override
    public List<Participant> getAllParticipant() {
        List<ParticipantEntity> participants =  participantRepository.getAll();
        List<Participant> response = new ArrayList<>();

        Set<String> existingAccounts = new HashSet<>(companiesRepository.findAllAccounts());

        for(ParticipantEntity participant : participants){
            response.add(Participant.builder()
                    .id(participant.getId())
                    .name(participant.getName())
                    .email(participant.getEmail())
                    .mobile(participant.getMobile())
                    .designation(participant.getDesignation())
                    .organization(participant.getOrganization())
                    .assignedUnassigned(participant.getAssignedUnassigned())
                    .attended(participant.getAttended())
                    .eventName(participant.getEventName())
                    .eventDate(participant.getEventDate())
                    .meetingDone(participant.getMeetingDone())
                    .isFocused(existingAccounts.contains(participant.getOrganization()))
                    .build());
        }
        return response;
    }

    @Override
    public List<Participant> deleteParticipantById(Long id) {
        try{
            participantRepository.deleteById(id);
            log.info("Deleted participant with ID: {}", id);
            return getAllParticipant();
        } catch (Exception e) {
            log.error("Error deleting participant with ID {}: {}", id, e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    @Override
    public List<Participant> updateParticipantById(Participant participant) {
        try {
            ParticipantEntity existingParticipant = participantRepository.findById(participant.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + participant.getId()));

            // Update fields
            ParticipantEntity updatedParticipant = ParticipantEntity.builder()
                    .id(existingParticipant.getId())
                    .name(participant.getName())
                    .email(participant.getEmail())
                    .mobile(participant.getMobile())
                    .designation(participant.getDesignation())
                    .organization(participant.getOrganization())
                    .assignedUnassigned(participant.getAssignedUnassigned())
                    .attended(participant.getAttended())
                    .sheetName(existingParticipant.getSheetName()) // Keep original sheet name
                    .build();

            participantRepository.save(updatedParticipant);
            return getAllParticipant();
        } catch (Exception e) {
            log.error("Error updating participant: {}", e.getMessage());
            return List.of(); // Return empty list on error
        }
    }


    private Workbook createWorkbook(String fileName, InputStream inputStream) throws IOException {
        if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName != null && fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileName);
        }
    }

    private ParticipantEntity parseRowToParticipant(Row row, String sheetName, String sourceFile) {
        ParticipantEntity participant = ParticipantEntity.builder()
                .sheetName(sheetName)
                .name(getCellValueAsString(row.getCell(headerMappings.get("name"))))
                .designation(getCellValueAsString(row.getCell(headerMappings.get("designation"))))
                .organization(getCellValueAsString(row.getCell(headerMappings.get("organization"))))
                .email(getCellValueAsString(row.getCell(headerMappings.get("email"))))
                .mobile(getCellValueAsString(row.getCell(headerMappings.get("mobile"))))
                .attended(getCellValueAsString(row.getCell(headerMappings.get("attended"))))
                .assignedUnassigned(getCellValueAsString(row.getCell(headerMappings.get("assigned/unassigned"))))
                .eventName(getCellValueAsString(row.getCell(headerMappings.get("Event Name"))))
                .eventDate(OffsetDateTime.parse(getCellValueAsString(row.getCell(headerMappings.get("Date")))))
                .meetingDone(getCellValueAsString(row.getCell(headerMappings.get("Meeting Done"))))
                .build();
        log.info("Parsed participant from row {}: {}", row.getRowNum(), participant);
        return participant;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : value;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle phone numbers and other numeric data as strings
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // Try to evaluate formula
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue().trim();
                        case NUMERIC:
                            double numValue = cell.getNumericCellValue();
                            if (numValue == (long) numValue) {
                                return String.valueOf((long) numValue);
                            } else {
                                return String.valueOf(numValue);
                            }
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return cell.getCellFormula();
                    }
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < headerMappings.size(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidParticipant(ParticipantEntity participant) {
        // At least name should be present
        return participant.getName() != null && !participant.getName().trim().isEmpty();
    }

    /**
     * Parse the header row to create dynamic column mappings
     */
    private Map<String, Integer> parseHeaders(Sheet sheet, Map<String, Integer> headerMappings) {
        Row headerRow = sheet.getRow(1);
        if (headerRow == null) {
            log.warn("No header row found in sheet: {}", sheet.getSheetName());
            return headerMappings;
        }

        // Iterate through all cells in the header row
        for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
            Cell cell = headerRow.getCell(cellIndex);
            if (cell != null) {
                String headerValue = getCellValueAsString(cell);
                log.info("Header cell {}: '{}'", cellIndex, headerValue);
                if (headerValue != null) {
                    // Check if this header matches any of our expected headers
                    for (String expectedHeader : EXPECTED_HEADERS_PARTICIPANTS) {
                        log.info("Checking header '{}' against expected '{}'", headerValue, expectedHeader);
                        if (expectedHeader.equalsIgnoreCase(headerValue)) {
                            headerMappings.put(expectedHeader, cellIndex);
                            log.debug("Mapped header '{}' (original: '{}') to column {}",
                                    expectedHeader, headerValue, cellIndex);
                            break;
                        }
                    }
                }
            }
        }
        return headerMappings;
    }

    //Search API to get the Participant by name
    public List<Participant> searchParticipantFromAllFields(String name) {
        //Search the participant by name designation and organization
        List<ParticipantEntity> participant = participantRepository.findByNameOrDesignationOrOrganization(name);

        List<Participant> response = new ArrayList<>();
        for(ParticipantEntity participantEntity : participant) {
            response.add(Participant.builder()
                    .id(participantEntity.getId())
                    .name(participantEntity.getName())
                    .email(participantEntity.getEmail())
                    .mobile(participantEntity.getMobile())
                    .designation(participantEntity.getDesignation())
                    .organization(participantEntity.getOrganization())
                    .assignedUnassigned(participantEntity.getAssignedUnassigned())
                    .attended(participantEntity.getAttended())
                    .eventName(participantEntity.getEventName())
                    .eventDate(participantEntity.getEventDate())
                    .meetingDone(participantEntity.getMeetingDone())
                    .build());
        }
        return response;
    }

    @Override
    public List<Participant> filterParticipants(String field, String value) {
        // Validate field
        if (!headerMappings.containsKey(field)) {
            log.warn("Invalid field for filtering: {}", field);
            return Collections.emptyList();
        }

        // Fetch all participants and filter based on the specified field and value
        List<ParticipantEntity> allParticipants = participantRepository.getAll();
        List<Participant> filteredParticipants = new ArrayList<>();

        for (ParticipantEntity participant : allParticipants) {
            String fieldValue = getFieldValue(participant, field);
            if (fieldValue != null && fieldValue.toLowerCase().contains(value.toLowerCase())) {
                filteredParticipants.add(Participant.builder()
                        .id(participant.getId())
                        .name(participant.getName())
                        .email(participant.getEmail())
                        .mobile(participant.getMobile())
                        .designation(participant.getDesignation())
                        .organization(participant.getOrganization())
                        .assignedUnassigned(participant.getAssignedUnassigned())
                        .attended(participant.getAttended())
                        .eventName(participant.getEventName())
                        .eventDate(participant.getEventDate())
                        .meetingDone(participant.getMeetingDone())
                        .build());
            }
        }
        return filteredParticipants;
    }

    private String getFieldValue(ParticipantEntity participant, String field) {
        switch (field.toLowerCase()) {
            case "name":
                return participant.getName();
            case "designation":
                return participant.getDesignation();
            case "organization":
                return participant.getOrganization();
            case "email":
                return participant.getEmail();
            case "mobile":
                return participant.getMobile();
            case "attended":
                return participant.getAttended();
            case "assigned/unassigned":
                return participant.getAssignedUnassigned();
            case "event name":
                return participant.getEventName();
            case "meeting done":
                return participant.getMeetingDone();
            default:
                log.warn("Unknown field for filtering: {}", field);
                return null;
        }
    }
}