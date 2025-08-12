package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.Persona;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PersonaService {

    /**
     * Parse Excel file and save company contacts to database
     * @param file Excel file containing company contact data
     * @param clientId Client ID to associate with the contacts
     * @return Number of contacts successfully parsed and saved
     * @throws IOException if file cannot be read
     */
    Integer parseExcelFile(MultipartFile file, Long clientId) throws IOException;

    /**
     * Get all company contacts for a specific client
     * @param clientId Client ID
     * @return List of company contacts
     */
    List<Persona> getAllCompanyContacts(Long clientId);

    /**
     * Delete a company contact by ID
     * @param id Company contact ID
     * @return true if deleted successfully, false otherwise
     */
    Boolean deleteCompanyContactById(Long id);

    /**
     * Update a company contact
     * @param companyContact Updated company contact data
     * @return true if updated successfully, false otherwise
     */
    Boolean updateCompanyContactById(Persona companyContact);

    /**
     * Search company contacts by company name
     * @param company Company name (partial match supported)
     * @param clientId Client ID
     * @return List of matching company contacts
     */
    List<Persona> searchByCompany(String company, Long clientId);

    /**
     * Search company contacts by person name
     * @param name Person name (partial match supported)
     * @param clientId Client ID
     * @return List of matching company contacts
     */
    List<Persona> searchByName(String name, Long clientId);
}