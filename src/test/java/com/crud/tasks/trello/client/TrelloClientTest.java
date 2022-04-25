package com.crud.tasks.trello.client;

import com.crud.tasks.domain.*;
import com.crud.tasks.exception.TrelloNotFound;
import com.crud.tasks.trello.config.TrelloConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrelloClientTest {

    @InjectMocks
    private TrelloClient trelloClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TrelloConfig trelloConfig;

    private static final String TEST_URI = "https://test.com";
    private static final String KEY_N_TOKEN = "key=test&token=test";

    @BeforeEach
    public void setWhenStatement(TestInfo excludedTest) {
        if (excludedTest.getDisplayName().equals("testGenerateObjects")) {
            return;
        }
        when(trelloConfig.getTrelloApiEndpoint()).thenReturn(TEST_URI);
        when(trelloConfig.getTrelloAppKey()).thenReturn("test");
        when(trelloConfig.getTrelloToken()).thenReturn("test");
    }

    @Test
    public void shouldFetchTrelloBoards() throws URISyntaxException {
        //Given
        when(trelloConfig.getUsername()).thenReturn("test");

        TrelloBoardDto[] trelloBoards = new TrelloBoardDto[1];
        trelloBoards[0] = new TrelloBoardDto("test_id", "test_board_Kodilla", new ArrayList<>());

        URI uri = new URI(TEST_URI + "/members/test/boards?" + KEY_N_TOKEN + "&fields=name,id&lists=all");

        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(trelloBoards);

        //When
        List<TrelloBoardDto> fetchedTrelloBoards = trelloClient.getTrelloBoards();

        //Then
        assertEquals(1, fetchedTrelloBoards.size());
        assertEquals("test_id", fetchedTrelloBoards.get(0).getId());
        assertEquals("test_board_Kodilla", fetchedTrelloBoards.get(0).getName());
        assertEquals(new ArrayList<>(), fetchedTrelloBoards.get(0).getLists());
    }

    @Test
    public void shouldCreateCard() throws URISyntaxException {
        //Given
        TrelloCardDto trelloCardDto = new TrelloCardDto("1", "Test task", "Test Description", "top", "test_id");
        CreatedTrelloCardDto createdTrelloCardDto = new CreatedTrelloCardDto("1", "test task", "https://test.com");

        URI uri = new URI(TEST_URI + "/cards?" + KEY_N_TOKEN + "&name=Test%20task&desc=Test%20Description&pos=top&idList=test_id");

        when(restTemplate.postForObject(uri, null, CreatedTrelloCardDto.class)).thenReturn(createdTrelloCardDto);

        //When
        CreatedTrelloCardDto newCard = trelloClient.createNewCard(trelloCardDto);

        //Then
        assertEquals("1", newCard.getId());
        assertEquals("test task", newCard.getName());
        assertEquals("https://test.com", newCard.getShortUrl());
    }

    @Test
    public void shouldReturnEmptyList() throws URISyntaxException {
        //Given
        when(trelloConfig.getUsername()).thenReturn("test_user");

        TrelloBoardDto[] trelloBoards = new TrelloBoardDto[1];
        trelloBoards[0] = new TrelloBoardDto("id", "Test name_Kodilla", new ArrayList<>());

        URI uri = new URI(TEST_URI + "/members/test_user/boards?" + KEY_N_TOKEN + "&fields=name,id&lists=all");

        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(null);

        //When
        List<TrelloBoardDto> resultEmptyList = trelloClient.getTrelloBoards();

        //Then
        assertNotEquals(1, resultEmptyList.size());
        assertEquals(0, resultEmptyList.size());
    }

    @Test
    public void shouldReturnListById() throws URISyntaxException {
        //Given
        TrelloListDto trelloList = new TrelloListDto("1", "test", false, new ArrayList<>());

        URI uri1 = new URI(TEST_URI + "/lists/1/cards?" + KEY_N_TOKEN);
        URI uri2 = new URI(TEST_URI + "/lists/1?" + KEY_N_TOKEN);

        when(restTemplate.getForObject(uri1, TrelloCardDto[].class)).thenReturn(new TrelloCardDto[0]);
        when(restTemplate.getForObject(uri2, TrelloListDto.class)).thenReturn(trelloList);

        //When
        TrelloListDto resultList = trelloClient.getTrelloListById("1").orElseThrow(() -> new TrelloNotFound(TrelloNotFound.LIST_NF));

        //Then
        assertNotNull(resultList);
        assertEquals("1", resultList.getId());
        assertEquals("test", resultList.getName());
        assertFalse(resultList.isClosed());
    }

    @Test
    public void shouldReturnCardById() throws URISyntaxException {
        //Given
        TrelloCardDto trelloCard = new TrelloCardDto("1", "testName", "testDesc", "testPos", "testIdList");

        URI uri = new URI(TEST_URI + "/cards/1?" + KEY_N_TOKEN + "&fields=idShort,name,pos,desc,idList");

        when(restTemplate.getForObject(uri, TrelloCardDto.class)).thenReturn(trelloCard);

        //When
        TrelloCardDto trelloCardById = trelloClient.getTrelloCardById("1").orElseThrow(() -> new TrelloNotFound(TrelloNotFound.CARD_NF));

        //Then
        assertNotNull(trelloCardById);
        assertEquals("1", trelloCardById.getId());
        assertEquals("testName", trelloCardById.getName());
        assertEquals("testDesc", trelloCardById.getDesc());
    }

    @Test
    public void shouldReturnListThatContainsCard() throws URISyntaxException {
        //Given
        TrelloListDto trelloList = new TrelloListDto("1", "testList", false, new ArrayList<>());
        TrelloCardDto trelloCard = new TrelloCardDto("1", "testCard", "testDesc", "testPos", "1");
        trelloList.setCardDtoList(List.of(trelloCard));

        URI uri = new URI(TEST_URI + "/cards/1/list?" + KEY_N_TOKEN);

        when(restTemplate.getForObject(uri, TrelloListDto.class)).thenReturn(trelloList);

        //When
        Optional<TrelloListDto> listThatContainsCard = trelloClient.getListThatContainsCard("1");

        //Then
        assertNotNull(trelloList);
        assertEquals("testList", trelloList.getName());
        assertEquals(1, trelloList.getCardDtoList().size());
        assertTrue(trelloList.getCardDtoList().contains(trelloCard));
    }

    @Test
    public void shouldUpdateCard() throws URISyntaxException {
        //Given
        CreatedTrelloCardDto cardDto = new CreatedTrelloCardDto("1", "test_card", "https://test.com/cardId/1");
        TrelloCardDto updateCard = new TrelloCardDto("1", "new_name", "new_description", "bottom", "10");

        URI uri = new URI(TEST_URI + "/cards/1?" + KEY_N_TOKEN + "&name=new_name&desc=new_description&pos=bottom");

        //When
        trelloClient.updateCard(cardDto.getId(), updateCard);

        //Then
        verify(restTemplate, times(1)).put(uri, null);
    }

    @Test
    public void shouldReturnAllCardsFromList() throws URISyntaxException {
        //Given
        TrelloCardDto[] trelloCardDtosArray = new TrelloCardDto[] {
                new TrelloCardDto("1", "name1", "desc1", "pos1", "100"),
                new TrelloCardDto("2", "name2", "desc2", "pos2", "100")
        };

        URI uri = new URI(TEST_URI + "/lists/100/cards?" + KEY_N_TOKEN);

        when(restTemplate.getForObject(uri, TrelloCardDto[].class)).thenReturn(trelloCardDtosArray);

        //When
        var resultList = trelloClient.getAllCardsFromList("100").orElseThrow(() -> new TrelloNotFound(TrelloNotFound.LIST_NF));

        //Then
        assertEquals(2, resultList.size());
    }

    @Test
    public void shouldDeleteCard() throws URISyntaxException {
        //Given
        URI uri = new URI(TEST_URI + "/cards/1?" + KEY_N_TOKEN);

        doNothing().when(restTemplate).delete(uri);

        //When
        trelloClient.deleteCard("1");

        //Then
        verify(restTemplate, times(1)).delete(uri);
    }
}

