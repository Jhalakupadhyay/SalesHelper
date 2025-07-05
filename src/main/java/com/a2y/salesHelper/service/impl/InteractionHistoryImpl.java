package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InteractionHistoryImpl implements InteractionHistoryService {

    private final InteractionHistoryRepository interactionHistoryRepository;

    public InteractionHistoryImpl(InteractionHistoryRepository interactionHistoryRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
    }

    @Override
    public boolean addInteractionHistory(String participantName, String eventName, String organization, String interactionDetails) {
        InteractionHistoryEntity existingInteraction = interactionHistoryRepository.findByParticipantNameAndEventNameAndOrganization(
                participantName, // Designation is not provided in the method signature, assuming it can be null
                eventName,
                organization
        );

        if (existingInteraction == null) {
            return false;
        }

        InteractionHistoryEntity interactionHistoryEntity = InteractionHistoryEntity.builder()
                .participantName(participantName)
                .organization(organization)
                .designation(existingInteraction.getDesignation()) // Assuming designation is part of the existing interaction
                .eventName(eventName)
                .eventDate(OffsetDateTime.now())
                .description(interactionDetails)
                .meetingDone(false) // Default value, can be changed based on requirements
                .build();

        interactionHistoryRepository.save(interactionHistoryEntity);
        return true;
    }

    @Override
    public List<InteractionHistory> getInteractionHistory(String participantName, String organization) {
        List<InteractionHistoryEntity> interaction = interactionHistoryRepository.findByParticipantNameAndOrganization(participantName, organization);

        if(interaction == null) {
            return new ArrayList<>();
        }
        List<InteractionHistory> interactionHistoryList = new ArrayList<>();
        for (InteractionHistoryEntity entity : interaction) {
            InteractionHistory interactionHistory = InteractionHistory.builder()
                    .participantName(entity.getParticipantName())
                    .organization(entity.getOrganization())
                    .designation(entity.getDesignation())
                    .eventName(entity.getEventName())
                    .eventDate(entity.getEventDate())
                    .description(entity.getDescription())
                    .meetingDone(entity.getMeetingDone())
                    .build();
            interactionHistoryList.add(interactionHistory);
        }
        return interactionHistoryList;
    }
}
