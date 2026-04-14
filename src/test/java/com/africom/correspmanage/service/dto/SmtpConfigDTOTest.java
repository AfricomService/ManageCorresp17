package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmtpConfigDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmtpConfigDTO.class);
        SmtpConfigDTO smtpConfigDTO1 = new SmtpConfigDTO();
        smtpConfigDTO1.setId(1L);
        SmtpConfigDTO smtpConfigDTO2 = new SmtpConfigDTO();
        assertThat(smtpConfigDTO1).isNotEqualTo(smtpConfigDTO2);
        smtpConfigDTO2.setId(smtpConfigDTO1.getId());
        assertThat(smtpConfigDTO1).isEqualTo(smtpConfigDTO2);
        smtpConfigDTO2.setId(2L);
        assertThat(smtpConfigDTO1).isNotEqualTo(smtpConfigDTO2);
        smtpConfigDTO1.setId(null);
        assertThat(smtpConfigDTO1).isNotEqualTo(smtpConfigDTO2);
    }
}
