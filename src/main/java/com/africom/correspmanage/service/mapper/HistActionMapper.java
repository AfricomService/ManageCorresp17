package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.HistAction;
import com.africom.correspmanage.service.dto.HistActionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HistAction} and its DTO {@link HistActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface HistActionMapper extends EntityMapper<HistActionDTO, HistAction> {}
