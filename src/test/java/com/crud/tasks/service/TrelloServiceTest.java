package com.crud.tasks.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrelloServiceTest {

    @Autowired
    TrelloService trelloService;

    @Test
    void getTrelloCardByName() {
        //Given
        String name = "Yeah ";

        //When
        var result = trelloService.getTrelloCardByName(name).get();
        System.out.println(result);

        //Then
        assertEquals("Yeah ", result.getName());
    }

    @Test
    void getNameOfTrelloListThatContainsCard() {
    }
}