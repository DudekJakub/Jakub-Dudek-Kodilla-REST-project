package com.crud.tasks.controller;

import com.crud.tasks.domain.CreatedTrelloCardDto;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.trello.client.TrelloClient;
import com.crud.tasks.trello.facade.TrelloFacade;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig
@WebMvcTest(TrelloController.class)
class TrelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrelloFacade trelloFacade;

    @MockBean
    private TrelloClient trelloClient;

    @Test
    void shouldFetchEmptyTrelloBoards() throws Exception {
        // Given
        when(trelloFacade.fetchTrelloBoards()).thenReturn(List.of());

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/trello/getTrelloBoards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200)) // or isOk()
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void shouldFetchTrelloBoards() throws Exception {
        //Given
        List<TrelloListDto> trelloListDtos = List.of(new TrelloListDto("1", "myList", false));
        List<TrelloBoardDto> trelloBoardDtos = List.of(new TrelloBoardDto("1", "myBoard", trelloListDtos));

        when(trelloFacade.fetchTrelloBoards()).thenReturn(trelloBoardDtos);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/trello/getTrelloBoards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //TrelloBoard fields
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("myBoard")))
                //TrelloList field
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lists", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lists[0].id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lists[0].name", Matchers.is("myList")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lists[0].closed", Matchers.is(false)));
    }

    @Test
    void shouldCreateTrelloCard() throws Exception {
        //Given
        TrelloCardDto cardToCreate = new TrelloCardDto("myTask", "taskDesc", "bottom", "10");
        CreatedTrelloCardDto createdCard = new CreatedTrelloCardDto("20", "myTask", "http://trello.com/card/20");

        when(trelloFacade.createCard(cardToCreate)).thenReturn(createdCard);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(cardToCreate);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/trello/createTrelloCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("20")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("myTask")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl", Matchers.is("http://trello.com/card/20")));
    }

}