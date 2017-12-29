package br.com.everycare.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.everycare.domain.Professional;

import br.com.everycare.repository.ProfessionalRepository;
import br.com.everycare.web.rest.errors.BadRequestAlertException;
import br.com.everycare.web.rest.util.HeaderUtil;
import br.com.everycare.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Professional.
 */
@RestController
@RequestMapping("/api")
public class ProfessionalResource {

    private final Logger log = LoggerFactory.getLogger(ProfessionalResource.class);

    private static final String ENTITY_NAME = "professional";

    private final ProfessionalRepository professionalRepository;

    public ProfessionalResource(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    /**
     * POST  /professionals : Create a new professional.
     *
     * @param professional the professional to create
     * @return the ResponseEntity with status 201 (Created) and with body the new professional, or with status 400 (Bad Request) if the professional has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/professionals")
    @Timed
    public ResponseEntity<Professional> createProfessional(@Valid @RequestBody Professional professional) throws URISyntaxException {
        log.debug("REST request to save Professional : {}", professional);
        if (professional.getId() != null) {
            throw new BadRequestAlertException("A new professional cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Professional result = professionalRepository.save(professional);
        return ResponseEntity.created(new URI("/api/professionals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /professionals : Updates an existing professional.
     *
     * @param professional the professional to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated professional,
     * or with status 400 (Bad Request) if the professional is not valid,
     * or with status 500 (Internal Server Error) if the professional couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/professionals")
    @Timed
    public ResponseEntity<Professional> updateProfessional(@Valid @RequestBody Professional professional) throws URISyntaxException {
        log.debug("REST request to update Professional : {}", professional);
        if (professional.getId() == null) {
            return createProfessional(professional);
        }
        Professional result = professionalRepository.save(professional);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, professional.getId().toString()))
            .body(result);
    }

    /**
     * GET  /professionals : get all the professionals.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of professionals in body
     */
    @GetMapping("/professionals")
    @Timed
    public ResponseEntity<List<Professional>> getAllProfessionals(Pageable pageable) {
        log.debug("REST request to get a page of Professionals");
        Page<Professional> page = professionalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/professionals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /professionals/:id : get the "id" professional.
     *
     * @param id the id of the professional to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the professional, or with status 404 (Not Found)
     */
    @GetMapping("/professionals/{id}")
    @Timed
    public ResponseEntity<Professional> getProfessional(@PathVariable Long id) {
        log.debug("REST request to get Professional : {}", id);
        Professional professional = professionalRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(professional));
    }

    /**
     * DELETE  /professionals/:id : delete the "id" professional.
     *
     * @param id the id of the professional to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/professionals/{id}")
    @Timed
    public ResponseEntity<Void> deleteProfessional(@PathVariable Long id) {
        log.debug("REST request to delete Professional : {}", id);
        professionalRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
