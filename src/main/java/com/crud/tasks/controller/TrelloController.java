package com.crud.tasks.controller;

import com.crud.tasks.domain.CreatedTrelloCardDto;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.trello.client.TrelloClient;
import com.crud.tasks.trello.facade.TrelloFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/v1/trello")
@RequiredArgsConstructor
public class TrelloController {

    @Autowired
    private final TrelloClient trelloClient;

    @Autowired
    private final TrelloFacade trelloFacade;

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
        return trelloClient.getTrelloCard(id);
    }

    @GetMapping("getCardOwnerList")
    public Optional<Map<String, String>> getCardOwnerList(@RequestParam String id) {
        return trelloClient.getList_CardIsOn(id);
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
