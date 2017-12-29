package br.com.everycare.repository;

import br.com.everycare.domain.Professional;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Professional entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Professional findByTelegramId(String telegramId);
}
