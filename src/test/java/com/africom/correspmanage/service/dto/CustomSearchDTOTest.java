package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomSearchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomSearchDTO.class);
        CustomSearchDTO customSearchDTO1 = new CustomSearchDTO();
        customSearchDTO1.setId(1L);
        CustomSearchDTO customSearchDTO2 = new CustomSearchDTO();
        assertThat(customSearchDTO1).isNotEqualTo(customSearchDTO2);
        customSearchDTO2.setId(customSearchDTO1.getId());
        assertThat(customSearchDTO1).isEqualTo(customSearchDTO2);
        customSearchDTO2.setId(2L);
        assertThat(customSearchDTO1).isNotEqualTo(customSearchDTO2);
        customSearchDTO1.setId(null);
        assertThat(customSearchDTO1).isNotEqualTo(customSearchDTO2);
    }
}
