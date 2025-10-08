package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.CustomSearch;
import com.africom.correspmanage.service.dto.CustomSearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomSearch} and its DTO {@link CustomSearchDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomSearchMapper extends EntityMapper<CustomSearchDTO, CustomSearch> {}
