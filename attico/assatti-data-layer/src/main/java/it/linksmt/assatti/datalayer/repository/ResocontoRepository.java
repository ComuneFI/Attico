package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Resoconto;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Resoconto entity.
 */
public interface ResocontoRepository extends JpaRepository<Resoconto,Long> {
	@Query("select resoconto from Resoconto resoconto where resoconto.sedutaGiunta.id = ?1 AND resoconto.tipo = ?2")
	public Resoconto findBySedutagiuntaIdAndTipo(Long sedutaId, Integer tipo);
	
	@Query("select resoconto from Resoconto resoconto where resoconto.sedutaGiunta.id = ?1 AND resoconto.tipo is null")
	public Resoconto findBySedutagiuntaIdAndTipoNull(Long sedutaId);
	
	@Query("select resoconto from Resoconto resoconto where resoconto.sedutaGiunta.id = ?1")
	public List<Resoconto> findBySedutagiunta(Long sedutaId);
}
