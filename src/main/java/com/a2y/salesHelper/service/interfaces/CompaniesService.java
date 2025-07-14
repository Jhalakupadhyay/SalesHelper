package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.Companies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CompaniesService {

    Integer parseExcelFile(MultipartFile file, Long clientId) throws IOException;

    List<Companies> getAllCompanies(Long clientId);

    Companies getCompanyById(Long id,Long clientId);

    List<Companies> filterCompanies(String field, String value,Long clientId);

    Companies updateCompanyById(Companies company);

    Boolean deleteCompanyById(Long id);
}
