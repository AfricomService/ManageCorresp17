package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BordereauxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bordereaux.class);
        Bordereaux bordereaux1 = new Bordereaux();
        bordereaux1.setId(1L);
        Bordereaux bordereaux2 = new Bordereaux();
        bordereaux2.setId(bordereaux1.getId());
        assertThat(bordereaux1).isEqualTo(bordereaux2);
        bordereaux2.setId(2L);
        assertThat(bordereaux1).isNotEqualTo(bordereaux2);
        bordereaux1.setId(null);
        assertThat(bordereaux1).isNotEqualTo(bordereaux2);
    }
}
