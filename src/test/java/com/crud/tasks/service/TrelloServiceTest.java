package com.crud.tasks.service;

import com.crud.tasks.domain.TrelloBoard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.mapper.TrelloMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrelloServiceTest {

    @Autowired
    TrelloService trelloService;

    @Autowired
    TrelloMapper trelloMapper;

    @Test
    void getTrelloCardByName() {
        //Given
        String name = "Testing Thymeleaf";

        //When
        var result = trelloService.getTrelloCardByName(name).get();
        System.out.println(result);

        //Then
        assertEquals("Testing Thymeleaf", result.getName());
    }

    @Test
    void test() {
        List<TrelloBoardDto> trelloBoardDtos = trelloService.fetchTrelloBoards();
        trelloBoardDtos.forEach(tBDto -> tBDto.getLists().forEach(tLDto -> tLDto.setCardList(trelloService.getAllCardsForList(tLDto.getId()))));
        List<TrelloBoard> trelloBoards = trelloMapper.mapToBoards(trelloBoardDtos);

        trelloBoards.forEach(System.out::println);
    }
}