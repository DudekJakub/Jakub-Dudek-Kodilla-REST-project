package com.crud.tasks.service;

import com.crud.tasks.trello.client.TrelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final DbService _dbService;
    private final TrelloClient _trelloClient;
    private static final String KODILLA_BOARD = "Kodilla Aplication";

    @Autowired
    public TaskService(DbService dbService, TrelloClient trelloClient) {
        this._dbService = dbService;
        this._trelloClient = trelloClient;
    }

    public boolean checkIfTaskIsOnTrello(Long id) {
        var task = _trelloClient.getAllCardsFromBoard(KODILLA_BOARD).get()
                .stream()
                .filter(trelloCardDto -> trelloCardDto.getName().equals(_dbService.getTaskById(id).get().getTitle()))
                .findAny();
        return task.isPresent();
    }
}
