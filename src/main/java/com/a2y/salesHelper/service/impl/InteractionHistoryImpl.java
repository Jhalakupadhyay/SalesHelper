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
    public boolean addInteractionHistory(Long participantId, OffsetDateTime intearctinDate, String interactionDetails) {
        InteractionHistoryEntity interaction = interactionHistoryRepository.findByParticipantIdAndEventDate(participantId,intearctinDate);
        //event Date will always be there, so we can use it to check if interaction already exists
        if(interaction == null){
            return false;
        }

        interaction.setDescription(interactionDetails);
        interactionHistoryRepository.save(interaction);
        return true;
    }

    @Override
    public List<InteractionHistory> getInteractionHistory(Long participantId) {
        List<InteractionHistoryEntity> interaction = interactionHistoryRepository.findByParticipantId(participantId);

        if(interaction == null) {
            return new ArrayList<>();
        }
        List<InteractionHistory> interactionHistoryList = new ArrayList<>();
        for (InteractionHistoryEntity entity : interaction) {
            InteractionHistory interactionHistory = InteractionHistory.builder()
                    .participantId(entity.getParticipantId())
                    .eventName(entity.getEventName())
                    .eventDate(entity.getEventDate())
                    .description(entity.getDescription())
                    .build();
            interactionHistoryList.add(interactionHistory);
        }
        return interactionHistoryList;
    }
}
