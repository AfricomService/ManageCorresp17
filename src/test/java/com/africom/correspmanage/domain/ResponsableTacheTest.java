package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsableTacheTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResponsableTache.class);
        ResponsableTache responsableTache1 = new ResponsableTache();
        responsableTache1.setId(1L);
        ResponsableTache responsableTache2 = new ResponsableTache();
        responsableTache2.setId(responsableTache1.getId());
        assertThat(responsableTache1).isEqualTo(responsableTache2);
        responsableTache2.setId(2L);
        assertThat(responsableTache1).isNotEqualTo(responsableTache2);
        responsableTache1.setId(null);
        assertThat(responsableTache1).isNotEqualTo(responsableTache2);
    }
}
