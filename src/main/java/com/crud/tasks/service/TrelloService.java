package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.*;
import com.crud.tasks.trello.client.TrelloClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Optional<TrelloCardDto> getTrelloCardByName(String name) {
        try {
            return trelloClient.getAllCardsFromBoard(KODILLA_TRELLO_BOARD)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(trelloCardDto -> trelloCardDto.getName().equals(name))
                    .findAny();
        } catch (NullPointerException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return Optional.empty();
        }
    }

    public TrelloListDto getTrelloListById(String id) {
            return trelloClient.getTrelloListById(id).orElseThrow();
    }

    public List<TrelloCardDto> getAllCardsForList(String id) {
        return trelloClient.getAllCardsFromList(id).orElse(new ArrayList<>());
    }
}
