package com.crud.tasks.trello.facade;

import com.crud.tasks.domain.*;
import com.crud.tasks.mapper.TrelloMapper;
import com.crud.tasks.service.TrelloService;
import com.crud.tasks.trello.client.TrelloClient;
import com.crud.tasks.trello.validator.TrelloValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TrelloFacadeTestSuite {

    @InjectMocks
    private TrelloFacade trelloFacade;

    @Mock
    private TrelloService trelloService;

    @Mock
    private TrelloValidator trelloValidator;

    @Mock
    private TrelloMapper trelloMapper;

    @Test
    public void shouldFetchPresentBoardsListWithOnePosition() {
        //Given
        List<TrelloListDto> trelloListDtos = new ArrayList<>();
        trelloListDtos.add(new TrelloListDto("1", "In_Progress", true));

        List<TrelloBoardDto> trelloBoardDtos = new ArrayList<>();
        trelloBoardDtos.add(new TrelloBoardDto("1", "Kodilla_Course", trelloListDtos));

        List<TrelloList> mappedTrelloLists = new ArrayList<>();
        mappedTrelloLists.add(new TrelloList("1", "In_Progress", true));

        List<TrelloBoard> mappedTrelloBoards = new ArrayList<>();
        mappedTrelloBoards.add(new TrelloBoard("1", "Kodilla_Course", mappedTrelloLists));

        when(trelloService.fetchTrelloBoards()).thenReturn(trelloBoardDtos);
        when(trelloMapper.mapToBoards(trelloBoardDtos)).thenReturn(mappedTrelloBoards);
        when(trelloMapper.mapToBoardsDto(mappedTrelloBoards)).thenReturn(trelloBoardDtos);
        when((trelloValidator.validateTrelloBoards(mappedTrelloBoards))).thenReturn(mappedTrelloBoards);

        //When
        List<TrelloBoardDto> fetchedTrelloBoards = trelloFacade.fetchTrelloBoards();

        //Then
        assertNotNull(fetchedTrelloBoards);
        assertEquals(1, fetchedTrelloBoards.size());

        System.out.println(fetchedTrelloBoards);

        fetchedTrelloBoards.forEach(fTB -> {
            assertEquals("1", fTB.getId());
            assertEquals("Kodilla_Course", fTB.getName());

            fTB.getLists().forEach(fTB_TrelloListDto -> {
                assertEquals("1", fTB_TrelloListDto.getId());
                assertEquals("In_Progress", fTB_TrelloListDto.getName());
                assertTrue(fTB_TrelloListDto.isClosed());
            });
        });
    }

    @Test
    public void shouldFetchEmptyList() {
        //Given
        List<TrelloListDto> trelloListDtos = new ArrayList<>();
        trelloListDtos.add(new TrelloListDto("test_list", "test_name", false));

        List<TrelloBoardDto> trelloBoardDtos = new ArrayList<>();
        trelloBoardDtos.add(new TrelloBoardDto("test_id", "test_name", trelloListDtos));

        List<TrelloList> mappedTrelloLists = new ArrayList<>();
        mappedTrelloLists.add(new TrelloList("test_list", "test_name", false));

        List<TrelloBoard> mappedTrelloBoards = new ArrayList<>();
        mappedTrelloBoards.add(new TrelloBoard("test_id", "test_name", mappedTrelloLists));

        when(trelloService.fetchTrelloBoards()).thenReturn(trelloBoardDtos);
        when(trelloMapper.mapToBoards(trelloBoardDtos)).thenReturn(mappedTrelloBoards);
        when(trelloMapper.mapToBoardsDto(anyList())).thenReturn(List.of()); //anyList() -> returns empty list -> i could use .clear() to appropriate lists and get this same result
        when(trelloValidator.validateTrelloBoards(anyList())).thenReturn(List.of()); //same as above

        //When
        List<TrelloBoardDto> fetchedTrelloBoards = trelloFacade.fetchTrelloBoards();

        //Then
        assertNotNull(fetchedTrelloBoards);
        assertEquals(0, fetchedTrelloBoards.size());
    }

    @Test
    public void shouldCreateCard() {
        //Given
        TrelloCardDto cardToCreate = new TrelloCardDto("test_card", "test_desc", "bottom", "10");
        CreatedTrelloCardDto createdCard = new CreatedTrelloCardDto("1", "test_card", "http://test.com/card/1");

        when(trelloService.createTrelloCard(cardToCreate)).thenReturn(createdCard);
        when(trelloMapper.mapToCardDto(any())).thenReturn(cardToCreate);

        //When
        CreatedTrelloCardDto newCard = trelloFacade.createCard(cardToCreate);

        //Then
        assertNotNull(newCard);
        assertEquals("test_card", newCard.getName());
    }
}
