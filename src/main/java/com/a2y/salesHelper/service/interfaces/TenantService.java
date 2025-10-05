package com.a2y.salesHelper.service.interfaces;

import java.util.List;

import com.a2y.salesHelper.pojo.Tenant;

public interface TenantService {

    Tenant createTenant(Tenant tenant);

    Tenant getTenantById(Long tenantId);

    Tenant getTenantByName(String tenantName);

    List<Tenant> getAllTenants();

    Tenant updateTenant(Long tenantId, Tenant tenant);

    void deleteTenant(Long tenantId);

    boolean tenantExists(Long tenantId);
}
