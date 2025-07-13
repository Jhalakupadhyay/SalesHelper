package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.pojo.EditRequest;
import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InteractionHistoryImpl implements InteractionHistoryService {

    private final InteractionHistoryRepository interactionHistoryRepository;
    private final ParticipantRepository participantRepository;

    public InteractionHistoryImpl(InteractionHistoryRepository interactionHistoryRepository, ParticipantRepository participantRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public boolean editInteractionHistory(EditRequest editRequest) {
        InteractionHistoryEntity existingInteraction = interactionHistoryRepository
                .findByParticipantNameAndCreatedAtAndClientId(editRequest.getParticipantName(), editRequest.getCreatedAt(), editRequest.getClientId());
        if (existingInteraction == null) {
            return false;
        }


        InteractionHistoryEntity interactionHistoryEntity = InteractionHistoryEntity.builder()
                .participantName(editRequest.getParticipantName())
                .clientId(existingInteraction.getClientId())
                .organization(existingInteraction.getOrganization())
                .designation(existingInteraction.getDesignation())
                .eventName(existingInteraction.getEventName())
                .eventDate(OffsetDateTime.now())
                .description(editRequest.getDescription())
                .createdAt(editRequest.getCreatedAt())
                .meetingDone(Boolean.TRUE)
                .build();

        interactionHistoryRepository.save(interactionHistoryEntity);
        return true;
    }

    //add interaction history


    @Override
    public List<InteractionHistory> getInteractionHistory(String participantName, String organization,Long clientId) {
        List<InteractionHistoryEntity> interaction = interactionHistoryRepository.findByParticipantNameAndOrganizationAndClientId(participantName, organization);

        if(interaction == null) {
            return new ArrayList<>();
        }
        List<InteractionHistory> interactionHistoryList = new ArrayList<>();
        for (InteractionHistoryEntity entity : interaction) {
            InteractionHistory interactionHistory = InteractionHistory.builder()
                    .participantName(entity.getParticipantName())
                    .clientId(entity.getClientId()) // Assuming clientId is part of the interaction
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
                .clientId(interactionHistory.getClientId()) // Client ID is provided in the method signature
                .organization(interactionHistory.getOrganization())
                .eventName(interactionHistory.getEventName())
                .designation(interactionHistory.getDesignation()) // Designation is provided in the method signature
                .eventDate(interactionHistory.getEventDate()) // Use provided date or current time
                .description(interactionHistory.getDescription())
                .meetingDone(Boolean.TRUE)// Default value, can be changed based on requirements
                .build();

        //also update the eventDate in participant table
        participantRepository.findByNameAndDesignationAndOrganizationAndClientId(interactionHistory.getParticipantName(),
                interactionHistory.getDesignation(), interactionHistory.getOrganization(), interactionHistory.getClientId())
                .ifPresent(participant -> {
                    participant.setEventDate(interactionHistory.getEventDate());
                    participantRepository.save(participant);
                });

        interactionHistoryRepository.save(interactionHistoryEntity);
        return true;
    }
}
