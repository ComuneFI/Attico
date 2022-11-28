package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.linksmt.assatti.datalayer.domain.RiassegnazioneIncarico;

public interface RiassegnazioneIncaricoRepository  extends JpaRepository<RiassegnazioneIncarico, Long> {
	
	RiassegnazioneIncarico findByTaskId(String taskId);
}
