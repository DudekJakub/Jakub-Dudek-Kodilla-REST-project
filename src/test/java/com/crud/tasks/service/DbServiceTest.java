package com.crud.tasks.service;

import com.crud.tasks.domain.Task;
import com.crud.tasks.exception.TaskNotFound;
import com.crud.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DbServiceTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    void getAllTasks() {
        //Given & When
        var tasks = taskRepository.findAll();

        //Then
        assertEquals(5, tasks.size());
    }

    @Test
    void getTaskById() {
        //Given
        long taskId = 1;
        String taskTitle = "firstTest";

        //When
        var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFound(TaskNotFound.TASK_NF));

        //Then
        assertEquals(taskTitle, task.getTitle());
    }

    @Test
    void saveTask() {
        //Given
        Task taskToSave = new Task(1L, "DbServiceTest", "Test_Content");

        //When
        taskRepository.save(taskToSave);

        //Then
        Task taskDao = taskRepository.findAll()
                .stream()
                .filter(t -> t.getTitle().equals(taskToSave.getTitle()))
                .findFirst()
                .orElseThrow(() -> new TaskNotFound(TaskNotFound.TASK_NF));

        assertEquals(taskToSave.getTitle(), taskDao.getTitle());

        //CleanUp
            //@Transactional
    }

    @Test
    void deleteTask() {
        //Given
        long taskIdToDelete = 1L;
        long tasksCountBefore = taskRepository.count(); //5 rows (25.04.2022)

        //When
        taskRepository.deleteById(taskIdToDelete);

        //Then
        long tasksCountAfter = taskRepository.count();

        assertEquals(1, tasksCountBefore - tasksCountAfter);
        assertEquals(4, tasksCountAfter);

        //CleanUp
            //@Transactional
    }
}