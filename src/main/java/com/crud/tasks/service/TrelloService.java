package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.*;
import com.crud.tasks.exception.TrelloNotFound;
import com.crud.tasks.trello.client.TrelloClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloService.class);
    private static final String SUBJECT = "Tasks: New Trello Card";
    private static final String KODILLA_TRELLO_BOARD = "Kodilla Aplication";

    private final TrelloClient trelloClient;
    private final SimpleEmailService emailService;
    private final AdminConfiguration adminConfig;

    public List<TrelloBoardDto> fetchTrelloBoards() {
        return trelloClient.getTrelloBoards();
    }

    public CreatedTrelloCardDto createTrelloCard(final TrelloCardDto trelloCardDto) {
        CreatedTrelloCardDto newCard = trelloClient.createNewCard(trelloCardDto);

        Mail mail = Mail.builder()
                .mailTo(adminConfig.getAdminMail())
                .subject(SUBJECT)
                .message("New card: " + trelloCardDto.getName() + " has been created on Your Trello account")
                .toCc(null)
                .build();

        Optional.ofNullable(newCard).ifPresent(card -> emailService.sendNewTrelloCardMail(mail));

        return newCard;
    }

    public Optional<List<TrelloCardDto>> getAllCardsFromBoard(String name) {
        try {
            List<TrelloCardDto> allCardsFromBoard = new ArrayList<>();
            trelloClient.getTrelloBoards().stream()
                    .filter(tBDto -> tBDto.getName().equals(name))
                    .findAny()
                    .orElseThrow(()-> new TrelloNotFound(TrelloNotFound.BOARD_NF))
                    .getLists()
                    .forEach(trelloListDto -> allCardsFromBoard.addAll(trelloClient.getAllCardsFromList(trelloListDto.getId()).orElseThrow()));
            return Optional.of(allCardsFromBoard);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<TrelloCardDto> getAllCardsForList(String id) {
        return trelloClient.getAllCardsFromList(id).orElseThrow(()-> new TrelloNotFound(TrelloNotFound.LIST_NF));
    }

    public Optional<TrelloCardDto> getTrelloCardByName(String name) {
        try {
            return this.getAllCardsFromBoard(KODILLA_TRELLO_BOARD)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(trelloCardDto -> trelloCardDto.getName().equals(name))
                    .findAny();
        } catch (TrelloNotFound e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return Optional.empty();
        }
    }

    public String getNameOfTrelloListThatContainsCard(String cardId) {
        return trelloClient.getListThatContainsCard(cardId).orElseThrow(()-> new TrelloNotFound(TrelloNotFound.CARD_NF)).getName();
    }
}
