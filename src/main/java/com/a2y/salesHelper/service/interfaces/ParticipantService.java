package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.Participant;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ParticipantService {

    /**
     * Parse Excel file from MultipartFile and save participants to database
     * @param file MultipartFile containing Excel data
     * @return number of participants processed
     * @throws IOException if file processing fails
     */
    int parseExcelFile(MultipartFile file) throws IOException;

    /**
     * get all the participants from the DB
     * @return
     * List of all participants
     */
    List<Participant> getAllParticipant();

    /**
     * Get a map of participant IDs to their names
     * @return List<participant> containing participant details
     */
    List<Participant> deleteParticipantById(Long id);

    /**
     * Get a map of participant IDs to their names
     * @return List<participant> containing participant details
     */
    List<Participant> updateParticipantById(Participant participant);
}