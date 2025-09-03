package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.db.repository.NotificationRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.db.repository.UserRepository;
import com.a2y.salesHelper.pojo.Notification;
import com.a2y.salesHelper.service.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    private final InteractionHistoryRepository interactionHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(InteractionHistoryRepository interactionHistoryRepository, NotificationRepository notificationRepository, ParticipantRepository participantRepository, UserRepository userRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }


    @Scheduled(fixedRate = 30000)// Every day at 9 AM
    public void addNotificationForWeek()
    {
        //get all the interactions whose cooldown_date is 7 days from now
        List<InteractionHistoryEntity> interactionHistories = interactionHistoryRepository.findAllByCooldownDateIsAfter(
                java.time.OffsetDateTime.now().plusDays(6)
        );

        if(interactionHistories.size() >= 10)
        {
            List<Long> participantIds = new ArrayList<>();
            for(InteractionHistoryEntity interactionHistory : interactionHistories)
            {
                Long participantId = participantRepository.findByNameAndDesignationAndOrganizationAndClientId(
                        interactionHistory.getParticipantName(),interactionHistory.getDesignation(),interactionHistory.getOrganization(),interactionHistory.getClientId()
                ).map(participant -> participant.getId()).orElse(null);
                participantIds.add(participantId != null ? participantId : 0);
            }

            //get all the user IDs from userRepository
            List<Long> userIds = new ArrayList<>();
            userRepository.findAll().forEach(user -> userIds.add(user.getId()));

            //check if the notification already exists for the participantIds
            if(Boolean.FALSE.equals(notificationRepository.existsByParticipantId(participantIds.get(0)))) {
                //if not, create a new notification
                com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity = com.a2y.salesHelper.db.entity.NotificationEntity.builder()
                        .participantIds(participantIds)
                        .userIds(userIds)
                        .type("WEEKLY")
                        .build();
                notificationRepository.save(notificationEntity);
            }
        }

    }

    @Scheduled(fixedRate = 30000) // every minute
    public void addNotificationAtCooldownDate()
    {
        log.error("Checking for interactions with cooldown date today...");
        //get all the interactions whose cooldown_date is today
        List<InteractionHistoryEntity> interactionHistories = interactionHistoryRepository.findAllByCooldownDateIsBefore(
                java.time.OffsetDateTime.now().plusDays(-1)
        );

        log.info("Found {} interactions with cooldown date today.", interactionHistories.size());

        if(interactionHistories.size() >= 10)
        {
            List<Long> participantIds = new ArrayList<>();
            for(InteractionHistoryEntity interactionHistory : interactionHistories)
            {
                Long participantId = participantRepository.findByNameAndDesignationAndOrganizationAndClientId(
                        interactionHistory.getParticipantName(),interactionHistory.getDesignation(),interactionHistory.getOrganization(),interactionHistory.getClientId()
                ).map(participant -> participant.getId()).orElse(null);
                participantIds.add(participantId != null ? participantId : 0);
            }

            log.info("Participant IDs for notifications: {}", participantIds);

            //get all the user IDs from userRepository
            List<Long> userIds = new ArrayList<>();
            userRepository.findAll().forEach(user -> userIds.add(user.getId()));

            log.info("User IDs for notifications: {}", userIds);

            //check if the notification already exists for the participantIds
            if(Boolean.FALSE.equals(notificationRepository.existsByParticipantId(participantIds.get(0)))) {
                //if not, create a new notification
                com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity = com.a2y.salesHelper.db.entity.NotificationEntity.builder()
                        .participantIds(participantIds)
                        .userIds(userIds)
                        .type("DAILY")
                        .build();
                notificationRepository.save(notificationEntity);
            }
        }
    }

    @Override
    public Boolean addSeenByUserId(Long userId, Long notificationId) {
        try{
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                List<Long> userIds = notification.getUserIds();
                if(userIds.contains(userId)) {
                    userIds.remove(userId);
                    notification.setUserIds(userIds);
                    notificationRepository.save(notification);
                }
            });
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Notification> getNotificationsForUserId(Long userId) {

        List<Notification> notifications = new ArrayList<>();
        try{
            List<com.a2y.salesHelper.db.entity.NotificationEntity> notificationEntities = notificationRepository.findAll();
            for(com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity : notificationEntities)
            {
                if(notificationEntity.getUserIds().contains(userId))
                {
                    Notification notification = Notification.builder()
                            .notificationId(notificationEntity.getId())
                            .participantIds(notificationEntity.getParticipantIds())
                            .type(notificationEntity.getType())
                            .build();
                    notifications.add(notification);
                }
            }
            return notifications;
        } catch (Exception e) {
            return notifications;
        }

    }


}
