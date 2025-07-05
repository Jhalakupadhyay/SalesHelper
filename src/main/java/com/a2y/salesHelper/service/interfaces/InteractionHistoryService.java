package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.InteractionHistory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public interface InteractionHistoryService {

    /**
     * Add interaction history for a participant
     * @param participantId ID of the participant
     * @param interactionDetails Details of the interaction
     * @return true if interaction history was added successfully, false otherwise
     */
    boolean addInteractionHistory(Long participantId, OffsetDateTime interactionDate, String interactionDetails);

    /**
     * Get interaction history for a participant
     * @param participantId ID of the participant
     * @return Interaction history details as a String
     */
    List<InteractionHistory> getInteractionHistory(Long participantId);
}
