package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.db.entity.ParticipantEntity;
import com.a2y.salesHelper.pojo.Participant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface ParticipantService {

    /**
     * Parse Excel file from MultipartFile and save participants to database
     * @param file MultipartFile containing Excel data
     * @return number of participants processed
     * @throws IOException if file processing fails
     */
    Integer parseExcelFile(MultipartFile file,Long clientId) throws IOException;

    /**
     * get all the participants from the DB
     * @return
     * List of all participants
     */
    List<Participant> getAllParticipant(Long clientId);

    /**
     * Get a map of participant IDs to their names
     * @return List<participant> containing participant details
     */
    Boolean deleteParticipantById(Long id);

    /**
     * Get a map of participant IDs to their names
     * @return List<participant> containing participant details
     */
    Boolean updateParticipantById(Participant participant);

    List<Participant> filterParticipants(String field, String value,Long clientId);

    List<Participant> searchParticipant(String name,Long clientId);

    List<ParticipantEntity> getParticipantsForOrganization(Long orgId, Long clientId);
}