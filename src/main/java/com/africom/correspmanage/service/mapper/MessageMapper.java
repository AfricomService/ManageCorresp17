package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.service.dto.MessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {}
