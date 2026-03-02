package com.africom.correspmanage.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.ContactCopieCorresp;
import com.africom.correspmanage.repository.ContactCopieCorrespRepository;
import com.africom.correspmanage.service.dto.ContactCopieCorrespDTO;
import com.africom.correspmanage.service.mapper.ContactCopieCorrespMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ContactCopieCorrespResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactCopieCorrespResourceIT {

    private static final Long DEFAULT_ID_CORRSP = 1L;
    private static final Long UPDATED_ID_CORRSP = 2L;

    private static final Long DEFAULT_ID_DEPT = 1L;
    private static final Long UPDATED_ID_DEPT = 2L;

    private static final String DEFAULT_CODE_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_CODE_DEPT = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_DEPT = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_DEPT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contact-copie-corresps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ContactCopieCorrespRepository contactCopieCorrespRepository;

    @Autowired
    private ContactCopieCorrespMapper contactCopieCorrespMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactCopieCorrespMockMvc;

    private ContactCopieCorresp contactCopieCorresp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactCopieCorresp createEntity(EntityManager em) {
        ContactCopieCorresp contactCopieCorresp = new ContactCopieCorresp()
            .idCorrsp(DEFAULT_ID_CORRSP)
            .idDept(DEFAULT_ID_DEPT)
            .codeDept(DEFAULT_CODE_DEPT)
            .nomDept(DEFAULT_NOM_DEPT)
            .emailDept(DEFAULT_EMAIL_DEPT);
        return contactCopieCorresp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactCopieCorresp createUpdatedEntity(EntityManager em) {
        ContactCopieCorresp contactCopieCorresp = new ContactCopieCorresp()
            .idCorrsp(UPDATED_ID_CORRSP)
            .idDept(UPDATED_ID_DEPT)
            .codeDept(UPDATED_CODE_DEPT)
            .nomDept(UPDATED_NOM_DEPT)
            .emailDept(UPDATED_EMAIL_DEPT);
        return contactCopieCorresp;
    }

    @BeforeEach
    public void initTest() {
        contactCopieCorresp = createEntity(em);
    }

    @Test
    @Transactional
    void createContactCopieCorresp() throws Exception {
        int databaseSizeBeforeCreate = contactCopieCorrespRepository.findAll().size();
        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);
        restContactCopieCorrespMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeCreate + 1);
        ContactCopieCorresp testContactCopieCorresp = contactCopieCorrespList.get(contactCopieCorrespList.size() - 1);
        assertThat(testContactCopieCorresp.getIdCorrsp()).isEqualTo(DEFAULT_ID_CORRSP);
        assertThat(testContactCopieCorresp.getIdDept()).isEqualTo(DEFAULT_ID_DEPT);
        assertThat(testContactCopieCorresp.getCodeDept()).isEqualTo(DEFAULT_CODE_DEPT);
        assertThat(testContactCopieCorresp.getNomDept()).isEqualTo(DEFAULT_NOM_DEPT);
        assertThat(testContactCopieCorresp.getEmailDept()).isEqualTo(DEFAULT_EMAIL_DEPT);
    }

    @Test
    @Transactional
    void createContactCopieCorrespWithExistingId() throws Exception {
        // Create the ContactCopieCorresp with an existing ID
        contactCopieCorresp.setId(1L);
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        int databaseSizeBeforeCreate = contactCopieCorrespRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactCopieCorrespMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllContactCopieCorresps() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        // Get all the contactCopieCorrespList
        restContactCopieCorrespMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactCopieCorresp.getId().intValue())))
            .andExpect(jsonPath("$.[*].idCorrsp").value(hasItem(DEFAULT_ID_CORRSP.intValue())))
            .andExpect(jsonPath("$.[*].idDept").value(hasItem(DEFAULT_ID_DEPT.intValue())))
            .andExpect(jsonPath("$.[*].codeDept").value(hasItem(DEFAULT_CODE_DEPT)))
            .andExpect(jsonPath("$.[*].nomDept").value(hasItem(DEFAULT_NOM_DEPT)))
            .andExpect(jsonPath("$.[*].emailDept").value(hasItem(DEFAULT_EMAIL_DEPT)));
    }

    @Test
    @Transactional
    void getContactCopieCorresp() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        // Get the contactCopieCorresp
        restContactCopieCorrespMockMvc
            .perform(get(ENTITY_API_URL_ID, contactCopieCorresp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contactCopieCorresp.getId().intValue()))
            .andExpect(jsonPath("$.idCorrsp").value(DEFAULT_ID_CORRSP.intValue()))
            .andExpect(jsonPath("$.idDept").value(DEFAULT_ID_DEPT.intValue()))
            .andExpect(jsonPath("$.codeDept").value(DEFAULT_CODE_DEPT))
            .andExpect(jsonPath("$.nomDept").value(DEFAULT_NOM_DEPT))
            .andExpect(jsonPath("$.emailDept").value(DEFAULT_EMAIL_DEPT));
    }

    @Test
    @Transactional
    void getNonExistingContactCopieCorresp() throws Exception {
        // Get the contactCopieCorresp
        restContactCopieCorrespMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewContactCopieCorresp() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();

        // Update the contactCopieCorresp
        ContactCopieCorresp updatedContactCopieCorresp = contactCopieCorrespRepository.findById(contactCopieCorresp.getId()).get();
        // Disconnect from session so that the updates on updatedContactCopieCorresp are not directly saved in db
        em.detach(updatedContactCopieCorresp);
        updatedContactCopieCorresp
            .idCorrsp(UPDATED_ID_CORRSP)
            .idDept(UPDATED_ID_DEPT)
            .codeDept(UPDATED_CODE_DEPT)
            .nomDept(UPDATED_NOM_DEPT)
            .emailDept(UPDATED_EMAIL_DEPT);
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(updatedContactCopieCorresp);

        restContactCopieCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactCopieCorrespDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isOk());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
        ContactCopieCorresp testContactCopieCorresp = contactCopieCorrespList.get(contactCopieCorrespList.size() - 1);
        assertThat(testContactCopieCorresp.getIdCorrsp()).isEqualTo(UPDATED_ID_CORRSP);
        assertThat(testContactCopieCorresp.getIdDept()).isEqualTo(UPDATED_ID_DEPT);
        assertThat(testContactCopieCorresp.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testContactCopieCorresp.getNomDept()).isEqualTo(UPDATED_NOM_DEPT);
        assertThat(testContactCopieCorresp.getEmailDept()).isEqualTo(UPDATED_EMAIL_DEPT);
    }

    @Test
    @Transactional
    void putNonExistingContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactCopieCorrespDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactCopieCorrespWithPatch() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();

        // Update the contactCopieCorresp using partial update
        ContactCopieCorresp partialUpdatedContactCopieCorresp = new ContactCopieCorresp();
        partialUpdatedContactCopieCorresp.setId(contactCopieCorresp.getId());

        partialUpdatedContactCopieCorresp.idCorrsp(UPDATED_ID_CORRSP).idDept(UPDATED_ID_DEPT).codeDept(UPDATED_CODE_DEPT);

        restContactCopieCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactCopieCorresp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContactCopieCorresp))
            )
            .andExpect(status().isOk());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
        ContactCopieCorresp testContactCopieCorresp = contactCopieCorrespList.get(contactCopieCorrespList.size() - 1);
        assertThat(testContactCopieCorresp.getIdCorrsp()).isEqualTo(UPDATED_ID_CORRSP);
        assertThat(testContactCopieCorresp.getIdDept()).isEqualTo(UPDATED_ID_DEPT);
        assertThat(testContactCopieCorresp.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testContactCopieCorresp.getNomDept()).isEqualTo(DEFAULT_NOM_DEPT);
        assertThat(testContactCopieCorresp.getEmailDept()).isEqualTo(DEFAULT_EMAIL_DEPT);
    }

    @Test
    @Transactional
    void fullUpdateContactCopieCorrespWithPatch() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();

        // Update the contactCopieCorresp using partial update
        ContactCopieCorresp partialUpdatedContactCopieCorresp = new ContactCopieCorresp();
        partialUpdatedContactCopieCorresp.setId(contactCopieCorresp.getId());

        partialUpdatedContactCopieCorresp
            .idCorrsp(UPDATED_ID_CORRSP)
            .idDept(UPDATED_ID_DEPT)
            .codeDept(UPDATED_CODE_DEPT)
            .nomDept(UPDATED_NOM_DEPT)
            .emailDept(UPDATED_EMAIL_DEPT);

        restContactCopieCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactCopieCorresp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContactCopieCorresp))
            )
            .andExpect(status().isOk());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
        ContactCopieCorresp testContactCopieCorresp = contactCopieCorrespList.get(contactCopieCorrespList.size() - 1);
        assertThat(testContactCopieCorresp.getIdCorrsp()).isEqualTo(UPDATED_ID_CORRSP);
        assertThat(testContactCopieCorresp.getIdDept()).isEqualTo(UPDATED_ID_DEPT);
        assertThat(testContactCopieCorresp.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testContactCopieCorresp.getNomDept()).isEqualTo(UPDATED_NOM_DEPT);
        assertThat(testContactCopieCorresp.getEmailDept()).isEqualTo(UPDATED_EMAIL_DEPT);
    }

    @Test
    @Transactional
    void patchNonExistingContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contactCopieCorrespDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContactCopieCorresp() throws Exception {
        int databaseSizeBeforeUpdate = contactCopieCorrespRepository.findAll().size();
        contactCopieCorresp.setId(count.incrementAndGet());

        // Create the ContactCopieCorresp
        ContactCopieCorrespDTO contactCopieCorrespDTO = contactCopieCorrespMapper.toDto(contactCopieCorresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactCopieCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contactCopieCorrespDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactCopieCorresp in the database
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContactCopieCorresp() throws Exception {
        // Initialize the database
        contactCopieCorrespRepository.saveAndFlush(contactCopieCorresp);

        int databaseSizeBeforeDelete = contactCopieCorrespRepository.findAll().size();

        // Delete the contactCopieCorresp
        restContactCopieCorrespMockMvc
            .perform(delete(ENTITY_API_URL_ID, contactCopieCorresp.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ContactCopieCorresp> contactCopieCorrespList = contactCopieCorrespRepository.findAll();
        assertThat(contactCopieCorrespList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
