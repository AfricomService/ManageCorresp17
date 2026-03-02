package com.africom.correspmanage.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactCopieCorrespDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContactCopieCorrespDTO.class);
        ContactCopieCorrespDTO contactCopieCorrespDTO1 = new ContactCopieCorrespDTO();
        contactCopieCorrespDTO1.setId(1L);
        ContactCopieCorrespDTO contactCopieCorrespDTO2 = new ContactCopieCorrespDTO();
        assertThat(contactCopieCorrespDTO1).isNotEqualTo(contactCopieCorrespDTO2);
        contactCopieCorrespDTO2.setId(contactCopieCorrespDTO1.getId());
        assertThat(contactCopieCorrespDTO1).isEqualTo(contactCopieCorrespDTO2);
        contactCopieCorrespDTO2.setId(2L);
        assertThat(contactCopieCorrespDTO1).isNotEqualTo(contactCopieCorrespDTO2);
        contactCopieCorrespDTO1.setId(null);
        assertThat(contactCopieCorrespDTO1).isNotEqualTo(contactCopieCorrespDTO2);
    }
}
