package com.a2y.salesHelper.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.a2y.salesHelper.pojo.Persona;

public interface PersonaService {

    /**
     * ParseExcel file and save company contacts to database
     * 
     * @param file     Excel file containing company contact data
     * @param clientId Client ID to associate with the contacts
     * @param tenantId Tenant ID for data isolation
     * @return Number of contacts successfully parsed and saved
     * @throws IOException if file cannot be read
     */
    Integer parseExcelFile(MultipartFile file, Long clientId, Long tenantId) throws IOException;

    /**
     * Get all company contacts for a specific client and tenant
     * 
     * @param clientId Client ID
     * @param tenantId Tenant ID for data isolation
     * @return List of company contacts
     */
    List<Persona> getAllCompanyContacts(Long clientId, Long tenantId);

    /**
     * Delete a company contact by ID (tenant-scoped)
     * 
     * @param id       Company contact ID
     * @param clientId Client ID
     * @param tenantId Tenant ID for data isolation
     * @return true if deleted successfully, false otherwise
     */
    Boolean deleteCompanyContactById(Long id, Long clientId, Long tenantId);

    /**
     * Update a company contact (tenant-scoped)
     * 
     * @param companyContact Updated company contact data
     * @return true if updated successfully, false otherwise
     */
    Boolean updateCompanyContactById(Persona companyContact);

    /**
     * Search company contacts by company name (tenant-scoped)
     * 
     * @param company  Company name (partial match supported)
     * @param clientId Client ID
     * @param tenantId Tenant ID for data isolation
     * @return List of matching company contacts
     */
    List<Persona> searchByCompany(String company, Long clientId, Long tenantId);

    /**
     * Search company contacts by person name (tenant-scoped)
     * 
     * @param name     Person name (partial match supported)
     * @param clientId Client ID
     * @param tenantId Tenant ID for data isolation
     * @return List of matching company contacts
     */
    List<Persona> searchByName(String name, Long clientId, Long tenantId);

    Boolean deleteMultiplePersonaByIds(List<Long> ids, Long clientId, Long tenantId);
}