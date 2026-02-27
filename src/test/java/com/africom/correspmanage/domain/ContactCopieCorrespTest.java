package com.africom.correspmanage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.africom.correspmanage.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactCopieCorrespTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContactCopieCorresp.class);
        ContactCopieCorresp contactCopieCorresp1 = new ContactCopieCorresp();
        contactCopieCorresp1.setId(1L);
        ContactCopieCorresp contactCopieCorresp2 = new ContactCopieCorresp();
        contactCopieCorresp2.setId(contactCopieCorresp1.getId());
        assertThat(contactCopieCorresp1).isEqualTo(contactCopieCorresp2);
        contactCopieCorresp2.setId(2L);
        assertThat(contactCopieCorresp1).isNotEqualTo(contactCopieCorresp2);
        contactCopieCorresp1.setId(null);
        assertThat(contactCopieCorresp1).isNotEqualTo(contactCopieCorresp2);
    }
}
