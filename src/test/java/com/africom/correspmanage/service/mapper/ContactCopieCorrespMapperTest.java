package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactCopieCorrespMapperTest {

    private ContactCopieCorrespMapper contactCopieCorrespMapper;

    @BeforeEach
    public void setUp() {
        contactCopieCorrespMapper = new ContactCopieCorrespMapperImpl();
    }
}
