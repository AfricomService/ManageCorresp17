package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmtpConfigMapperTest {

    private SmtpConfigMapper smtpConfigMapper;

    @BeforeEach
    public void setUp() {
        smtpConfigMapper = new SmtpConfigMapperImpl();
    }
}
