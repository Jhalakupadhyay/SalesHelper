package com.a2y.salesHelper.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ExcelParserService {

    /**
     * Parse Excel file from MultipartFile and save participants to database
     * @param file MultipartFile containing Excel data
     * @return number of participants processed
     * @throws IOException if file processing fails
     */
    int parseExcelFile(MultipartFile file) throws IOException;
}