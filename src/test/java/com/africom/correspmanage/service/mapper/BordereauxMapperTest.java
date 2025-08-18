package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BordereauxMapperTest {

    private BordereauxMapper bordereauxMapper;

    @BeforeEach
    public void setUp() {
        bordereauxMapper = new BordereauxMapperImpl();
    }
}
