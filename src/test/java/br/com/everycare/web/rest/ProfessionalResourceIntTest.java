package br.com.everycare.web.rest;

import br.com.everycare.CadastroEverycareApp;

import br.com.everycare.domain.Professional;
import br.com.everycare.repository.ProfessionalRepository;
import br.com.everycare.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.everycare.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProfessionalResource REST controller.
 *
 * @see ProfessionalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CadastroEverycareApp.class)
public class ProfessionalResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TELEGRAM_ID = "AAAAAAAAAA";
    private static final String UPDATED_TELEGRAM_ID = "BBBBBBBBBB";

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProfessionalMockMvc;

    private Professional professional;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProfessionalResource professionalResource = new ProfessionalResource(professionalRepository);
        this.restProfessionalMockMvc = MockMvcBuilders.standaloneSetup(professionalResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Professional createEntity(EntityManager em) {
        Professional professional = new Professional()
            .name(DEFAULT_NAME)
            .telegramId(DEFAULT_TELEGRAM_ID);
        return professional;
    }

    @Before
    public void initTest() {
        professional = createEntity(em);
    }

    @Test
    @Transactional
    public void createProfessional() throws Exception {
        int databaseSizeBeforeCreate = professionalRepository.findAll().size();

        // Create the Professional
        restProfessionalMockMvc.perform(post("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professional)))
            .andExpect(status().isCreated());

        // Validate the Professional in the database
        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeCreate + 1);
        Professional testProfessional = professionalList.get(professionalList.size() - 1);
        assertThat(testProfessional.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProfessional.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
    }

    @Test
    @Transactional
    public void createProfessionalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = professionalRepository.findAll().size();

        // Create the Professional with an existing ID
        professional.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfessionalMockMvc.perform(post("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professional)))
            .andExpect(status().isBadRequest());

        // Validate the Professional in the database
        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = professionalRepository.findAll().size();
        // set the field null
        professional.setName(null);

        // Create the Professional, which fails.

        restProfessionalMockMvc.perform(post("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professional)))
            .andExpect(status().isBadRequest());

        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelegramIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = professionalRepository.findAll().size();
        // set the field null
        professional.setTelegramId(null);

        // Create the Professional, which fails.

        restProfessionalMockMvc.perform(post("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professional)))
            .andExpect(status().isBadRequest());

        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProfessionals() throws Exception {
        // Initialize the database
        professionalRepository.saveAndFlush(professional);

        // Get all the professionalList
        restProfessionalMockMvc.perform(get("/api/professionals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(professional.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID.toString())));
    }

    @Test
    @Transactional
    public void getProfessional() throws Exception {
        // Initialize the database
        professionalRepository.saveAndFlush(professional);

        // Get the professional
        restProfessionalMockMvc.perform(get("/api/professionals/{id}", professional.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(professional.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProfessional() throws Exception {
        // Get the professional
        restProfessionalMockMvc.perform(get("/api/professionals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProfessional() throws Exception {
        // Initialize the database
        professionalRepository.saveAndFlush(professional);
        int databaseSizeBeforeUpdate = professionalRepository.findAll().size();

        // Update the professional
        Professional updatedProfessional = professionalRepository.findOne(professional.getId());
        // Disconnect from session so that the updates on updatedProfessional are not directly saved in db
        em.detach(updatedProfessional);
        updatedProfessional
            .name(UPDATED_NAME)
            .telegramId(UPDATED_TELEGRAM_ID);

        restProfessionalMockMvc.perform(put("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProfessional)))
            .andExpect(status().isOk());

        // Validate the Professional in the database
        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeUpdate);
        Professional testProfessional = professionalList.get(professionalList.size() - 1);
        assertThat(testProfessional.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProfessional.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingProfessional() throws Exception {
        int databaseSizeBeforeUpdate = professionalRepository.findAll().size();

        // Create the Professional

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProfessionalMockMvc.perform(put("/api/professionals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professional)))
            .andExpect(status().isCreated());

        // Validate the Professional in the database
        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProfessional() throws Exception {
        // Initialize the database
        professionalRepository.saveAndFlush(professional);
        int databaseSizeBeforeDelete = professionalRepository.findAll().size();

        // Get the professional
        restProfessionalMockMvc.perform(delete("/api/professionals/{id}", professional.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Professional> professionalList = professionalRepository.findAll();
        assertThat(professionalList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Professional.class);
        Professional professional1 = new Professional();
        professional1.setId(1L);
        Professional professional2 = new Professional();
        professional2.setId(professional1.getId());
        assertThat(professional1).isEqualTo(professional2);
        professional2.setId(2L);
        assertThat(professional1).isNotEqualTo(professional2);
        professional1.setId(null);
        assertThat(professional1).isNotEqualTo(professional2);
    }
}
