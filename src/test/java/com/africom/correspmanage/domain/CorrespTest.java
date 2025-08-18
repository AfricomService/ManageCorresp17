package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CorrespTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Corresp.class);
        Corresp corresp1 = new Corresp();
        corresp1.setId(1L);
        Corresp corresp2 = new Corresp();
        corresp2.setId(corresp1.getId());
        assertThat(corresp1).isEqualTo(corresp2);
        corresp2.setId(2L);
        assertThat(corresp1).isNotEqualTo(corresp2);
        corresp1.setId(null);
        assertThat(corresp1).isNotEqualTo(corresp2);
    }
}
