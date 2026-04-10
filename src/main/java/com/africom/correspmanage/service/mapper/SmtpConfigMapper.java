package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.SmtpConfig;
import com.africom.correspmanage.service.dto.SmtpConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SmtpConfig} and its DTO {@link SmtpConfigDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SmtpConfigMapper extends EntityMapper<SmtpConfigDTO, SmtpConfig> {}
