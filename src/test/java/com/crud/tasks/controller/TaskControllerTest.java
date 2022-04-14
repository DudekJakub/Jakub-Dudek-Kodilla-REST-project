package com.crud.tasks.controller;

import com.crud.tasks.domain.Task;
import com.crud.tasks.domain.TaskDto;
import com.crud.tasks.mapper.TaskMapper;
import com.crud.tasks.service.DbService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    Gson gson;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DbService dbService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    void shouldRetrieveEmptyListOfTasks() throws Exception {
        //Given
        when(dbService.getAllTasks()).thenReturn(List.of());

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/task/getTasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void shouldRetrieveListOfTasks() throws Exception {
        //Given
        List<TaskDto> taskDtos = List.of(new TaskDto(1L, "test_title", "anything_here"));
        List<Task> tasks = new ArrayList<>();

        when(dbService.getAllTasks()).thenReturn(tasks);
        when(taskMapper.mapToTaskDtoList(tasks)).thenReturn(taskDtos);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/task/getTasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("test_title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content", Matchers.is("anything_here")));
    }

    @Test
    void shouldRetrieveOneTaskById() throws Exception {
        //Given
        TaskDto taskDto = new TaskDto(1L, "test_title", "test_content");
        Task task = new Task(1L, "test_title", "test_content");

        when(dbService.getTaskById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.mapToTaskDto(any(Task.class))).thenReturn(taskDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/task/getTask").param("taskId","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is("test_content")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("test_title")));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        //Given
        TaskDto inTaskDto = new TaskDto(1L, "test_title", "test_content");
        TaskDto outTaskDto = new TaskDto(1L, "test_title", "test_content");
        Task task = new Task(1L, "test_title", "test_content");
        Task savedTask = new Task(1L, "test_title", "test_content");

        when(taskMapper.mapToTask(any(TaskDto.class))).thenReturn(task);
        when(dbService.saveTask(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.mapToTaskDto(any(Task.class))).thenReturn(outTaskDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(inTaskDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/v1/task/updateTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is("test_content")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("test_title")));
    }

    @Test
    void shouldCreateTask() throws Exception {
        //Given
        TaskDto taskDto = new TaskDto(1L, "test_title", "test_content");
        Task task = new Task(1L, "test_title", "test_content");

        given(taskMapper.mapToTask(ArgumentMatchers.any(TaskDto.class))).willReturn(task);
        given(dbService.saveTask(ArgumentMatchers.any(Task.class))).willReturn(task);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(taskDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/task/createTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteTask() throws Exception {
        //Given
        long taskId = 1;

        doNothing().when(dbService).deleteTask(taskId); //when(<mock>).<action>

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/v1/task/deleteTask")
                        .param("taskId", "1"))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }
}