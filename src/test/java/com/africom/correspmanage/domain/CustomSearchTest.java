package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomSearchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomSearch.class);
        CustomSearch customSearch1 = new CustomSearch();
        customSearch1.setId(1L);
        CustomSearch customSearch2 = new CustomSearch();
        customSearch2.setId(customSearch1.getId());
        assertThat(customSearch1).isEqualTo(customSearch2);
        customSearch2.setId(2L);
        assertThat(customSearch1).isNotEqualTo(customSearch2);
        customSearch1.setId(null);
        assertThat(customSearch1).isNotEqualTo(customSearch2);
    }
}
