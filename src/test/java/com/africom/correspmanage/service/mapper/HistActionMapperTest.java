package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistActionMapperTest {

    private HistActionMapper histActionMapper;

    @BeforeEach
    public void setUp() {
        histActionMapper = new HistActionMapperImpl();
    }
}
