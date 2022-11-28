package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.News;
import it.linksmt.assatti.datalayer.domain.StatoJob;

/**
 * Spring Data JPA repository for the News entity.
 */
public interface NewsRepository extends JpaRepository<News,Long>, QueryDslPredicateExecutor<News> {
	@Query("select count(*) from News news where news.atto.id = ?1")
	public Integer countNewsOfAttoId(Long attoId);
	
	List<News> findByDestinatarioEsternoIdAndDocumento(Long destEsternoId, DocumentoPdf doc);
	
	List<News> findByStatoIn(List<StatoJob> stati);
	
	@Query(value="select count(*) from (select stato from news where retry_news_id = ?1 or id = ?1 order by data_creazione desc limit ?2) a where stato = 'ERROR'", nativeQuery=true)
	Long countTentativiInvioFalliti(Long notificaId, Long limit);

	@Query("select news from News news where (news.stato = 'DONE' or news.stato = 'RECEIVED') and news.tipoInvio = 'Posta Elettronica Certificata' and :subject like CONCAT('%', news.oggetto,'%') and news.destinazioneNotifica in :toList group by news.tipoDocumento, news.atto, news.destinatarioInterno, news.destinatarioEsternoId, news.beneficiario order by news.dataCreazione desc")
	public List<News> getNewsPecDoneBySubjectAndTo(@Param("subject") String subject, @Param("toList") List<String> toList);
	
}
