package com.crud.tasks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class TrelloCard {

    private String id;
    private String name;
    private String desc;
    private String pos;
    private String idList;

    @Override
    public String toString() {
        return "TrelloCard{" +
                "name='" + name + '\'' +
                ", description='" + desc + '\'' +
                ", pos='" + pos + '\'' +
                ", listId='" + idList + '\'' +
                '}';
    }
}
