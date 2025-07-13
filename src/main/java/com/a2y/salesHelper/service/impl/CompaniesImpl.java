package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.ClientEntity;
import com.a2y.salesHelper.db.entity.CompanyEntity;
import com.a2y.salesHelper.db.repository.ClientRepository;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.pojo.Companies;
import com.a2y.salesHelper.service.interfaces.CompaniesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
public class CompaniesImpl implements CompaniesService {
    private final CompaniesRepository companiesRepository;
    private final ClientRepository clientRepository;

    Map<String, Integer> headerMappings = new HashMap<>();

    private static final String[] EXPECTED_HEADERS_PARTICIPANTS = {
            "Accounts", "Account Owner","Type","Focused/Assigned","ETM Region","Account Tier","Meeting Update","Quarter"
            ,"Meeting Initiative","SDR Responsible","Sales Team Remarks","SDR Remark","Salespin Remark","Marketing Remark"
            ,"Customer Name","Designation","Mob. No.","Email ID"
    };

    public CompaniesImpl(CompaniesRepository companiesRepository, ClientRepository clientRepository) {
        this.companiesRepository = companiesRepository;
        this.clientRepository = clientRepository;
    }


    @Override
    public Integer parseExcelFile(MultipartFile file,Long clientId) throws IOException {
        List<CompanyEntity> companies = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(fileName, inputStream);

            // Process all sheets in the workbook
                Sheet sheet = workbook.getSheetAt(0);
                String sheetName = sheet.getSheetName();
                parseHeaders(sheet, headerMappings);
                log.info("Headers Parsed for sheet '{}': {}", sheetName, headerMappings);
                // Skip header row and process data rows
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null || isRowEmpty(row)) continue;
                    try {
                        CompanyEntity company = parseRowToCompany(row,clientId);
                        if (company != null) {
                            companies.add(company);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing row " + rowIndex + " in sheet " + sheetName +
                                " of file " + fileName + ": " + e.getMessage());
                    }
                }
            workbook.close();
        }
        if(!companies.isEmpty()) {
            log.info("Saving {} companies to the database", companies.size());
            Set<String> existingAccounts = new HashSet<>(companiesRepository.findAllAccounts(clientId));
            companies.removeIf(company -> existingAccounts.contains(company.getAccounts()));
            companiesRepository.saveAll(companies);
        } else {
            log.warn("No valid company data found in the file: {}", fileName);
        }
        return companies.size();
    }

    @Override
    public List<Companies> getAllCompanies(Long clientId) {
        List<CompanyEntity> companyEntities = companiesRepository.findAllByClientId(clientId);
        List<Companies> companiesList = new ArrayList<>();
        for (CompanyEntity entity : companyEntities) {
            Companies company = Companies.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .accounts(entity.getAccounts())
                    .accountOwner(entity.getAccountOwner())
                    .type(entity.getType())
                    .focusedOrAssigned(entity.getFocusedOrAssigned())
                    .etmRegion(entity.getEtmRegion())
                    .accountTier(entity.getAccountTier())
                    .meetingUpdate(entity.getMeetingUpdate())
                    .quarter(entity.getQuarter())
                    .meetingInitiative(entity.getMeetingInitiative())
                    .sdrResponsible(entity.getSdrResponsible())
                    .salesTeamRemarks(entity.getSalesTeamRemarks())
                    .sdrRemark(entity.getSdrRemark())
                    .salespinRemark(entity.getSalespinRemark())
                    .marketingRemark(entity.getMarketingRemark())
                    .customerName(entity.getCustomerName())
                    .designation(entity.getDesignation())
                    .mobileNumber(entity.getMobileNumber())
                    .email(entity.getEmail())
                    .build();
            companiesList.add(company);
        }
        return companiesList;
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

    private CompanyEntity parseRowToCompany(Row row,Long clientId) {

        return CompanyEntity.builder()
                .accounts(getCellValue(row, "Accounts"))
                .accountOwner(getCellValue(row, "Account Owner"))
                .type(getCellValue(row, "Type"))
                .focusedOrAssigned(getCellValue(row, "Focused/Assigned"))
                .etmRegion(getCellValue(row, "ETM Region"))
                .accountTier(getCellValue(row, "Account Tier"))
                .meetingUpdate(getCellValue(row, "Meeting Update"))
                .quarter(getCellValue(row, "Quarter"))
                .meetingInitiative(getCellValue(row, "Meeting Initiative"))
                .sdrResponsible(getCellValue(row, "SDR Responsible"))
                .salesTeamRemarks(getCellValue(row, "Sales Team Remarks"))
                .sdrRemark(getCellValue(row, "SDR Remark"))
                .salespinRemark(getCellValue(row, "Salespin Remark"))
                .marketingRemark(getCellValue(row, "Marketing Remark"))
                .customerName(getCellValue(row, "Customer Name"))
                .designation(getCellValue(row, "Designation"))
                .mobileNumber(
                        Optional.ofNullable(getCellValue(row, "Mob. No."))
                                .map(Long::valueOf)
                                .orElse(null))
                .email(getCellValue(row, "Email ID"))
                .build();
    }

    private String getCellValue(Row row, String headerName) {
        Integer index = headerMappings.get(headerName);
        if (index == null) return null;
        Cell cell = row.getCell(index);
        return getCellValueAsString(cell);
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
                        if (expectedHeader.equalsIgnoreCase(headerValue)) {
                            headerMappings.put(expectedHeader, cellIndex);
                            break;
                        }
                    }
                }
            }
        }
        return headerMappings;
    }

    @Override
    public Companies getCompanyById(Long id,Long clientId) {

        if (id == null) {
            return null;
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findByIdAndClientId(id,clientId);
        if (optionalEntity.isPresent()) {
            CompanyEntity entity = optionalEntity.get();
            OffsetDateTime cooldownTime = null;
            return Companies.builder()
                    .id(entity.getId())
                    .accounts(entity.getAccounts())
                    .accountOwner(entity.getAccountOwner())
                    .type(entity.getType())
                    .focusedOrAssigned(entity.getFocusedOrAssigned())
                    .etmRegion(entity.getEtmRegion())
                    .accountTier(entity.getAccountTier())
                    .meetingUpdate(entity.getMeetingUpdate())
                    .quarter(entity.getQuarter())
                    .meetingInitiative(entity.getMeetingInitiative())
                    .sdrResponsible(entity.getSdrResponsible())
                    .salesTeamRemarks(entity.getSalesTeamRemarks())
                    .sdrRemark(entity.getSdrRemark())
                    .salespinRemark(entity.getSalespinRemark())
                    .marketingRemark(entity.getMarketingRemark())
                    .customerName(entity.getCustomerName())
                    .designation(entity.getDesignation())
                    .mobileNumber(entity.getMobileNumber())
                    .email(entity.getEmail())
                    .build();
        }
        return null;
    }

    @Override
    public List<Companies> filterCompanies(String field, String value) {
        if (field == null || value == null || field.trim().isEmpty() || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<CompanyEntity> companyEntities = companiesRepository.findAll();
        List<Companies> filteredCompanies = new ArrayList<>();
        for (CompanyEntity entity : companyEntities) {
            String fieldValue = getFieldValue(entity, field);
            if (fieldValue != null && fieldValue.equalsIgnoreCase(value)) {
                Companies company = Companies.builder()
                        .accounts(entity.getAccounts())
                        .accountOwner(entity.getAccountOwner())
                        .type(entity.getType())
                        .focusedOrAssigned(entity.getFocusedOrAssigned())
                        .etmRegion(entity.getEtmRegion())
                        .accountTier(entity.getAccountTier())
                        .meetingUpdate(entity.getMeetingUpdate())
                        .quarter(entity.getQuarter())
                        .meetingInitiative(entity.getMeetingInitiative())
                        .sdrResponsible(entity.getSdrResponsible())
                        .salesTeamRemarks(entity.getSalesTeamRemarks())
                        .sdrRemark(entity.getSdrRemark())
                        .salespinRemark(entity.getSalespinRemark())
                        .marketingRemark(entity.getMarketingRemark())
                        .customerName(entity.getCustomerName())
                        .designation(entity.getDesignation())
                        .mobileNumber(entity.getMobileNumber())
                        .email(entity.getEmail())
                        .build();
                filteredCompanies.add(company);
            }
        }
        return filteredCompanies;
    }

    @Override
    public Companies updateCompanyById(Companies company) {
        if (company == null || company.getId() == null) {
            throw new IllegalArgumentException("Company or Company ID cannot be null");
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findByIdAndClientId(company.getId(), company.getClientId());
        if (optionalEntity.isPresent()) {
            CompanyEntity entity = optionalEntity.get();
            entity.builder()
                    .accounts(company.getAccounts())
                    .accountOwner(company.getAccountOwner())
                    .type(company.getType())
                    .focusedOrAssigned(company.getFocusedOrAssigned())
                    .etmRegion(company.getEtmRegion())
                    .accountTier(company.getAccountTier())
                    .meetingUpdate(company.getMeetingUpdate())
                    .quarter(company.getQuarter())
                    .meetingInitiative(company.getMeetingInitiative())
                    .sdrResponsible(company.getSdrResponsible())
                    .salesTeamRemarks(company.getSalesTeamRemarks())
                    .sdrRemark(company.getSdrRemark())
                    .salespinRemark(company.getSalespinRemark())
                    .marketingRemark(company.getMarketingRemark())
                    .customerName(company.getCustomerName())
                    .designation(company.getDesignation())
                    .mobileNumber(company.getMobileNumber())
                    .email(company.getEmail());

            CompanyEntity updatedEntity = companiesRepository.save(entity);
            return Companies.builder()
                    .id(updatedEntity.getId())
                    .accounts(updatedEntity.getAccounts())
                    .accountOwner(updatedEntity.getAccountOwner())
                    .type(updatedEntity.getType())
                    .focusedOrAssigned(updatedEntity.getFocusedOrAssigned())
                    .etmRegion(updatedEntity.getEtmRegion())
                    .accountTier(updatedEntity.getAccountTier())
                    .meetingUpdate(updatedEntity.getMeetingUpdate())
                    .quarter(updatedEntity.getQuarter())
                    .meetingInitiative(updatedEntity.getMeetingInitiative())
                    .sdrResponsible(updatedEntity.getSdrResponsible())
                    .salesTeamRemarks(updatedEntity.getSalesTeamRemarks())
                    .sdrRemark(updatedEntity.getSdrRemark())
                    .salespinRemark(updatedEntity.getSalespinRemark())
                    .marketingRemark(updatedEntity.getMarketingRemark())
                    .customerName(updatedEntity.getCustomerName())
                    .designation(updatedEntity.getDesignation())
                    .mobileNumber(updatedEntity.getMobileNumber())
                    .email(updatedEntity.getEmail())
                    .build();
        }
        throw new NoSuchElementException("Company with ID " + company.getId() + " not found");
    }

    @Override
    public Boolean deleteCompanyById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findById(id);
        if (optionalEntity.isPresent()) {
            companiesRepository.deleteById(id);
            return true;
        }
        return false; // Return false if the company with the given ID does not exist
    }

    private String getFieldValue(CompanyEntity entity, String field) {
        switch (field.toLowerCase()) {
            case "accounts":
                return entity.getAccounts();
            case "accountowner":
                return entity.getAccountOwner();
            case "customername":
                return entity.getCustomerName();
            case "email":
                return entity.getEmail();
            default:
                log.warn("Unknown field for filtering: {}", field);
                return null;
        }
    }
}
