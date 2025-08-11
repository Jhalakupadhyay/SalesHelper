package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.ClientEntity;
import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.ClientRepository;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.pojo.EditRequest;
import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InteractionHistoryImpl implements InteractionHistoryService {

    private final InteractionHistoryRepository interactionHistoryRepository;
    private final ParticipantRepository participantRepository;
    private final ClientRepository clientRepository;

    public InteractionHistoryImpl(InteractionHistoryRepository interactionHistoryRepository, ParticipantRepository participantRepository, ClientRepository clientRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
        this.participantRepository = participantRepository;
        this.clientRepository = clientRepository;
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
        List<InteractionHistoryEntity> interaction = interactionHistoryRepository.findByParticipantNameAndOrganizationAndClientId(participantName, organization,clientId);

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

        ClientEntity client = clientRepository.getReferenceById(interactionHistory.getClientId());

        if (client == null) {
            log.error("Client with ID {} not found", interactionHistory.getClientId());
            return false;
        }

        Long cooldown1 = client.getCooldownPeriod1();
        Long cooldown2 = client.getCooldownPeriod2();
        Long cooldown3 = client.getCooldownPeriod3();

        OffsetDateTime cooldownDate;
        int cooldownCount;

        //get the latest interaction history for the participant
        InteractionHistoryEntity latestInteraction = interactionHistoryRepository
                .findTopByParticipantNameAndOrganizationAndClientIdOrderByCreatedAtDesc(interactionHistory.getParticipantName(), interactionHistory.getOrganization(), interactionHistory.getClientId());

        if (latestInteraction != null) {
            if (latestInteraction.getCooldownCount() == 1) {
                if (interactionHistory.getEventDate().isBefore(latestInteraction.getCooldownDate())) {
                    cooldownDate = interactionHistory.getEventDate().plusDays(cooldown2);
                    cooldownCount = 2;
                } else {
                    cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
                    cooldownCount = 1;
                }
            } else if (latestInteraction.getCooldownCount() == 2) {
                if (interactionHistory.getEventDate().isBefore(latestInteraction.getCooldownDate())) {
                    cooldownDate = interactionHistory.getEventDate().plusDays(cooldown3);
                    cooldownCount = 3;
                } else {
                    cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
                    cooldownCount = 1;
                }
            } else {
                cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
                cooldownCount = 1;
            }
        } else {
            cooldownDate = interactionHistory.getEventDate().plusDays(cooldown1);
            cooldownCount = 1;
        }


        InteractionHistoryEntity interactionHistoryEntity = InteractionHistoryEntity.builder()
                .participantName(interactionHistory.getParticipantName())
                .clientId(interactionHistory.getClientId()) // Client ID is provided in the method signature
                .organization(interactionHistory.getOrganization())
                .eventName(interactionHistory.getEventName())
                .designation(interactionHistory.getDesignation()) // Designation is provided in the method signature
                .eventDate(interactionHistory.getEventDate())
                .cooldownDate(cooldownDate)
                .cooldownCount(cooldownCount)// Use provided date or current time
                .description(interactionHistory.getDescription())
                .meetingDone(Boolean.TRUE)// Default value, can be changed based on requirements
                .build();

        log.info("Adding interaction history for participant: {}", interactionHistoryEntity.toString());

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
