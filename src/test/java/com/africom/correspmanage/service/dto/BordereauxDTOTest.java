package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BordereauxDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BordereauxDTO.class);
        BordereauxDTO bordereauxDTO1 = new BordereauxDTO();
        bordereauxDTO1.setId(1L);
        BordereauxDTO bordereauxDTO2 = new BordereauxDTO();
        assertThat(bordereauxDTO1).isNotEqualTo(bordereauxDTO2);
        bordereauxDTO2.setId(bordereauxDTO1.getId());
        assertThat(bordereauxDTO1).isEqualTo(bordereauxDTO2);
        bordereauxDTO2.setId(2L);
        assertThat(bordereauxDTO1).isNotEqualTo(bordereauxDTO2);
        bordereauxDTO1.setId(null);
        assertThat(bordereauxDTO1).isNotEqualTo(bordereauxDTO2);
    }
}
