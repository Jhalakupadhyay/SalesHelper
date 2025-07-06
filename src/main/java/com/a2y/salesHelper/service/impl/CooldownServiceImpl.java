package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.db.entity.CooldownEntity;
import com.a2y.salesHelper.db.repository.CompaniesRepository;
import com.a2y.salesHelper.db.repository.CooldownRepository;
import com.a2y.salesHelper.pojo.Cooldown;
import com.a2y.salesHelper.service.interfaces.CooldownService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;


@Service
@EnableScheduling
public class CooldownServiceImpl implements CooldownService {

    private final CooldownRepository cooldownRepository;
    private final CompaniesRepository companiesRepository;

    public CooldownServiceImpl(CooldownRepository cooldownRepository, CompaniesRepository companiesRepository) {
        this.cooldownRepository = cooldownRepository;
        this.companiesRepository = companiesRepository;
    }

    @Override
    public Boolean addCooldown(Cooldown cooldown) {
        try{
            cooldownRepository.save(CooldownEntity.builder()
                    .orgId(cooldown.getOrgId())
                    .cooldownPeriod1(cooldown.getCooldownPeriod1())
                    .cooldownPeriod2(cooldown.getCooldownPeriod2())
                    .cooldownPeriod3(cooldown.getCooldownPeriod3())
                    .build());
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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
