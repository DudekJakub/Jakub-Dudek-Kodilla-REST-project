package com.crud.tasks.service;

import com.crud.tasks.domain.Task;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCard;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.repository.TaskRepository;
import com.crud.tasks.trello.client.TrelloClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService _taskService;

    @Mock
    private DbService _dbService;

    @Mock
    private TrelloService _trelloService;

    @Mock
    private TaskRepository _taskRepository;

    @Test
    void getAllTasks() {
        //Given
        List<Task> allTasks = new ArrayList<>(List.of(new Task(1L, "Test_title", "Test_content")));
        when(_taskRepository.findAll()).thenReturn(allTasks);

        //When
        var allTasksList = _taskService.getAllTasks();

        //Then
        assertEquals(1, allTasksList.size());
    }

    @Test
    void countTasks() {
        //Given
        long qntOfTasks = 5L;
        when(_taskRepository.count()).thenReturn(qntOfTasks);

        //When
        long resultOfTasksQnt = _taskService.countTasks();

        //Then
        assertEquals(5, resultOfTasksQnt);
    }

    @Test
    void checkIfTaskIsOnTrello() {
        //Given
        TrelloCardDto card = TrelloCardDto.builder()
                .id("1")
                .name("test_card")
                .desc("test_desc")
                .pos("test_pos")
                .idList("test_idList")
                .build();

        Task task = new Task(1L, "test_card", "test_content");

        when(_trelloService.getAllCardsFromBoard(any())).thenReturn(Optional.of(List.of(card)));
        when(_dbService.getTaskById(1L)).thenReturn(Optional.of(task));

        //When
        var isOnTrello = _taskService.checkIfTaskIsOnTrello(1L);

        //Then
        assertTrue(isOnTrello);
    }
}