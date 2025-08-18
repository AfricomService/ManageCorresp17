package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PjTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pj.class);
        Pj pj1 = new Pj();
        pj1.setId(1L);
        Pj pj2 = new Pj();
        pj2.setId(pj1.getId());
        assertThat(pj1).isEqualTo(pj2);
        pj2.setId(2L);
        assertThat(pj1).isNotEqualTo(pj2);
        pj1.setId(null);
        assertThat(pj1).isNotEqualTo(pj2);
    }
}
