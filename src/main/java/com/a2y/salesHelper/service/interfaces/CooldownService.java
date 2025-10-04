package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.ClientPojo;
import com.a2y.salesHelper.pojo.ClientResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public interface CooldownService {

    Boolean addCooldown(ClientPojo client);
    List<ClientResponse> getClients(Long tenantId);
    ClientResponse editCooldownPeriods(Long clientId, Long tenantId,Long cooldownPeriod1, Long cooldownPeriod2, Long cooldownPeriod3);
}
