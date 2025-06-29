package com.a2y.salesHelper.service.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CompaniesService {

    Integer parseExcelFile(MultipartFile file) throws IOException;
}
