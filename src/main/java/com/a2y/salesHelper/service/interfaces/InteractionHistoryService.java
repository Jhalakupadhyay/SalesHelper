package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.EditRequest;
import com.a2y.salesHelper.pojo.InteractionHistory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface InteractionHistoryService {


    /**
     * Add interaction history for a participant
     * @param editRequest EditRequest object containing participant details, date, and description
     * @return true if added successfully, false otherwise
     */
    boolean editInteractionHistory(EditRequest editRequest);

    /**
     * Get interaction history for a participant
     * @param participantName Name of the participant
     * @param organization Organization of the participant
     * @return List of InteractionHistory objects
     */
    List<InteractionHistory> getInteractionHistory(String participantName, String organization,Long clientId,Long tenantId);

    /**
     * Add interaction history for a participant
     * @return true if added successfully, false otherwise
     */
    boolean addInteractionHistory(InteractionHistory interactionHistory);
}
