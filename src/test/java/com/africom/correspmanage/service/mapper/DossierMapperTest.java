package com.africom.correspmanage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DossierMapperTest {

    private DossierMapper dossierMapper;

    @BeforeEach
    public void setUp() {
        dossierMapper = new DossierMapperImpl();
    }
}
