package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PjDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PjDTO.class);
        PjDTO pjDTO1 = new PjDTO();
        pjDTO1.setId(1L);
        PjDTO pjDTO2 = new PjDTO();
        assertThat(pjDTO1).isNotEqualTo(pjDTO2);
        pjDTO2.setId(pjDTO1.getId());
        assertThat(pjDTO1).isEqualTo(pjDTO2);
        pjDTO2.setId(2L);
        assertThat(pjDTO1).isNotEqualTo(pjDTO2);
        pjDTO1.setId(null);
        assertThat(pjDTO1).isNotEqualTo(pjDTO2);
    }
}
