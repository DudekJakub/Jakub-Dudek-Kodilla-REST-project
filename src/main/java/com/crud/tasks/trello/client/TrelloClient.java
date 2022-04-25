package com.crud.tasks.trello.client;

import com.crud.tasks.domain.*;
import com.crud.tasks.trello.config.TrelloConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrelloClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloClient.class);

    private final RestTemplate restTemplate;
    private final TrelloConfig trelloConfig;

    private URI buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/members/" + trelloConfig.getUsername() + "/boards")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("fields", "name,id")
                .queryParam("lists", "all")
                .build()
                .encode()
                .toUri();
    }

    public List<TrelloBoardDto> getTrelloBoards() {
        URI url = buildUrl();
        try {
            TrelloBoardDto[] boardsResponse = restTemplate.getForObject(url, TrelloBoardDto[].class);
            return Optional.ofNullable(boardsResponse)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(p -> Objects.nonNull(p.getId()) && Objects.nonNull(p.getName()))
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Optional<List<TrelloCardDto>> getAllCardsFromList(String id) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/lists/" + id + "/cards")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .build()
                .encode().toUri();
        try {
            var trelloCardDtoList = Arrays.stream(Objects.requireNonNull(restTemplate.getForObject(url, TrelloCardDto[].class))).collect(Collectors.toList());
            return Optional.of(trelloCardDtoList);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<TrelloListDto> getTrelloListById(String id) {
        List<TrelloCardDto> cardDtos = getAllCardsFromList(id).orElse(new ArrayList<>());

        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/lists/" + id)
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .build()
                .encode().toUri();
        try {
            TrelloListDto trelloListDto = restTemplate.getForObject(url, TrelloListDto.class);
            assert trelloListDto != null;
            trelloListDto.setCardDtoList(cardDtos);
            return Optional.of(trelloListDto);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<TrelloCardDto> getTrelloCardById(String id) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards/" + id)
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("fields", "idShort,name,pos,desc,idList")
                .build()
                .encode().toUri();
        try {
            TrelloCardDto trelloCardDto = restTemplate.getForObject(url, TrelloCardDto.class);
            return Optional.ofNullable(trelloCardDto);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<TrelloListDto> getListThatContainsCard(String id) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards/" + id + "/list")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .build()
                .encode().toUri();
        try {
            TrelloListDto trelloListDto = restTemplate.getForObject(url, TrelloListDto.class);
            return Optional.ofNullable(trelloListDto);
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public CreatedTrelloCardDto createNewCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("name", trelloCardDto.getName())
                .queryParam("desc", trelloCardDto.getDesc())
                .queryParam("pos", trelloCardDto.getPos())
                .queryParam("idList", trelloCardDto.getIdList())
                .build()
                .encode().toUri();
        return restTemplate.postForObject(url, null, CreatedTrelloCardDto.class);
    }

    public void updateCard(String id, TrelloCardDto trelloCardDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards/" + id)
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("name", trelloCardDto.getName())
                .queryParam("desc", trelloCardDto.getDesc())
                .queryParam("pos", trelloCardDto.getPos())
                .build()
                .encode().toUri();
        restTemplate.put(uri, null);
    }

    public void deleteCard(String id) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards/" + id)
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .build()
                .encode().toUri();
        restTemplate.delete(url);
    }
}
