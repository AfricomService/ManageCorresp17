package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistActionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistActionDTO.class);
        HistActionDTO histActionDTO1 = new HistActionDTO();
        histActionDTO1.setId(1L);
        HistActionDTO histActionDTO2 = new HistActionDTO();
        assertThat(histActionDTO1).isNotEqualTo(histActionDTO2);
        histActionDTO2.setId(histActionDTO1.getId());
        assertThat(histActionDTO1).isEqualTo(histActionDTO2);
        histActionDTO2.setId(2L);
        assertThat(histActionDTO1).isNotEqualTo(histActionDTO2);
        histActionDTO1.setId(null);
        assertThat(histActionDTO1).isNotEqualTo(histActionDTO2);
    }
}
