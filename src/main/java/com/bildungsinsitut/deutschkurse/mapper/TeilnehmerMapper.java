package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.TeilnehmerDto;
import com.bildungsinsitut.deutschkurse.model.Teilnehmer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeilnehmerMapper {

    TeilnehmerDto toDto(Teilnehmer teilnehmer);
    Teilnehmer toEntity(TeilnehmerDto teilnehmerDto);
    List<TeilnehmerDto> toDtoList(List<Teilnehmer> teilnehmer);
}