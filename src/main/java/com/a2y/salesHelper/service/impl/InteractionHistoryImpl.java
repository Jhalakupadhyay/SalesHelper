package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.pojo.EditRequest;
import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InteractionHistoryImpl implements InteractionHistoryService {

    private final InteractionHistoryRepository interactionHistoryRepository;

    public InteractionHistoryImpl(InteractionHistoryRepository interactionHistoryRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
    }

    @Override
    public boolean editInteractionHistory(EditRequest editRequest) {
        InteractionHistoryEntity existingInteraction = interactionHistoryRepository
                .findByParticipantNameAndCreatedAt(editRequest.getParticipantName(), editRequest.getCreatedAt());
        if (existingInteraction == null) {
            return false;
        }


        InteractionHistoryEntity interactionHistoryEntity = InteractionHistoryEntity.builder()
                .participantName(existingInteraction.getParticipantName())
                .organization(existingInteraction.getOrganization()) // Assuming organization is part of the existing interaction
                .designation(existingInteraction.getDesignation()) // Assuming designation is part of the existing interaction
                .eventName(existingInteraction.getEventName()) // Assuming eventName is part of the existing interaction
                .eventDate(OffsetDateTime.now())
                .description(editRequest.getDescription())
                .meetingDone(Boolean.TRUE)
                .build();

        interactionHistoryRepository.save(interactionHistoryEntity);
        return true;
    }

    //add interaction history


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
                    .createdAt(entity.getCreatedAt()) // Use existing createdAt or current time
                    .build();
            interactionHistoryList.add(interactionHistory);
        }
        return interactionHistoryList;
    }

    @Override
    public boolean addInteractionHistory(InteractionHistory interactionHistory) {
        InteractionHistoryEntity interactionHistoryEntity = InteractionHistoryEntity.builder()
                .participantName(interactionHistory.getParticipantName())
                .organization(interactionHistory.getOrganization())
                .eventName(interactionHistory.getEventName())
                .designation(interactionHistory.getDesignation()) // Designation is provided in the method signature
                .eventDate(interactionHistory.getEventDate()) // Use provided date or current time
                .description(interactionHistory.getDescription())
                .meetingDone(Boolean.TRUE)// Default value, can be changed based on requirements
                .build();

        interactionHistoryRepository.save(interactionHistoryEntity);
        return true;
    }
}
