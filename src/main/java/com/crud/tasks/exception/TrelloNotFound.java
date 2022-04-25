package com.crud.tasks.exception;

public class TrelloNotFound extends IllegalStateException {
    public static final String BOARD_NF = "Couldn't find any trello board by name:%s, name";
    public static final String CARD_NF = "Couldn't find any trello board by name:%s, name";
    public static final String LIST_NF = "Couldnt' find any trello list by id:$s, id";

    public TrelloNotFound(String message) {
        super(message);
    }
}
