package com.crud.tasks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class TrelloList {
    private String id;
    private String name;
    private boolean isClosed;
    private List<TrelloCard> cardList;

    @Override
    public String toString() {
        return "TrelloList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isClosed=" + isClosed +
                ", cardList=" + cardList +
                '}';
    }
}
