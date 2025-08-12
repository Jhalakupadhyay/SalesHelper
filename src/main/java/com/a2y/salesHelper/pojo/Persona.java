package com.a2y.salesHelper.pojo;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Persona {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("clientId")
    private Long clientId;

    @JsonProperty("company")
    private String company;

    @JsonProperty("name")
    private String name;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("sheetName")
    private String sheetName;
}
