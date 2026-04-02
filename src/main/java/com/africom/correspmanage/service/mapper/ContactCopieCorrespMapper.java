package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.ContactCopieCorresp;
import com.africom.correspmanage.service.dto.ContactCopieCorrespDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ContactCopieCorresp} and its DTO {@link ContactCopieCorrespDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ContactCopieCorrespMapper extends EntityMapper<ContactCopieCorrespDTO, ContactCopieCorresp> {}
