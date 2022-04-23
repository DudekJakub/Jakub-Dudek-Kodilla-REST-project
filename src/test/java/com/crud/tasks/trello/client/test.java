package com.crud.tasks.trello.client;

import com.crud.tasks.domain.TrelloBoard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.mapper.TrelloMapper;
import com.crud.tasks.service.TrelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class test {

    @Autowired
    TrelloService trelloService;

    @Autowired
    TrelloMapper trelloMapper;

    @Test
    public void test() {

        List<TrelloBoardDto> trelloBoardDtos = trelloService.fetchTrelloBoards();
        System.out.println("trelloBoardDtso");
        trelloBoardDtos.forEach(System.out::println);

        List<TrelloBoardDto> trelloBoardDtos1 = trelloService.fetchTrelloBoards();
        trelloBoardDtos1.forEach(tBDto -> tBDto.getLists().forEach(tLDto -> tLDto.setCardDtoList(trelloService.getAllCardsForList(tLDto.getId()))));
        List<TrelloBoard> trelloBoards = trelloMapper.mapToBoards(trelloBoardDtos1);

        System.out.println("\ntrelloBoardDtso1");
        trelloBoardDtos1.forEach(System.out::println);

        System.out.println("\n\ntrelloBoardsPoMapowaniu");
        trelloBoards.forEach(System.out::println);


    }
}
