package it.linksmt.assatti.security;

public interface IAttoRetrievalSavingAccessControl {
	public boolean canReadAtto(Long attoId, Long profiloId, String taskBpmId, Long sedutaId);
	public boolean canSaveAtto(Long attoId, Long profiloId, String taskBpmId, Long sedutaId);
}
