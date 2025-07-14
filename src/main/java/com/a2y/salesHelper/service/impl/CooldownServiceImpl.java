package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.ClientEntity;
import com.a2y.salesHelper.db.repository.ClientRepository;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.pojo.ClientPojo;
import com.a2y.salesHelper.pojo.ClientResponse;
import com.a2y.salesHelper.service.interfaces.CooldownService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@EnableScheduling
public class CooldownServiceImpl implements CooldownService {

    private final ClientRepository clientRepository;
    private final CompaniesRepository companiesRepository;

    public CooldownServiceImpl(ClientRepository cooldownRepository, CompaniesRepository companiesRepository) {
        this.clientRepository = cooldownRepository;
        this.companiesRepository = companiesRepository;
    }

    @Override
    public Boolean addCooldown(ClientPojo client) {
        try{
            clientRepository.save(ClientEntity.builder()
                    .orgName(client.getOrgName())
                    .cooldownPeriod1(client.getCooldownPeriod1())
                    .cooldownPeriod2(client.getCooldownPeriod2())
                    .cooldownPeriod3(client.getCooldownPeriod3())
                    .build());
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientResponse> getClients() {
        return clientRepository.findAll().stream()
                .map(clientEntity -> ClientResponse.builder()
                        .clientId(clientEntity.getOrgId())
                        .orgName(clientEntity.getOrgName())
                        .cooldownPeriod1(clientEntity.getCooldownPeriod1())
                        .cooldownPeriod2(clientEntity.getCooldownPeriod2())
                        .cooldownPeriod3(clientEntity.getCooldownPeriod3())
                        .build())
                .toList();
    }

//    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
//    public void mailCooldownWhenLessThan7Days() {
//        cooldownRepository.findAll().forEach(cooldownEntity -> {
//            CompanyEntity company = companiesRepository.findById(cooldownEntity.getOrgId()).orElse(null);
//            if (company != null && company.getEmail() != null) {
//                //get all the three cooldown periods
//                //one that is less than 7 days from now
//                if (cooldownEntity.getCooldownPeriod1() != null &&
//                    cooldownEntity.getCooldownPeriod1().isBefore(OffsetDateTime.now().plusDays(7))) {
//                    // TODO: SEND THE MAIL TO THE A2Y TEAM
//                }
//            }
//        });
//    }

}
