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

    @GetMapping("/boards")
    public List<TrelloBoardDto> getTrelloBoards() {
        return trelloFacade.fetchTrelloBoards();
    }

    @GetMapping("/lists/{idList}")
    public Optional<TrelloListDto> getTrelloList(@PathVariable String idList) {
        return trelloClient.getTrelloListById(idList);
    }

    @GetMapping("/cards/{idCard}")
    public Optional<TrelloCardDto> getTrelloCard(@PathVariable String idCard) {
        return trelloClient.getTrelloCardById(idCard);
    }

    @GetMapping("/lists/parentListOfCard/{idCard}")
    public Optional<TrelloListDto> getListThatContainsCard(@PathVariable String idCard) {
        return trelloClient.getListThatContainsCard(idCard);
    }

    @GetMapping("/cards/fromList/{idList}")
    public Optional<List<TrelloCardDto>> getCardsFromList(@RequestParam String idList) {
        return trelloClient.getAllCardsFromList(idList);
    }

    @GetMapping("/cards/fromBoard/{nameBoard}")
    public Optional<List<TrelloCardDto>> getAllCardsFromBoard(@PathVariable String nameBoard) {
        return trelloClient.getAllCardsFromBoard(nameBoard);
    }

    @PostMapping( "/cards")
    public CreatedTrelloCardDto createTrelloCard(@RequestBody TrelloCardDto trelloCardDto) {
        System.out.println(trelloCardDto);
        return trelloFacade.createCard(trelloCardDto);
    }

    @PutMapping( "/cards")
    public void updateCard(@RequestParam String idCard, @RequestBody TrelloCardDto trelloCardDto) {
        trelloClient.updateCard(idCard, trelloCardDto);
    }

    @DeleteMapping( "/cards/{idCard}")
    public void deleteCard(@RequestParam String idCard) {
        trelloClient.deleteCard(idCard);
    }
}
