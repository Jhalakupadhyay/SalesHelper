package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.ClientEntity;
import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.entity.ParticipantEntity;
import com.a2y.salesHelper.db.repository.ClientRepository;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.pojo.Participant;
import com.a2y.salesHelper.service.interfaces.ParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.atp.Switch;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final CompaniesRepository companiesRepository;
    private final InteractionHistoryRepository interactionHistoryRepository;
    private final ClientRepository clientRepository;

    // Fixed header mappings for Participant fields - customize these based on your Excel structure
    Map<String, Integer> headerMappings = new HashMap<>();
    private static final String[] EXPECTED_HEADERS_PARTICIPANTS = {
            "name", "designation", "Company Name", "email Id", "mobile no", "assigned/unassigned" , "Event Name" , "Date" ,"Meeting Done","City"
    };

    public ParticipantServiceImpl(ParticipantRepository participantRepository, CompaniesRepository companiesRepository, InteractionHistoryRepository interactionHistoryRepository, ClientRepository clientRepository) {
        this.participantRepository = participantRepository;
        this.companiesRepository = companiesRepository;
        this.interactionHistoryRepository = interactionHistoryRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Integer parseExcelFile(MultipartFile file,Long clientId) throws IOException {
        List<ParticipantEntity> participants = new ArrayList<>();
        List<InteractionHistoryEntity> interactionHistories = new ArrayList<>();
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
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null || isRowEmpty(row)) continue;
                    try {
                        ParticipantEntity participant = parseRowToParticipant(row, sheetName, clientId);
                        InteractionHistoryEntity interactionHistory = parseRowToInteractions(row,clientId);
                        if (participant != null && isValidParticipant(participant)) {
                            participants.add(participant);
                            log.info("Parsed participant: {}", participant);
                        }
                        if( interactionHistory != null)
                        {
                            interactionHistories.add(interactionHistory);
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
            Map<String,Long> map = new HashMap<>();
            participants.removeIf(participant -> participantRepository.existsByNameAndDesignationAndOrganization(participant.getName(), participant.getDesignation(),participant.getOrganization()));
            for (ParticipantEntity participant : participants) {
                if(!map.containsKey(participant.getOrganization())) {
                    Long orgId = companiesRepository.findByOrganizationAndClientId(participant.getOrganization(), clientId);
                    if (orgId != null) {
                        map.put(participant.getOrganization(), orgId);
                    }
                }
                participant.setOrgId(map.get(participant.getOrganization()));
                participant.setClientId(clientId);
            }
            participantRepository.saveAll(participants);
        }
        // Save all interaction histories to database
        if (!interactionHistories.isEmpty()) {
            log.info("Saving interaction histories: {}", interactionHistories);
            interactionHistories.removeIf(interactionHistory -> interactionHistory.getEventDate() == null);
            addInteractionHistories(interactionHistories);
        }
        return participants.size();
    }

    private InteractionHistoryEntity parseRowToInteractions(Row row,Long clientId) {
        return InteractionHistoryEntity.builder()
                .clientId(clientId)
                .participantName(getCellValueAsString(getCell(row, "name")))
                .designation(getCellValueAsString(getCell(row, "designation")))
                .organization(getCellValueAsString(getCell(row, "Company Name")))
                .eventName(getCellValueAsString(getCell(row, "Event Name")))
                .eventDate(parseOffsetDateTime(getCellValueAsString(getCell(row, "Date"))))
                .description("")
                .meetingDone(getCellValueAsString(getCell(row, "Meeting Status")) != null && getCellValueAsString(getCell(row, "Meeting Status")).equalsIgnoreCase("Done"))
                .build();
    }

    @Override
    public List<Participant> getAllParticipant(Long clientId) {
        List<ParticipantEntity> participantEntities = participantRepository.findByClientId(clientId);
        List<Participant> response = new ArrayList<>();

        for (ParticipantEntity participant : participantEntities) {
            response.add(Participant.builder()
                    .id(participant.getId())
                    .name(participant.getName())
                    .email(participant.getEmail())
                    .mobile(participant.getMobile())
                    .designation(participant.getDesignation())
                    .organization(participant.getOrganization())
                    .assignedUnassigned(participant.getAssignedUnassigned())
                    .attended(participant.getAttended())
                    .eventDate(participant.getEventDate())
                    .sheetName(participant.getSheetName())
                    .build());
        }
        return response;
    }

    @Override
    public Boolean deleteParticipantById(Long id) {
        try{
            participantRepository.deleteById(id);
            log.info("Deleted participant with ID: {}", id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error deleting participant with ID {}: {}", id, e.getMessage());
            return Boolean.FALSE; // Return empty list on error
        }
    }

    @Override
    public Boolean updateParticipantById(Participant participant) {

        try {
            ParticipantEntity existingParticipant = participantRepository.findById(participant.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Participant not found with ID: " + participant.getId()));

            // Update fields
            ParticipantEntity updatedParticipant = ParticipantEntity.builder()
                    .id(existingParticipant.getId())
                    .clientId(existingParticipant.getClientId()) // Keep original client ID
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
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error updating participant: {}", e.getMessage());
            return Boolean.FALSE; // Return empty list on error
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

    private ParticipantEntity parseRowToParticipant(Row row, String sheetName, Long clientId) {
        ParticipantEntity participant = ParticipantEntity.builder()
                .sheetName(sheetName)
                .clientId(clientId)
                .name(getCellValueAsString(getCell(row, "name")))
                .designation(getCellValueAsString(getCell(row, "designation")))
                .organization(getCellValueAsString(getCell(row, "Company Name")))
                .email(getCellValueAsString(getCell(row, "email Id")))
                .mobile(getCellValueAsString(getCell(row, "mobile no")))
                .eventName(getCellValueAsString(getCell(row, "Event Name")))
                .city(getCellValueAsString(getCell(row, "City")))
                .isGoodLead(true)
                .assignedUnassigned(getCellValueAsString(getCell(row, "assigned/unassigned")))
                .eventDate(parseOffsetDateTime(getCellValueAsString(getCell(row, "Date"))))
                .build();
        log.info("Parsed participant from row {}: {}", row.getRowNum(), participant);
        return participant;
    }

    /**
     * Safely get a cell from a row using header mapping, with null checking
     */
    private Cell getCell(Row row, String headerName) {
        Integer columnIndex = headerMappings.get(headerName);
        if (columnIndex == null) {
            log.debug("Header '{}' not found in mapping, returning null cell", headerName);
            return null;
        }
        return row.getCell(columnIndex);
    }

    /**
     * Parse date string to OffsetDateTime with proper error handling
     */
    private OffsetDateTime parseOffsetDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            // First, try to parse as ISO-8601 format (if it's already in the correct format)
            return OffsetDateTime.parse(dateString);
        } catch (DateTimeParseException e1) {
            try {
                // Try to parse the Java Date.toString() format: "Wed Mar 12 00:00:00 IST 2025"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                return OffsetDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e2) {
                try {
                    // Try to parse common date formats
                    DateTimeFormatter[] commonFormats = {
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
                            DateTimeFormatter.ofPattern("yyyy/MM/dd")
                    };

                    for (DateTimeFormatter format : commonFormats) {
                        try {
                            return OffsetDateTime.parse(dateString + "T00:00:00Z",
                                    DateTimeFormatter.ofPattern(format.toString() + "'T'HH:mm:ss'Z'"));
                        } catch (DateTimeParseException ignored) {
                            // Continue to next format
                        }
                    }

                    log.warn("Could not parse date string: '{}'. Setting date to null.", dateString);
                    return null;
                } catch (Exception e3) {
                    log.warn("Could not parse date string: '{}'. Setting date to null.", dateString);
                    return null;
                }
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : value;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // For date cells, get the Date object and convert to OffsetDateTime
                    Date dateValue = cell.getDateCellValue();
                    return dateValue.toInstant().atOffset(ZoneOffset.UTC).toString();
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
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date dateValue = cell.getDateCellValue();
                                return dateValue.toInstant().atOffset(ZoneOffset.UTC).toString();
                            } else {
                                double numValue = cell.getNumericCellValue();
                                if (numValue == (long) numValue) {
                                    return String.valueOf((long) numValue);
                                } else {
                                    return String.valueOf(numValue);
                                }
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
        for (String headerName : headerMappings.keySet()) {
            Cell cell = getCell(row, headerName);
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
        Row headerRow = sheet.getRow(0);
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

    @Override
    public List<Participant> filterParticipants(String field, String value,Long clientId, String startDate, String endDate) {
        // Validate field
        if (field == null || field.trim().isEmpty()) {
            log.warn("Invalid field for filtering: {}", field);
            return Collections.emptyList();
        }
       //switch through all cases and make db call for the fileds
        List<ParticipantEntity> participants;
        switch (field.toLowerCase()) {
            case "name":
                participants = participantRepository.findByNameAndClientId(value, clientId);
                break;
            case "designation":
                participants = participantRepository.findByDesignationAndClientId(value, clientId);
                break;
            case "organization":
                participants = participantRepository.findByOrganizationAndClientId(value, clientId);
                break;
            case "date":
                participants = participantRepository.findByEventDateBetweenAndClientId(parseOffsetDateTime(startDate),parseOffsetDateTime(endDate),clientId);
                break;
            case "assignedorunassigned":
                participants = participantRepository.findByAssignedUnassignedAndClientId(value, clientId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported field for filtering: " + field);
        }

        // Convert to Participant DTOs
        List<Participant> response = new ArrayList<>();
        for (ParticipantEntity participant : participants) {
            response.add(Participant.builder()
                    .id(participant.getId())
                    .name(participant.getName())
                    .email(participant.getEmail())
                    .mobile(participant.getMobile())
                    .designation(participant.getDesignation())
                    .organization(participant.getOrganization())
                    .assignedUnassigned(participant.getAssignedUnassigned())
                    .attended(participant.getAttended())
                    .eventDate(participant.getEventDate())
                    .build());
        }
        return response;
    }

    @Override
    public List<Participant> searchParticipant(String name, Long clientId) {
        // Search participants by name and clientId
        List<ParticipantEntity> participants = participantRepository.findByNameAndClientId(name, clientId);
        List<Participant> response = new ArrayList<>();

        for (ParticipantEntity participant : participants) {
            response.add(Participant.builder()
                    .id(participant.getId())
                    .name(participant.getName())
                    .email(participant.getEmail())
                    .mobile(participant.getMobile())
                    .designation(participant.getDesignation())
                    .organization(participant.getOrganization())
                    .assignedUnassigned(participant.getAssignedUnassigned())
                    .attended(participant.getAttended())
                    .eventDate(participant.getEventDate())
                    .build());
        }
        return response;
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
            default:
                log.warn("Unknown field for filtering: {}", field);
                return null;
        }
    }

    public boolean addInteractionHistories(List<InteractionHistoryEntity> interactionHistories) {

        if (interactionHistories == null || interactionHistories.isEmpty()) {
            log.warn("No interaction histories provided");
            return false;
        }

        List<InteractionHistoryEntity> entitiesToSave = new ArrayList<>();

        for (InteractionHistoryEntity interactionHistory : interactionHistories) {

            ClientEntity client = clientRepository.getReferenceById(interactionHistory.getClientId());

            if (client == null) {
                log.error("Client with ID {} not found. Skipping this record.", interactionHistory.getClientId());
                continue; // Skip this record but continue processing others
            }

//            Long cooldown1 = client.getCooldownPeriod1();
//            Long cooldown2 = client.getCooldownPeriod2();
//            Long cooldown3 = client.getCooldownPeriod3();

            OffsetDateTime cooldownDate = null;
            int cooldownCount = 1;

            InteractionHistoryEntity latestInteraction = interactionHistoryRepository
                    .findTopByParticipantNameAndOrganizationAndClientIdOrderByCreatedAtDesc(
                            interactionHistory.getParticipantName(),
                            interactionHistory.getOrganization(),
                            interactionHistory.getClientId()
                    );

//            if (latestInteraction != null) {
//                if (latestInteraction.getCooldownCount() == 1) {
//                    if (interactionHistory.getEventDate().isBefore(latestInteraction.getCooldownDate())) {
//                        cooldownDate = interactionHistory.getEventDate().plusDays(cooldown2);
//                        cooldownCount = 2;
//                    } else {
//                        cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
//                        cooldownCount = 1;
//                    }
//                } else if (latestInteraction.getCooldownCount() == 2) {
//                    if (interactionHistory.getEventDate().isBefore(latestInteraction.getCooldownDate())) {
//                        cooldownDate = interactionHistory.getEventDate().plusDays(cooldown3);
//                        cooldownCount = 3;
//                    } else {
//                        cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
//                        cooldownCount = 1;
//                    }
//                } else {
//                    cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
//                    cooldownCount = 1;
//                }
//            } else {
//                cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
//                cooldownCount = 1;
//            }

            if(latestInteraction!=null)
            {
                if(latestInteraction.getCooldownDate().isEqual(interactionHistory.getEventDate()) || latestInteraction.getCooldownDate().isAfter(interactionHistory.getEventDate())) {
                    //update the pariticpant in db with isGoodLead false
                    Optional<ParticipantEntity> participantOpt = participantRepository.findByNameAndDesignationAndOrganizationAndClientId(
                            interactionHistory.getParticipantName(),
                            interactionHistory.getDesignation(),
                            interactionHistory.getOrganization(),
                            interactionHistory.getClientId()
                    );
                    participantOpt.ifPresent(participant -> {
                        participant.setIsGoodLead(Boolean.FALSE);
                        participantRepository.save(participant);
                    });
                    cooldownDate = latestInteraction.getCooldownDate();
                }else {
                    cooldownDate = latestInteraction.getCooldownDate().plusDays(30);
                    cooldownCount = 1;
                }
            }else {
                cooldownDate = interactionHistory.getEventDate().plusDays(30);
                cooldownCount = 1;
            }

            interactionHistory.setCooldownDate(cooldownDate);
            interactionHistory.setCooldownCount(cooldownCount);
            interactionHistory.setMeetingDone(Boolean.TRUE);

            log.info("Prepared interaction history for participant: {}", interactionHistory);

            // Update participant event date
            participantRepository.findByNameAndDesignationAndOrganizationAndClientId(
                    interactionHistory.getParticipantName(),
                    interactionHistory.getDesignation(),
                    interactionHistory.getOrganization(),
                    interactionHistory.getClientId()
            ).ifPresent(participant -> {
                participant.setEventDate(interactionHistory.getEventDate());
                participantRepository.save(participant);
            });

            entitiesToSave.add(interactionHistory);
        }

        if (!entitiesToSave.isEmpty()) {
            interactionHistoryRepository.saveAll(entitiesToSave);
            log.info("Saved {} interaction histories", entitiesToSave.size());
            return true;
        }

        log.warn("No interaction histories saved");
        return false;
    }

    @Override
    public List<ParticipantEntity> getParticipantsForOrganization(Long orgId, Long clientId) {
        return participantRepository.findByClientIdAndOrgId(clientId, orgId);
    }

}