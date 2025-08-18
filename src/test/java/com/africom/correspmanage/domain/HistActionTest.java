package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistAction.class);
        HistAction histAction1 = new HistAction();
        histAction1.setId(1L);
        HistAction histAction2 = new HistAction();
        histAction2.setId(histAction1.getId());
        assertThat(histAction1).isEqualTo(histAction2);
        histAction2.setId(2L);
        assertThat(histAction1).isNotEqualTo(histAction2);
        histAction1.setId(null);
        assertThat(histAction1).isNotEqualTo(histAction2);
    }
}
