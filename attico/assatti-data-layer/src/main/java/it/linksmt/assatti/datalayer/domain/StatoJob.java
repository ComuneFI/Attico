package it.linksmt.assatti.datalayer.domain;

public enum StatoJob {
	NEW, IN_PROGRESS, DONE, ERROR, CANCELED, FORCED_SENDING,
	RECEIVED, DELIVERED, SENDED_WITH_ERROR, FAILED, UPDATED, WAITING_INFO, IN_PROGRESS_WAITING, ERROR_WAITING, DISABLED;
}