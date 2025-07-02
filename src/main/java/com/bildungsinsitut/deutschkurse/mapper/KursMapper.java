package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface KursMapper {
    KursMapper INSTANCE = Mappers.getMapper(KursMapper.class);

    @Mapping(source = "kurstyp.id", target = "kurstypId")
    @Mapping(source = "kursraum.id", target = "kursraumId")
    KursDto toDto(Kurs kurs);

    @Mapping(source = "kurstypId", target = "kurstyp.id")
    @Mapping(source = "kursraumId", target = "kursraum.id")
    Kurs toEntity(KursDto kursDto);
}