package com.crud.tasks.service;

import com.crud.tasks.domain.Task;
import com.crud.tasks.exception.TaskNotFound;
import com.crud.tasks.exception.TrelloNotFound;
import com.crud.tasks.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final DbService _dbService;
    private final TrelloService _trelloService;
    private final TaskRepository _taskRepository;
    private static final String KODILLA_BOARD = "Kodilla Aplication";

    @Autowired
    public TaskService(DbService dbService, TrelloService trelloService, TaskRepository taskRepository) {
        this._dbService = dbService;
        this._trelloService = trelloService;
        this._taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return _taskRepository.findAll();
    }

    public long countTasks() {
        return _taskRepository.count();
    }

    public boolean checkIfTaskIsOnTrello(Long id) {
        var allCardsFromBoard = _trelloService.getAllCardsFromBoard(KODILLA_BOARD).orElseThrow(()-> new TrelloNotFound(TrelloNotFound.BOARD_NF));
        var task = _dbService.getTaskById(id).orElseThrow(()-> new TaskNotFound(TaskNotFound.TASK_NF));

        return allCardsFromBoard.stream()
                .anyMatch(trelloCardDto -> trelloCardDto.getName().equals(task.getTitle()));
    }
}
