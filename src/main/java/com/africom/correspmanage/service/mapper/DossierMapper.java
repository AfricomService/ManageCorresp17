package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Dossier;
import com.africom.correspmanage.service.dto.DossierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dossier} and its DTO {@link DossierDTO}.
 */
@Mapper(componentModel = "spring")
public interface DossierMapper extends EntityMapper<DossierDTO, Dossier> {}
