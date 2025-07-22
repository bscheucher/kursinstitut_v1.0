package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.AnwesenheitDto;
import com.bildungsinsitut.deutschkurse.model.Anwesenheit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnwesenheitMapper {

    @Mapping(source = "teilnehmer.id", target = "teilnehmerId")
    @Mapping(source = "teilnehmer", target = "teilnehmerName", qualifiedByName = "teilnehmerToFullName")
    @Mapping(source = "kurs.id", target = "kursId")
    @Mapping(source = "kurs.kursName", target = "kursName")
    AnwesenheitDto toDto(Anwesenheit anwesenheit);

    @Mapping(source = "teilnehmerId", target = "teilnehmer.id")
    @Mapping(source = "kursId", target = "kurs.id")
    @Mapping(target = "teilnehmer", ignore = true) // Will be set by service
    @Mapping(target = "kurs", ignore = true) // Will be set by service
    @Mapping(target = "erfasstAm", ignore = true)
    Anwesenheit toEntity(AnwesenheitDto anwesenheitDto);

    List<AnwesenheitDto> toDtoList(List<Anwesenheit> anwesenheitList);

    @Named("teilnehmerToFullName")
    default String teilnehmerToFullName(com.bildungsinsitut.deutschkurse.model.Teilnehmer teilnehmer) {
        if (teilnehmer == null) {
            return null;
        }
        return teilnehmer.getVorname() + " " + teilnehmer.getNachname();
    }
}