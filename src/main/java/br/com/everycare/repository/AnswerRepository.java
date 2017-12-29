package br.com.everycare.repository;

import br.com.everycare.domain.Answer;
import br.com.everycare.domain.Professional;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Answer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByProfessional(Professional professional);
}
