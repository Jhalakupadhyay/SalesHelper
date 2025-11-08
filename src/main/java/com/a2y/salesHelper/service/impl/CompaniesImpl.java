package com.a2y.salesHelper.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.a2y.salesHelper.db.entity.CompanyEntity;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.exception.ExcelValidationException;
import com.a2y.salesHelper.pojo.Companies;
import com.a2y.salesHelper.service.interfaces.CompaniesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompaniesImpl implements CompaniesService {
    private final CompaniesRepository companiesRepository;

    Map<String, Integer> headerMappings = new HashMap<>();

    private static final String[] EXPECTED_HEADERS_PARTICIPANTS = { "Account Name", "AE Name", "Segment",
            "Focus/Assigned", "Account Status", "PG/Pipeline Status", "Account Category", "City" };

    public CompaniesImpl(CompaniesRepository companiesRepository) {
        this.companiesRepository = companiesRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer parseExcelFile(MultipartFile file, Long clientId, Long tenantId) throws IOException {
        List<CompanyEntity> companies = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(fileName, inputStream);

            // Process all sheets in the workbook
            Sheet sheet = workbook.getSheetAt(0);
            String sheetName = sheet.getSheetName();
            parseHeaders(sheet, headerMappings);
            log.info("Headers parsed for sheet '{}'", sheetName);
            // Skip header row and process data rows
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row))
                    continue;
                try {
                    CompanyEntity company = parseRowToCompany(row, clientId, tenantId, sheetName, rowIndex + 1);
                    if (company != null) {
                        companies.add(company);
                    }
                } catch (ExcelValidationException e) {
                    // Re-throw validation exceptions to rollback transaction
                    throw e;
                } catch (Exception e) {
                    log.error("Error parsing row {} in sheet {} of file {}", rowIndex, sheetName, fileName, e);
                    throw new ExcelValidationException("row data", rowIndex + 1, sheetName);
                }
            }
            workbook.close();
        }
        if (!companies.isEmpty()) {
            log.info("Saving {} companies to the database", companies.size());
            Set<String> existingAccounts = new HashSet<>(
                    companiesRepository.findAllAccountsByTenantIdAndClientId(clientId, tenantId));
            companies.removeIf(company -> existingAccounts.contains(company.getAccountName()));
            companiesRepository.saveAll(companies);
        } else {
            log.warn("No valid company data found in the file: {}", fileName);
        }
        return companies.size();
    }

    @Override
    public List<Companies> getAllCompanies(Long clientId, Long tenantId) {
        List<CompanyEntity> companyEntities = companiesRepository.findAllByTenantIdAndClientId(tenantId, clientId);
        List<Companies> companiesList = new ArrayList<>();
        for (CompanyEntity entity : companyEntities) {
            Companies company = Companies.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .aeNam(entity.getAeNam())
                    .accountName(entity.getAccountName())
                    .segment(entity.getSegment())
                    .focusedOrAssigned(entity.getFocusedOrAssigned())
                    .accountStatus(entity.getAccountStatus())
                    .pipelineStatus(entity.getPipelineStatus())
                    .accountCategory(entity.getAccountCategory())
                    .build();
            companiesList.add(company);
        }
        return companiesList;
    }

    @Override
    public Page<Companies> getAllCompanies(Long clientId, Long tenantId, Pageable pageable) {
        Page<CompanyEntity> companyEntities = companiesRepository.findAllByTenantIdAndClientId(tenantId, clientId,
                pageable);
        return companyEntities.map(entity -> Companies.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .aeNam(entity.getAeNam())
                .accountName(entity.getAccountName())
                .segment(entity.getSegment())
                .focusedOrAssigned(entity.getFocusedOrAssigned())
                .accountStatus(entity.getAccountStatus())
                .pipelineStatus(entity.getPipelineStatus())
                .accountCategory(entity.getAccountCategory())
                .build());
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

    private CompanyEntity parseRowToCompany(Row row, Long clientId, Long tenantId, String sheetName, int rowNumber) {
        // Validate required fields
        String accountName = getCellValue(row, "Account Name");

        // Check for null or empty required fields
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new ExcelValidationException("Account Name", rowNumber, sheetName);
        }

        return CompanyEntity.builder()
                .clientId(clientId)
                .tenantId(tenantId)
                .accountName(accountName.trim())
                .aeNam(getCellValue(row, "AE Name"))
                .segment(getCellValue(row, "Segment"))
                .focusedOrAssigned(getCellValue(row, "Focus/Assigned"))
                .accountStatus(getCellValue(row, "Account Status"))
                .pipelineStatus(getCellValue(row, "PG/Pipeline Status"))
                .accountCategory(getCellValue(row, "Account Category"))
                .city(getCellValue(row, "City"))
                .build();
    }

    private String getCellValue(Row row, String headerName) {
        Integer index = headerMappings.get(headerName);
        if (index == null)
            return null;
        Cell cell = row.getCell(index);
        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return null;

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
    public Companies getCompanyById(Long id, Long clientId, Long tenantId) {

        if (id == null) {
            return null;
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findByIdAndClientIdAndTenantId(id, clientId,
                tenantId);
        if (optionalEntity.isPresent()) {
            CompanyEntity entity = optionalEntity.get();
            OffsetDateTime cooldownTime = null;
            return Companies.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .accountName(entity.getAccountName())
                    .aeNam(entity.getAeNam())
                    .segment(entity.getSegment())
                    .focusedOrAssigned(entity.getFocusedOrAssigned())
                    .accountStatus(entity.getAccountStatus())
                    .pipelineStatus(entity.getPipelineStatus())
                    .accountCategory(entity.getAccountCategory())
                    .build();
        }
        return null;
    }

    @Override
    public List<Companies> filterCompanies(String field, String value, Long clientId, Long tenantId) {
        if (field == null || value == null || field.trim().isEmpty() || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<CompanyEntity> companyEntities = getFieldValue(field, value, clientId, tenantId);

        List<Companies> companiesList = new ArrayList<>();
        for (CompanyEntity entity : companyEntities) {
            Companies company = Companies.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .aeNam(entity.getAeNam())
                    .accountName(entity.getAccountName())
                    .segment(entity.getSegment())
                    .focusedOrAssigned(entity.getFocusedOrAssigned())
                    .accountStatus(entity.getAccountStatus())
                    .pipelineStatus(entity.getPipelineStatus())
                    .accountCategory(entity.getAccountCategory())
                    .build();
            companiesList.add(company);
        }
        return companiesList;
    }

    @Override
    public Companies updateCompanyById(Companies company) {
        if (company == null || company.getId() == null) {
            throw new IllegalArgumentException("Company or Company ID cannot be null");
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findByIdAndClientIdAndTenantId(company.getId(),
                company.getClientId(), company.getTenantId());
        if (optionalEntity.isPresent()) {
            CompanyEntity entity = optionalEntity.get();
            entity.setAccountName(company.getAccountName());
            entity.setAeNam(company.getAeNam());
            entity.setSegment(company.getSegment());
            entity.setFocusedOrAssigned(company.getFocusedOrAssigned());
            entity.setAccountStatus(company.getAccountStatus());
            entity.setPipelineStatus(company.getPipelineStatus());
            entity.setAccountCategory(company.getAccountCategory());

            CompanyEntity updatedEntity = companiesRepository.save(entity);
            return Companies.builder()
                    .id(updatedEntity.getId())
                    .clientId(updatedEntity.getClientId())
                    .accountName(updatedEntity.getAccountName())
                    .aeNam(updatedEntity.getAeNam())
                    .segment(updatedEntity.getSegment())
                    .focusedOrAssigned(updatedEntity.getFocusedOrAssigned())
                    .accountStatus(updatedEntity.getAccountStatus())
                    .pipelineStatus(updatedEntity.getPipelineStatus())
                    .accountCategory(updatedEntity.getAccountCategory())
                    .build();
        }
        throw new NoSuchElementException("Company with ID " + company.getId() + " not found");
    }

    @Override
    public Boolean deleteCompanyById(Long id, Long clientId, Long tenantId) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        Optional<CompanyEntity> optionalEntity = companiesRepository.findByIdAndClientIdAndTenantId(id, clientId,
                tenantId);
        if (optionalEntity.isPresent()) {
            companiesRepository.deleteById(id);
            return true;
        }
        return false; // Return false if the company with the given ID does not exist
    }

    private List<CompanyEntity> getFieldValue(String field, String value, Long clientId, Long tennatId) {
        switch (field.toLowerCase()) {
            case "company":
                return companiesRepository.findByClientIdAndAccountNameAndTenantId(clientId, value, tennatId);
            case "aename":
                return companiesRepository.findByAeNamAndTenantIdAndClientIdIgnoreCase(value, tennatId, clientId);
            case "city":
                return companiesRepository.findByClientIdAndTenantIdAndCityIgnoreCase(value, tennatId, clientId);
            case "focusedorassigned":
                return companiesRepository.findByClientIdAndTenantIdAndFocusedOrAssigned(clientId, tennatId, value);
            default:
                throw new RuntimeException("\"Unknown field for filtering: {}\", field");
        }
    }
}
