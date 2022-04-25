package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.*;
import com.crud.tasks.trello.client.TrelloClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrelloServiceTest {

    @InjectMocks
    TrelloService trelloService;

    @Mock
    TrelloClient trelloClient;

    @Mock
    SimpleEmailService emailService;

    @Mock
    AdminConfiguration adminConfig;

    @Test
    void testGetTrelloCardByName() {
        //Given
        String cardName = "Testing Thymeleaf";
        String boardName = "Kodilla Aplication";

        var boardDtoList = new ArrayList<TrelloBoardDto>();
        var trelloListDtos = new ArrayList<TrelloListDto>();
        var cardDtoList = new ArrayList<TrelloCardDto>();

        boardDtoList.add(new TrelloBoardDto("1", boardName, trelloListDtos));
        trelloListDtos.add(new TrelloListDto("100", "test_list", false, cardDtoList));
        cardDtoList.add(new TrelloCardDto("1", "Testing Thymeleaf", "desc_test", "pos_test", "100"));

        when(trelloClient.getTrelloBoards()).thenReturn(boardDtoList);
        when(trelloClient.getAllCardsFromList("100")).thenReturn(Optional.of(cardDtoList));

        //When
        var cardResult = trelloService.getTrelloCardByName(cardName).orElseThrow();

        //Then
        assertEquals("Testing Thymeleaf", cardResult.getName());
    }

    @Test
    public void testGetAllCardsFromBoard() {
        //Given
        var boardDtoList = new ArrayList<TrelloBoardDto>();
        var trelloListDtos = new ArrayList<TrelloListDto>();
        var cardDtoList = new ArrayList<TrelloCardDto>();

        boardDtoList.add(new TrelloBoardDto("1", "Kodilla Aplication", trelloListDtos));
        trelloListDtos.add(new TrelloListDto("100", "test_list", false, cardDtoList));
        cardDtoList.add(new TrelloCardDto("1", "test_card1", "test_desc1", "test_pos1", "100"));
        cardDtoList.add(new TrelloCardDto("2", "test_card2", "test_desc2", "test_pos2", "100"));

        var boardName = boardDtoList.get(0).getName();

        when(trelloClient.getTrelloBoards()).thenReturn(boardDtoList);
        when(trelloClient.getAllCardsFromList("100")).thenReturn(Optional.of(cardDtoList));

        //When
        var cardListResult = trelloService.getAllCardsFromBoard(boardName).orElseThrow();

        //Then
        assertEquals(2, cardListResult.size());
    }

    @Test
    public void testGetAllCardsFromList() {
        //Given
        var cardDtoList = new ArrayList<TrelloCardDto>();
        cardDtoList.add(new TrelloCardDto("1", "test_card1", "test_desc1", "test_pos1", "100"));
        cardDtoList.add(new TrelloCardDto("2", "test_card2", "test_desc2", "test_pos2", "100"));

        when(trelloClient.getAllCardsFromList("100")).thenReturn(Optional.of(cardDtoList));

        //When
        var cardListResult = trelloService.getAllCardsForList("100");

        //Then
        assertEquals(2, cardListResult.size());
    }

    @Test
    public void testCreateTrelloCard() {
        //Given
        var trelloCard = new TrelloCardDto("1", "test_card", "test_desc", "test_pos", "100");
        var createdTrelloCard = new CreatedTrelloCardDto("1", "test_card", "https://www.trello.pl");

        when(trelloClient.createNewCard(trelloCard)).thenReturn(createdTrelloCard);
        when(adminConfig.getAdminMail()).thenReturn(null);
        doNothing().when(emailService).sendNewTrelloCardMail(any());

        //When
        var newTrelloCard = trelloService.createTrelloCard(trelloCard);

        //Then
        assertEquals("test_card", newTrelloCard.getName());
    }

    @Test
    public void testGetNameOfTrelloListThatContainsCard() {
        //Given
        var cardDto = new TrelloCardDto("1", "test_card", "test_desc", "test_pos", "100");
        var listDto = new TrelloListDto("100", "test_list", false, List.of(cardDto));

        when(trelloClient.getListThatContainsCard("1")).thenReturn(Optional.of(listDto));

        //When
        var resultTrelloListName = trelloService.getNameOfTrelloListThatContainsCard("1");

        //Then
        assertNotNull(resultTrelloListName);
        assertEquals("test_list", resultTrelloListName);
    }
}