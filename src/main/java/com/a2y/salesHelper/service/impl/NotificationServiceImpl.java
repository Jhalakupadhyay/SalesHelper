package com.a2y.salesHelper.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import com.a2y.salesHelper.db.repository.InteractionHistoryRepository;
import com.a2y.salesHelper.db.repository.NotificationRepository;
import com.a2y.salesHelper.db.repository.ParticipantRepository;
import com.a2y.salesHelper.db.repository.UserRepository;
import com.a2y.salesHelper.pojo.Notification;
import com.a2y.salesHelper.service.interfaces.NotificationService;

import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    private final InteractionHistoryRepository interactionHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(InteractionHistoryRepository interactionHistoryRepository,
            NotificationRepository notificationRepository, ParticipantRepository participantRepository,
            UserRepository userRepository) {
        this.interactionHistoryRepository = interactionHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 30000) // Every day at 9 AM
    public void addNotificationForWeek() {
        // get all the interactions whose cooldown_date is 7 days from now
        List<InteractionHistoryEntity> interactionHistories = interactionHistoryRepository.findAllByCooldownDateIsAfter(
                java.time.OffsetDateTime.now().plusDays(6));

        if (interactionHistories.size() >= 10) {
            // Group interactions by tenant to maintain tenant isolation
            Map<Long, List<InteractionHistoryEntity>> interactionsByTenant = interactionHistories.stream()
                    .collect(java.util.stream.Collectors.groupingBy(InteractionHistoryEntity::getTenantId));

            // Process notifications for each tenant separately
            interactionsByTenant.forEach((tenantId, tenantInteractions) -> {
                List<Long> participantIds = new ArrayList<>();
                for (InteractionHistoryEntity interactionHistory : tenantInteractions) {
                    Long participantId = participantRepository
                            .findFirstByNameAndDesignationAndOrganizationAndClientIdAndTenantId(
                                    interactionHistory.getParticipantName(), interactionHistory.getDesignation(),
                                    interactionHistory.getOrganization(), interactionHistory.getClientId(),
                                    interactionHistory.getTenantId())
                            .map(participant -> participant.getId()).orElse(null);
                    participantIds.add(participantId != null ? participantId : 0);
                }

                // get all the user IDs for this specific tenant only
                List<Long> userIds = new ArrayList<>();
                userRepository.findByTenantId(tenantId).forEach(user -> userIds.add(user.getId()));

                // check if the notification already exists for the participantIds
                if (!participantIds.isEmpty()
                        && Boolean.FALSE.equals(notificationRepository.existsByParticipantId(participantIds.get(0)))) {
                    // if not, create a new notification for this tenant
                    com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity = com.a2y.salesHelper.db.entity.NotificationEntity
                            .builder()
                            .participantIds(participantIds)
                            .tenantId(tenantId)
                            .userIds(userIds)
                            .type("WEEKLY")
                            .build();
                    notificationRepository.save(notificationEntity);
                    log.info("Created weekly notification for tenant {} with {} participants", tenantId,
                            participantIds.size());
                }
            });
        }

    }

    @Scheduled(fixedRate = 30000) // every minute
    public void addNotificationAtCooldownDate() {
        log.info("Checking for interactions with cooldown date today");
        // get all the interactions whose cooldown_date is today
        List<InteractionHistoryEntity> interactionHistories = interactionHistoryRepository
                .findAllByCooldownDateIsBefore(
                        java.time.OffsetDateTime.now().plusDays(-1));

        log.info("Found {} interactions with cooldown date today", interactionHistories.size());

        if (interactionHistories.size() >= 10) {
            // Group interactions by tenant to maintain tenant isolation
            Map<Long, List<InteractionHistoryEntity>> interactionsByTenant = interactionHistories.stream()
                    .collect(java.util.stream.Collectors.groupingBy(InteractionHistoryEntity::getTenantId));

            // Process notifications for each tenant separately
            interactionsByTenant.forEach((tenantId, tenantInteractions) -> {
                List<Long> participantIds = new ArrayList<>();
                for (InteractionHistoryEntity interactionHistory : tenantInteractions) {
                    Long participantId = participantRepository
                            .findFirstByNameAndDesignationAndOrganizationAndClientIdAndTenantId(
                                    interactionHistory.getParticipantName(), interactionHistory.getDesignation(),
                                    interactionHistory.getOrganization(), interactionHistory.getClientId(),
                                    interactionHistory.getTenantId())
                            .map(participant -> participant.getId()).orElse(null);
                    participantIds.add(participantId != null ? participantId : 0);
                }

                log.info("Found {} participants for notifications in tenant {}", participantIds.size(), tenantId);

                // get all the user IDs for this specific tenant only
                List<Long> userIds = new ArrayList<>();
                userRepository.findByTenantId(tenantId).forEach(user -> userIds.add(user.getId()));

                log.info("Found {} users for notifications in tenant {}", userIds.size(), tenantId);

                // check if the notification already exists for the participantIds
                if (!participantIds.isEmpty()
                        && Boolean.FALSE.equals(notificationRepository.existsByParticipantId(participantIds.get(0)))) {
                    // if not, create a new notification for this tenant
                    com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity = com.a2y.salesHelper.db.entity.NotificationEntity
                            .builder()
                            .participantIds(participantIds)
                            .tenantId(tenantId)
                            .userIds(userIds)
                            .type("DAILY")
                            .build();
                    notificationRepository.save(notificationEntity);
                    log.info("Created daily notification for tenant {} with {} participants", tenantId,
                            participantIds.size());
                }
            });
        }
    }

    @Override
    public Boolean addSeenByUserId(Long userId, Long notificationId, Long tenantId) {
        try {
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                // Validate tenant access
                if (notification.getTenantId() != null && !notification.getTenantId().equals(tenantId)) {
                    log.warn("User {} from tenant {} attempted to access notification {} from tenant {}",
                            userId, tenantId, notificationId, notification.getTenantId());
                    return;
                }

                List<Long> userIds = notification.getUserIds();
                if (userIds.contains(userId)) {
                    userIds.remove(userId);
                    notification.setUserIds(userIds);
                    notificationRepository.save(notification);
                    log.info("User {} marked notification {} as seen in tenant {}", userId, notificationId, tenantId);
                }
            });
            return true;
        } catch (Exception e) {
            log.error("Error marking notification as seen for user {} and notification {} in tenant {}", userId,
                    notificationId, tenantId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Notification> getNotificationsForUserId(Long userId, Long tenantId) {

        List<Notification> notifications = new ArrayList<>();
        try {
            // Fetch only notifications for this specific tenant
            List<com.a2y.salesHelper.db.entity.NotificationEntity> notificationEntities = notificationRepository
                    .findByTenantId(tenantId);
            for (com.a2y.salesHelper.db.entity.NotificationEntity notificationEntity : notificationEntities) {
                if (notificationEntity.getUserIds().contains(userId)) {
                    Notification notification = Notification.builder()
                            .notificationId(notificationEntity.getId())
                            .participantIds(notificationEntity.getParticipantIds())
                            .type(notificationEntity.getType())
                            .build();
                    notifications.add(notification);
                }
            }
            log.info("Found {} notifications for user {} in tenant {}", notifications.size(), userId, tenantId);
            return notifications;
        } catch (Exception e) {
            log.error("Error getting notifications for user {} in tenant {}", userId, tenantId, e);
            return notifications;
        }

    }

}
