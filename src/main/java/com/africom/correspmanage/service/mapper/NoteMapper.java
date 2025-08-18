package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Note;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.service.dto.NoteDTO;
import com.africom.correspmanage.service.dto.PjDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Note} and its DTO {@link NoteDTO}.
 */
@Mapper(componentModel = "spring")
public interface NoteMapper extends EntityMapper<NoteDTO, Note> {
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "pjId")
    NoteDTO toDto(Note s);

    @Named("pjId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PjDTO toDtoPjId(Pj pj);
}
