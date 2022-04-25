package com.crud.tasks.controller;

import com.crud.tasks.domain.CreatedTrelloCardDto;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.exception.TrelloNotFound;
import com.crud.tasks.service.TrelloService;
import com.crud.tasks.trello.client.TrelloClient;
import com.crud.tasks.trello.facade.TrelloFacade;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.MvcNamespaceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
@WebMvcTest(TrelloController.class)
class TrelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrelloFacade trelloFacade;

    @MockBean
    private TrelloClient trelloClient;

    @MockBean
    private TrelloService trelloService;

    private static final String TRELLO_URL = "/v1/trello/";
    private static final String TRELLO_BOARD = "TrelloBoard";
    private static final String TRELLO_LIST = "TrelloList";
    private static final String TRELLO_CARD = "TrelloCard";

    @Test
    void shouldFetchEmptyTrelloBoards() throws Exception {
        // Given
        when(trelloFacade.fetchTrelloBoards()).thenReturn(List.of());

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "boards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200)) // or isOk()
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void shouldFetchTrelloBoards() throws Exception {
        //Given
        List<TrelloListDto> trelloListDtos = List.of((TrelloListDto) objectFactoryForTest(TRELLO_LIST));
        List<TrelloBoardDto> trelloBoardDtos = List.of((TrelloBoardDto) objectFactoryForTest(TRELLO_BOARD));
        trelloBoardDtos.get(0).setLists(trelloListDtos);

        when(trelloFacade.fetchTrelloBoards()).thenReturn(trelloBoardDtos);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "boards")
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
    void shouldReturnTrelloList() throws Exception {
        //Given
        TrelloListDto trelloListDto = (TrelloListDto) objectFactoryForTest(TRELLO_LIST);

        //When & Then
        when(trelloClient.getTrelloListById("1")).thenReturn(Optional.of(trelloListDto));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "lists/{idList}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("myList")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.closed", Matchers.is(false)));
    }

    @Test
    void shouldReturnTrelloCard() throws Exception {
        //Given
        TrelloCardDto trelloCardDto = (TrelloCardDto) objectFactoryForTest(TRELLO_CARD);

        //When & Then
        when(trelloClient.getTrelloCardById("1")).thenReturn(Optional.of(trelloCardDto));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "cards/{idCard}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("myCard")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.desc", Matchers.is("myDesc")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pos", Matchers.is("myPos")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idList", Matchers.is("1")));
    }

    @Test
    void shouldReturnTrelloListThatContainsTrelloCard() throws Exception {
        //Given
        TrelloListDto trelloListDto = (TrelloListDto) objectFactoryForTest(TRELLO_LIST);
        TrelloCardDto trelloCardDto = (TrelloCardDto) objectFactoryForTest(TRELLO_CARD);
        trelloListDto.setCardDtoList(List.of(trelloCardDto));

        //When & Then
        when(trelloClient.getListThatContainsCard("1")).thenReturn(Optional.of(trelloListDto));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "lists/parentListOfCard/{idCard}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("myList")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.closed", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cardList", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cardList[0].name", Matchers.is("myCard")));
    }

    @Test
    void shouldReturnCardsFromList() throws Exception {
        //Given
        List<TrelloCardDto> cardDtos = new ArrayList<>();
        cardDtos.add((TrelloCardDto) objectFactoryForTest(TRELLO_CARD));
        cardDtos.add((TrelloCardDto) objectFactoryForTest(TRELLO_CARD));

        //When & Then
        when(trelloClient.getAllCardsFromList("1")).thenReturn(Optional.of(cardDtos));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "cards/fromList/{idList}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("myCard")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("myCard")));
    }

    @Test
    void shouldReturnCardsFromBoard() throws Exception {
        //Given
        List<TrelloCardDto> cardDtos = new ArrayList<>();
        cardDtos.add((TrelloCardDto) objectFactoryForTest(TRELLO_CARD));
        cardDtos.add((TrelloCardDto) objectFactoryForTest(TRELLO_CARD));

        //When & Then
        when(trelloService.getAllCardsFromBoard("Kodilla Aplication")).thenReturn(Optional.of(cardDtos));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(TRELLO_URL + "cards/fromBoard/{boardName}", "Kodilla Aplication")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("myCard")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].desc", Matchers.is("myDesc")));
    }

    @Test
    void shouldCreateTrelloCard() throws Exception {
        //Given
        TrelloCardDto cardToCreate = (TrelloCardDto) objectFactoryForTest(TRELLO_CARD);
        CreatedTrelloCardDto createdCard = new CreatedTrelloCardDto("1", "myCard", "https://trello.com/card/1");

        when(trelloFacade.createCard(cardToCreate)).thenReturn(createdCard);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(cardToCreate);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(TRELLO_URL + "cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("myCard")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl", Matchers.is("https://trello.com/card/1")));
    }

    @Test
    void shouldUpdateTrelloCard() throws Exception {
        //Given
        TrelloCardDto afterUpdate = new TrelloCardDto("1", "newName", "newDesc", "newPos", "newIdList");

        //When & Then
        doNothing().when(trelloClient).updateCard("1", afterUpdate);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(afterUpdate);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put(TRELLO_URL + "cards")
                        .param("idCard", "1")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(trelloClient, times(1)).updateCard("1", afterUpdate);
    }

    @Test
    void shouldDeleteTrelloCard() throws Exception {
        //When & Then
        doNothing().when(trelloClient).deleteCard("1");

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete(TRELLO_URL + "cards/{idCard}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(trelloClient, times(1)).deleteCard("1");
    }

    private Object objectFactoryForTest(String objectType) {
        switch (objectType) {
            case TRELLO_BOARD: return new TrelloBoardDto("1", "myBoard", new ArrayList<>());
            case TRELLO_LIST: return new TrelloListDto("1", "myList", false, new ArrayList<>());
            case TRELLO_CARD: return new TrelloCardDto("1","myCard", "myDesc", "myPos", "1");
            default: throw new IllegalStateException("Object Type Not Found");
        }
    }
}