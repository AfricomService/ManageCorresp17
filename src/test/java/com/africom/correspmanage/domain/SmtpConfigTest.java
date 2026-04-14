package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmtpConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmtpConfig.class);
        SmtpConfig smtpConfig1 = new SmtpConfig();
        smtpConfig1.setId(1L);
        SmtpConfig smtpConfig2 = new SmtpConfig();
        smtpConfig2.setId(smtpConfig1.getId());
        assertThat(smtpConfig1).isEqualTo(smtpConfig2);
        smtpConfig2.setId(2L);
        assertThat(smtpConfig1).isNotEqualTo(smtpConfig2);
        smtpConfig1.setId(null);
        assertThat(smtpConfig1).isNotEqualTo(smtpConfig2);
    }
}
