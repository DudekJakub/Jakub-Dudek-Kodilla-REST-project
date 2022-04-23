package com.crud.tasks.service;

import com.crud.tasks.trello.client.TrelloClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrelloServiceTest {

    @Autowired
    TrelloService trelloService;

    @Autowired
    TrelloClient trelloClient;

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
    void getCardById() {
        //Given
        String id = "626431eec303822dbce0fdac";

        //When
        var result = trelloClient.getTrelloCardById(id);
        System.out.println(result);
    }
}