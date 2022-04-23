package com.crud.tasks.controller;

import com.crud.tasks.domain.CreatedTrelloCardDto;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.trello.client.TrelloClient;
import com.crud.tasks.trello.facade.TrelloFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/v1/trello")
public class TrelloController {

    private final TrelloClient trelloClient;
    private final TrelloFacade trelloFacade;

    @Autowired
    public TrelloController(TrelloClient trelloClient, TrelloFacade trelloFacade) {
        this.trelloClient = trelloClient;
        this.trelloFacade = trelloFacade;
    }

    @GetMapping("getTrelloBoards")
    public List<TrelloBoardDto> getTrelloBoards() {
        return trelloFacade.fetchTrelloBoards();
    }

    @GetMapping("getTrelloListById")
    public Optional<TrelloListDto> getTrelloList(@RequestParam String id) {
        return trelloClient.getTrelloListById(id);
    }

    @GetMapping("getTrelloCardById")
    public Optional<TrelloCardDto> getTrelloCard(@RequestParam String id) {
        return trelloClient.getTrelloCardById(id);
    }

    @GetMapping("getListThatContainsCard")
    public Optional<TrelloListDto> getListThatContainsCard(@RequestParam String id) {
        return trelloClient.getListThatContainsCard(id);
    }

    @GetMapping("getCardsFromList")
    public Optional<List<TrelloCardDto>> getCardsFromList(@RequestParam String id) {
        return trelloClient.getAllCardsFromList(id);
    }

    @GetMapping("getAllCardsFromBoard")
    public Optional<List<TrelloCardDto>> getAllCardsFromBoard(@RequestParam String name) {
        return trelloClient.getAllCardsFromBoard(name);
    }

    @PostMapping( "createTrelloCard")
    public CreatedTrelloCardDto createTrelloCard(@RequestBody TrelloCardDto trelloCardDto) {
        System.out.println(trelloCardDto);
        return trelloFacade.createCard(trelloCardDto);
    }

    @PutMapping( "updateCard")
    public void updateCard(@RequestParam String id, @RequestBody TrelloCardDto trelloCardDto) {
        trelloClient.updateCard(id, trelloCardDto);
    }

    @DeleteMapping( "deleteCard")
    public void deleteCard(@RequestParam String id) {
        trelloClient.deleteCard(id);
    }
}
