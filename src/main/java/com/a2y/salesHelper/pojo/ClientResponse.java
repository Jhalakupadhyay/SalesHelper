package com.a2y.salesHelper.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {

    private Long clientId;
    private String orgName;
    private Long cooldownPeriod1;
    private Long cooldownPeriod2;
    private Long cooldownPeriod3;
}
