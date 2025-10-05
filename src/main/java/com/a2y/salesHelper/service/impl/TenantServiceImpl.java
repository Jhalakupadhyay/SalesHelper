package com.a2y.salesHelper.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2y.salesHelper.db.entity.TenantEntity;
import com.a2y.salesHelper.db.repository.TenantRepository;
import com.a2y.salesHelper.enums.SubscriptionPlan;
import com.a2y.salesHelper.pojo.Tenant;
import com.a2y.salesHelper.service.interfaces.TenantService;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public Tenant createTenant(Tenant tenant) {
        // Set default subscription plan to FREE if not provided
        SubscriptionPlan subscriptionPlan = tenant.getSubscriptionPlan() != null
                ? tenant.getSubscriptionPlan()
                : SubscriptionPlan.FREE;

        TenantEntity entity = TenantEntity.builder()
                .tenantName(tenant.getTenantName())
                .subscriptionPlan(subscriptionPlan)
                .build();

        TenantEntity savedEntity = tenantRepository.save(entity);
        return convertToPojo(savedEntity);
    }

    @Override
    public Tenant getTenantById(Long tenantId) {
        TenantEntity entity = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));
        return convertToPojo(entity);
    }

    @Override
    public Tenant getTenantByName(String tenantName) {
        TenantEntity entity = tenantRepository.findByTenantName(tenantName)
                .orElseThrow(() -> new RuntimeException("Tenant not found with name: " + tenantName));
        return convertToPojo(entity);
    }

    @Override
    public List<Tenant> getAllTenants() {
        List<TenantEntity> entities = tenantRepository.findAll();
        return entities.stream()
                .map(this::convertToPojo)
                .collect(Collectors.toList());
    }

    @Override
    public Tenant updateTenant(Long tenantId, Tenant tenant) {
        TenantEntity entity = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));

        entity.setTenantName(tenant.getTenantName());

        // Update subscription plan if provided, otherwise keep existing
        if (tenant.getSubscriptionPlan() != null) {
            entity.setSubscriptionPlan(tenant.getSubscriptionPlan());
        }

        TenantEntity updatedEntity = tenantRepository.save(entity);
        return convertToPojo(updatedEntity);
    }

    @Override
    public void deleteTenant(Long tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new RuntimeException("Tenant not found with id: " + tenantId);
        }
        tenantRepository.deleteById(tenantId);
    }

    @Override
    public boolean tenantExists(Long tenantId) {
        return tenantRepository.existsById(tenantId);
    }

    private Tenant convertToPojo(TenantEntity entity) {
        return Tenant.builder()
                .tenantId(entity.getTenantId())
                .tenantName(entity.getTenantName())
                .subscriptionPlan(entity.getSubscriptionPlan())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
