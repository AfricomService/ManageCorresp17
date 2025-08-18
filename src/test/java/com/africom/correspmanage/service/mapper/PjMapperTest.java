package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PjMapperTest {

    private PjMapper pjMapper;

    @BeforeEach
    public void setUp() {
        pjMapper = new PjMapperImpl();
    }
}
