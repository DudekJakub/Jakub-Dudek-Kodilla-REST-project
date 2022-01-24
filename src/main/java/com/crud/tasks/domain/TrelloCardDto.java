package com.crud.tasks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Data
public class TrelloCardDto {

    private String name;
    private String description;
    private String pos;
    private String listId;
}
