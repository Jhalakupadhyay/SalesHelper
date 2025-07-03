package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.Companies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CompaniesService {

    Integer parseExcelFile(MultipartFile file) throws IOException;

    List<Companies> getAllCompanies();

    List<Companies> searchCompanies(String searchQuery);

    Companies getCompanyById(Long id);

    List<Companies> filterCompanies(String field, String value);
}
