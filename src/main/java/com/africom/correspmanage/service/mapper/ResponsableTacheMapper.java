package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResponsableTache} and its DTO {@link ResponsableTacheDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResponsableTacheMapper extends EntityMapper<ResponsableTacheDTO, ResponsableTache> {}
