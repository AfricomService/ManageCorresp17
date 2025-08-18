package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.service.dto.PjDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pj} and its DTO {@link PjDTO}.
 */
@Mapper(componentModel = "spring")
public interface PjMapper extends EntityMapper<PjDTO, Pj> {}
