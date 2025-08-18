package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorrespMapperTest {

    private CorrespMapper correspMapper;

    @BeforeEach
    public void setUp() {
        correspMapper = new CorrespMapperImpl();
    }
}
