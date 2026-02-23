package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsableTacheDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResponsableTacheDTO.class);
        ResponsableTacheDTO responsableTacheDTO1 = new ResponsableTacheDTO();
        responsableTacheDTO1.setId(1L);
        ResponsableTacheDTO responsableTacheDTO2 = new ResponsableTacheDTO();
        assertThat(responsableTacheDTO1).isNotEqualTo(responsableTacheDTO2);
        responsableTacheDTO2.setId(responsableTacheDTO1.getId());
        assertThat(responsableTacheDTO1).isEqualTo(responsableTacheDTO2);
        responsableTacheDTO2.setId(2L);
        assertThat(responsableTacheDTO1).isNotEqualTo(responsableTacheDTO2);
        responsableTacheDTO1.setId(null);
        assertThat(responsableTacheDTO1).isNotEqualTo(responsableTacheDTO2);
    }
}
