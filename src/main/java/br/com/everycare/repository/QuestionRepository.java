package br.com.everycare.repository;

import br.com.everycare.domain.Question;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Question entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findTop1ByOrderByOrdNumAsc();

    Question findTop1ByOrdNumGreaterThanOrderByOrdNumAsc(Integer integer);
}
