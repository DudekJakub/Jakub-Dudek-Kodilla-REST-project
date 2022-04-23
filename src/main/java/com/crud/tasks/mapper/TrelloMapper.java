package com.crud.tasks.mapper;

import com.crud.tasks.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrelloMapper {

    public TrelloBoard mapToBoard(final TrelloBoardDto trelloBoardDto) {
        return TrelloBoard.builder()
                .id(trelloBoardDto.getId())
                .name(trelloBoardDto.getName())
                .lists(mapToList(trelloBoardDto.getLists()))
                .build();
    }

    public List<TrelloBoard> mapToBoards(final List<TrelloBoardDto> trelloBoardDto) {
        return trelloBoardDto.stream().map(tBDto -> TrelloBoard.builder()
                                                                    .id(tBDto.getId())
                                                                    .name(tBDto.getName())
                                                                    .lists(mapToList(tBDto.getLists()))
                                                                    .build())
                                                                    .collect(Collectors.toList());
    }

    public List<TrelloBoardDto> mapToBoardsDto(final List<TrelloBoard> trelloBoards) {
        return trelloBoards.stream()
                .map(tB -> new TrelloBoardDto(tB.getId(), tB.getName(), mapToListDto(tB.getLists())))
                .collect(Collectors.toList());
    }

    public List<TrelloCard> mapToCardList(final List<TrelloCardDto> trelloCardDto) {
        return trelloCardDto.stream()
                .map(tCDto -> new TrelloCard(tCDto.getId(), tCDto.getName(), tCDto.getDesc(), tCDto.getPos(), tCDto.getIdList()))
                .collect(Collectors.toList());
    }

    public List<TrelloCardDto> mapToCardDtoList(final List<TrelloCard> trelloCards) {
        return trelloCards.stream()
                .map(tC -> new TrelloCardDto(tC.getId(), tC.getName(), tC.getDesc(), tC.getPos(), tC.getIdList()))
                .collect(Collectors.toList());
    }

    public List<TrelloList> mapToList(final List<TrelloListDto> trelloListDto) {
        return trelloListDto.stream()
                .map(trelloList -> new TrelloList(trelloList.getId(), trelloList.getName(), trelloList.isClosed(), this.mapToCardList(trelloList.getCardDtoList())))
                .collect(Collectors.toList());
    }

    public List<TrelloListDto> mapToListDto(final List<TrelloList> trelloLists) {
        return trelloLists.stream()
                .map(tL -> new TrelloListDto(tL.getId(), tL.getName(), tL.isClosed(), this.mapToCardDtoList(tL.getCardList())))
                .collect(Collectors.toList());
    }

    public TrelloCardDto mapToCardDto (final TrelloCard trelloCard) {
        return TrelloCardDto.builder()
                .id(trelloCard.getId())
                .name(trelloCard.getName())
                .desc(trelloCard.getDesc())
                .pos(trelloCard.getPos())
                .idList(trelloCard.getIdList())
                .build();
    }

    public TrelloCard mapToCard (final TrelloCardDto trelloCardDto) {
        return TrelloCard.builder()
                .id(trelloCardDto.getId())
                .name(trelloCardDto.getName())
                .desc(trelloCardDto.getDesc())
                .pos(trelloCardDto.getPos())
                .idList(trelloCardDto.getIdList())
                .build();
    }

}
