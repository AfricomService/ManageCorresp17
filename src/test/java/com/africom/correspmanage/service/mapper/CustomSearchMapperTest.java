package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomSearchMapperTest {

    private CustomSearchMapper customSearchMapper;

    @BeforeEach
    public void setUp() {
        customSearchMapper = new CustomSearchMapperImpl();
    }
}
