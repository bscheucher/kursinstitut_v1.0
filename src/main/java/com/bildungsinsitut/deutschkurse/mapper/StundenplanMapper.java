package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.StundenplanDto;
import com.bildungsinsitut.deutschkurse.model.Stundenplan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StundenplanMapper {

    @Mapping(source = "kurs.id", target = "kursId")
    @Mapping(source = "kurs.kursName", target = "kursName")
    StundenplanDto toDto(Stundenplan stundenplan);

    @Mapping(source = "kursId", target = "kurs.id")
    @Mapping(target = "kurs", ignore = true) // Will be set by service
    @Mapping(target = "erstelltAm", ignore = true)
    Stundenplan toEntity(StundenplanDto stundenplanDto);

    List<StundenplanDto> toDtoList(List<Stundenplan> stundenplanList);
}