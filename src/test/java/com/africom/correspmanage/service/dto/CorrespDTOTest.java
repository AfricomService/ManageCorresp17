package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CorrespDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CorrespDTO.class);
        CorrespDTO correspDTO1 = new CorrespDTO();
        correspDTO1.setId(1L);
        CorrespDTO correspDTO2 = new CorrespDTO();
        assertThat(correspDTO1).isNotEqualTo(correspDTO2);
        correspDTO2.setId(correspDTO1.getId());
        assertThat(correspDTO1).isEqualTo(correspDTO2);
        correspDTO2.setId(2L);
        assertThat(correspDTO1).isNotEqualTo(correspDTO2);
        correspDTO1.setId(null);
        assertThat(correspDTO1).isNotEqualTo(correspDTO2);
    }
}
