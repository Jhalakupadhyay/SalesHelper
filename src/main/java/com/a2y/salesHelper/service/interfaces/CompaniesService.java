package com.a2y.salesHelper.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a2y.salesHelper.pojo.Companies;

@Service
public interface CompaniesService {

    Integer parseExcelFile(MultipartFile file, Long clientId, Long tenantId) throws IOException;

    List<Companies> getAllCompanies(Long clientId, Long tenantId);

    Page<Companies> getAllCompanies(Long clientId, Long tenantId, Pageable pageable);

    Companies getCompanyById(Long id, Long clientId, Long tenantId);

    List<Companies> filterCompanies(String field, String value, Long clientId, Long tenantId);

    Companies updateCompanyById(Companies company);

    Boolean deleteCompanyById(Long id, Long clientId, Long tenantId);
}
