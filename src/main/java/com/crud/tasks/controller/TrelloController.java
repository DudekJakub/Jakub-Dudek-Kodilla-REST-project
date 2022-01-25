package com.crud.tasks.controller;

import com.crud.tasks.domain.CreatedTrelloCard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.domain.TrelloListDto;
import com.crud.tasks.service.TrelloService;
import com.crud.tasks.trello.client.TrelloClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/v1/trello")
@RequiredArgsConstructor
public class TrelloController {

    private final TrelloClient trelloClient;
    private final TrelloService trelloService;

    @RequestMapping(method = RequestMethod.GET, value = "getTrelloBoards")
    public List<TrelloBoardDto> getTrelloBaords() {
        return trelloService.fetchTrelloBoards();
    }

    @RequestMapping(method = RequestMethod.GET, value = "getTrelloListById")
    public Optional<TrelloListDto> getTrelloList(@RequestParam String id) {
        return trelloClient.getTrelloListById(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "createTrelloCard")
    public CreatedTrelloCard createTrelloCard(@RequestBody TrelloCardDto trelloCardDto) {
        return trelloService.createTrelloCard(trelloCardDto);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "updateCard")
    public void updateCard(@RequestParam String id, @RequestBody TrelloCardDto trelloCardDto) {
        trelloClient.updateCard(id, trelloCardDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "deleteCard")
    public void deleteCard(@RequestParam String id) {
        trelloClient.deleteCard(id);
    }
}
