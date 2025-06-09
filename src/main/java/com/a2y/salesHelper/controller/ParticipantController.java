package com.a2y.salesHelper.controller;


import com.a2y.salesHelper.pojo.Participant;
import com.a2y.salesHelper.service.interfaces.ExcelParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
@Tag(name = "Excel Parser", description = "All the API related to Excel parsing")
public class ParticipantController {

    @Autowired
    private ExcelParserService excelParserService;

    @PostMapping("/upload")
    @Operation(
            summary = "Upload the Excel Sheets",
            description = "Accepts Multipart file and Parses it to save data in DB"
    )
    public ResponseEntity<String> uploadExcelFile(MultipartFile file) {
        try {
            int processedCount = excelParserService.parseExcelFile(file);
            return new ResponseEntity<>("Successfully processed " + processedCount + " participants.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to process the file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Gives all The participant stored in the DB",
            description = "Returns all the Participants stored in DB"
    )
    @GetMapping()
    public ResponseEntity<List<Participant>> getAllParticipants(){

        List<Participant> response = excelParserService.getAllParticipant();
        if(response.isEmpty()){
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
