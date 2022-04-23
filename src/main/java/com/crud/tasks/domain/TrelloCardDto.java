package com.crud.tasks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloCardDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("desc")
    private String description;

    @JsonProperty("pos")
    private String pos;

    @JsonProperty("idList")
    private String listId;
}
