package com.crud.tasks.trello.mapper;

import com.crud.tasks.domain.*;
import com.crud.tasks.mapper.TrelloMapper;
import com.crud.tasks.trello.client.TrelloClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrelloMapperTest {

    @Autowired
    TrelloMapper trelloMapper;

    @Autowired
    TrelloClient trelloClient;

    List<TrelloBoardDto> trelloBoardDtos = new ArrayList<>();
    List<TrelloBoard> trelloBoards = new ArrayList<>();
    List<TrelloListDto> trelloListDtos = new ArrayList<>();
    List<TrelloList> trelloLists = new ArrayList<>();

    @BeforeEach
    public void setStartVariables() {
        trelloListDtos.add(new TrelloListDto("1", "test_listDTO", false, new ArrayList<>()));
        trelloBoardDtos.add(new TrelloBoardDto("1", "test_boardDTO", trelloListDtos));
        trelloLists.add(new TrelloList("1", "test_list", false, new ArrayList<>()));
        trelloBoards.add(new TrelloBoard("1", "test_board", trelloLists));
    }

    @Test
    public void testMapToBoards() {
        //Given
            //setStartVariables()

        //When
        List<TrelloBoard> resultBoardsList = trelloMapper.mapToBoards(trelloBoardDtos);

        //Then
        assertEquals(1, resultBoardsList.size());

        resultBoardsList.forEach(tB -> {
                    assertEquals("1", tB.getId());
                    assertEquals("test_boardDTO",  tB.getName());

                    tB.getLists().forEach(tL -> {
                        assertEquals("1", tL.getId());
                        assertEquals("test_listDTO", tL.getName());
                        assertFalse(tL.isClosed());
                    });
                });
    }

    @Test
    public void testMapToBoardsDto() {
        //Given
            //setStartVariables()

        //When
        List<TrelloBoardDto> resultBoardDtoList = trelloMapper.mapToBoardsDto(trelloBoards);

        //Then
        assertEquals(1, resultBoardDtoList.size());

        resultBoardDtoList.forEach(tBDto -> {
            assertEquals("1", tBDto.getId());
            assertEquals("test_board", tBDto.getName());

            tBDto.getLists().forEach(tLDto -> {
                assertEquals("1", tLDto.getId());
                assertEquals("test_list", tLDto.getName());
                assertFalse(tLDto.isClosed());
            });
        });
    }

    @Test
    public void testMapToList() {
        //Given
            //setStartVariables()

        //When
        List<TrelloList> resultList = trelloMapper.mapToList(trelloListDtos);

        //Then
        assertEquals(1, resultList.size());

        resultList.forEach(tL -> {
            assertEquals("1", tL.getId());
            assertEquals("test_listDTO", tL.getName());
            assertFalse(tL.isClosed());
        });
    }

    @Test
    public void testMapToListDto() {
        //Given
            //setStartVariables()

        //When
        List<TrelloListDto> resultListDto = trelloMapper.mapToListDto(trelloLists);

        //Then
        assertEquals(1, resultListDto.size());

        resultListDto.forEach(tLDto -> {
            assertEquals("1", tLDto.getId());
            assertEquals("test_list", tLDto.getName());
            assertFalse(tLDto.isClosed());
        });
    }

    @Test
    public void testMapToCard() {
        //Given
        TrelloCardDto trelloCardDto = TrelloCardDto.builder()
                .name("test_name")
                .desc("test_description")
                .pos("top")
                .idList("1")
                .build();

        //When
        TrelloCard trelloCard = trelloMapper.mapToCard(trelloCardDto);

        //Then
        assertNotNull(trelloCard);
        assertEquals("test_name", trelloCard.getName());
    }

    @Test
    public void testMapToCardDto() {
        //Given
        TrelloCard trelloCard = TrelloCard.builder()
                .name("test_name")
                .desc("test_description")
                .pos("bottom")
                .idList("1")
                .build();

        //When
        TrelloCardDto trelloCardDto = trelloMapper.mapToCardDto(trelloCard);

        //Then
        assertNotNull(trelloCardDto);
        assertEquals("test_name", trelloCardDto.getName());
    }

    @Test
    public void testMapToBoard() {
        //Given
        //setStartVariables()

        //When
        TrelloBoard trelloBoard = trelloMapper.mapToBoard(trelloBoardDtos.get(0));

        //Then
        assertNotNull(trelloBoard);
        assertEquals("test_boardDTO", trelloBoard.getName());
    }
}
