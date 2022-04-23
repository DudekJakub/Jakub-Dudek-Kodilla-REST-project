package com.crud.tasks.service;

import com.crud.tasks.domain.Task;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.repository.TaskRepository;
import com.crud.tasks.trello.client.TrelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final DbService _dbService;
    private final TrelloClient _trelloClient;
    private final TaskRepository _taskRepository;
    private static final String KODILLA_BOARD = "Kodilla Aplication";

    @Autowired
    public TaskService(DbService dbService, TrelloClient trelloClient, TaskRepository taskRepository) {
        this._dbService = dbService;
        this._trelloClient = trelloClient;
        this._taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return _taskRepository.findAll();
    }

    public long countTasks() {
        return _taskRepository.count();
    }

    public boolean checkIfTaskIsOnTrello(Long id) {
        var task = _trelloClient.getAllCardsFromBoard(KODILLA_BOARD).orElse(new ArrayList<>())
                .stream()
                .filter(trelloCardDto -> trelloCardDto.getName().equals(_dbService.getTaskById(id)
                        .orElseThrow()
                        .getTitle()))
                .findAny();
        return task.isPresent();
    }
}
