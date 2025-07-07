package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.InteractionHistory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public interface InteractionHistoryService {


    /**
     * Add interaction history for a participant
     * @param participantName ID of the participant
     * @param eventName Date of the interaction
     * @param interactionDetails Details of the interaction
     * @return true if added successfully, false otherwise
     */
    boolean editInteractionHistory(String participantName, String eventName, String organization, String interactionDetails);

    /**
     * Get interaction history for a participant
     * @param participantName Name of the participant
     * @param organization Organization of the participant
     * @return List of InteractionHistory objects
     */
    List<InteractionHistory> getInteractionHistory(String participantName, String organization);

    /**
     * Add interaction history for a participant
     * @return true if added successfully, false otherwise
     */
    boolean addInteractionHistory(InteractionHistory interactionHistory);
}
