package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Bordereaux;
import com.africom.correspmanage.service.dto.BordereauxDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Bordereaux} and its DTO {@link BordereauxDTO}.
 */
@Mapper(componentModel = "spring")
public interface BordereauxMapper extends EntityMapper<BordereauxDTO, Bordereaux> {}
