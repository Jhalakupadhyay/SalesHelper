package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.PersonaEntity;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.db.repository.PersonaRepository;
import com.a2y.salesHelper.pojo.Persona;
import com.a2y.salesHelper.service.interfaces.PersonaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository companyContactRepository;
    private final CompaniesRepository companiesRepository;

    // Fixed header mappings for CompanyContact fields
    private Map<String, Integer> headerMappings = new HashMap<>();
    private static final String[] EXPECTED_HEADERS = {
            "company", "name", "designation"
    };

    public PersonaServiceImpl(PersonaRepository companyContactRepository, CompaniesRepository companiesRepository) {
        this.companyContactRepository = companyContactRepository;
        this.companiesRepository = companiesRepository;
    }

    @Override
    public Integer parseExcelFile(MultipartFile file, Long clientId) throws IOException {
        List<PersonaEntity> companyContacts = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(fileName, inputStream);

            // Process all sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();

                // Clear and parse headers for each sheet
                headerMappings.clear();
                parseHeaders(sheet, headerMappings);
                log.info("Headers parsed for sheet '{}': {}", sheetName, headerMappings);

                // Skip header row and process data rows
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null || isRowEmpty(row)) continue;

                    try {
                        PersonaEntity companyContact = parseRowToCompanyContact(row, sheetName, clientId);
                        if (companyContact != null && isValidCompanyContact(companyContact)) {
                            companyContacts.add(companyContact);
                            log.info("Parsed company contact: {}", companyContact);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing row {} in sheet {} of file {}: {}",
                                rowIndex, sheetName, fileName, e.getMessage());
                    }
                }
            }
            workbook.close();
        }

        // Save all company contacts to database
        if (!companyContacts.isEmpty()) {
            // Remove duplicates based on company, name, and designation
            companyContacts.removeIf(contact ->
                    companyContactRepository.existsByCompanyAndNameAndDesignationAndClientId(
                            contact.getCompany(),
                            contact.getName(),
                            contact.getDesignation(),
                            contact.getClientId()
                    )
            );

            if (!companyContacts.isEmpty()) {
                Map<String, Long> existingCompanies = new HashMap<>();
                for( PersonaEntity contact : companyContacts) {
                    if(existingCompanies.containsKey(contact.getCompany())) {
                        contact.setCompanyId(existingCompanies.get(contact.getCompany()));
                    }else {
                        Long id =  companiesRepository.findByAccountName(contact.getCompany(),clientId);
                        if(id != null) {
                            contact.setCompanyId(id);
                            existingCompanies.put(contact.getCompany(), id);
                        }
                    }
                }
                companyContactRepository.saveAll(companyContacts);
                log.info("Saved {} company contacts to database", companyContacts.size());
            } else {
                log.info("All company contacts already exist in database");
            }
        }

        return companyContacts.size();
    }

    @Override
    public List<Persona> getAllCompanyContacts(Long clientId) {
        List<PersonaEntity> entities = companyContactRepository.findByClientId(clientId);
        List<Persona> response = new ArrayList<>();

        for (PersonaEntity entity : entities) {
            response.add(Persona.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .company(entity.getCompany())
                    .name(entity.getName())
                    .designation(entity.getDesignation())
                    .sheetName(entity.getSheetName())
                    .build());
        }

        return response;
    }

    @Override
    public Boolean deleteCompanyContactById(Long id) {
        try {
            companyContactRepository.deleteById(id);
            log.info("Deleted company contact with ID: {}", id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error deleting company contact with ID {}: {}", id, e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean updateCompanyContactById(Persona companyContact) {
        try {
            PersonaEntity existingContact = companyContactRepository.findById(companyContact.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Company contact not found with ID: " + companyContact.getId()));

            // Update fields
            PersonaEntity updatedContact = PersonaEntity.builder()
                    .id(existingContact.getId())
                    .clientId(existingContact.getClientId()) // Keep original client ID
                    .company(companyContact.getCompany())
                    .name(companyContact.getName())
                    .designation(companyContact.getDesignation())
                    .sheetName(existingContact.getSheetName()) // Keep original sheet name
                    .build();

            companyContactRepository.save(updatedContact);
            log.info("Updated company contact with ID: {}", companyContact.getId());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error updating company contact: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public List<Persona> searchByCompany(String company, Long clientId) {
        List<PersonaEntity> entities = companyContactRepository.findByCompanyContainingIgnoreCaseAndClientId(company, clientId);
        List<Persona> response = new ArrayList<>();

        for (PersonaEntity entity : entities) {
            response.add(Persona.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .company(entity.getCompany())
                    .name(entity.getName())
                    .designation(entity.getDesignation())
                    .sheetName(entity.getSheetName())
                    .build());
        }

        return response;
    }

    @Override
    public List<Persona> searchByName(String name, Long clientId) {
        List<PersonaEntity> entities = companyContactRepository.findByNameContainingIgnoreCaseAndClientId(name, clientId);
        List<Persona> response = new ArrayList<>();

        for (PersonaEntity entity : entities) {
            response.add(Persona.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .company(entity.getCompany())
                    .name(entity.getName())
                    .designation(entity.getDesignation())
                    .sheetName(entity.getSheetName())
                    .build());
        }

        return response;
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

    private PersonaEntity parseRowToCompanyContact(Row row, String sheetName, Long clientId) {
        PersonaEntity companyContact = PersonaEntity.builder()
                .sheetName(sheetName)
                .clientId(clientId)
                .company(getCellValueAsString(getCell(row, "company")))
                .name(getCellValueAsString(getCell(row, "name")))
                .designation(getCellValueAsString(getCell(row, "designation")))
                .build();

        log.debug("Parsed company contact from row {}: {}", row.getRowNum(), companyContact);
        return companyContact;
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

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : value;
            case NUMERIC:
                // Handle numeric data as strings
                double numValue = cell.getNumericCellValue();
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                } else {
                    return String.valueOf(numValue);
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
                            double formNumValue = cell.getNumericCellValue();
                            if (formNumValue == (long) formNumValue) {
                                return String.valueOf((long) formNumValue);
                            } else {
                                return String.valueOf(formNumValue);
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

    private boolean isValidCompanyContact(PersonaEntity companyContact) {
        // At least company and name should be present
        return companyContact.getCompany() != null && !companyContact.getCompany().trim().isEmpty() &&
                companyContact.getName() != null && !companyContact.getName().trim().isEmpty();
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
                log.debug("Header cell {}: '{}'", cellIndex, headerValue);
                if (headerValue != null) {
                    // Check if this header matches any of our expected headers
                    for (String expectedHeader : EXPECTED_HEADERS) {
                        if (expectedHeader.equalsIgnoreCase(headerValue.trim())) {
                            headerMappings.put(expectedHeader, cellIndex);
                            log.debug("Mapped header '{}' (original: '{}') to column {}",
                                    expectedHeader, headerValue, cellIndex);
                            break;
                        }
                    }
                }
            }
        }

        log.info("Final header mappings: {}", headerMappings);
        return headerMappings;
    }
}
