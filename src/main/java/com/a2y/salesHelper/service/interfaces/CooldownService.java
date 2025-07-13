package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.ClientPojo;
import org.springframework.stereotype.Service;

@Service
public interface CooldownService {

    Boolean addCooldown(ClientPojo client);
}
