package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

/**
 * A RuntimeOperation.
 */
@Entity
@Table(name = "runtime_operation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RuntimeOperation implements Serializable {

	private static final long serialVersionUID = -1819785502009249821L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "task")
	private RuntimeOperationTaskEnum task;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@Column(name = "time")
    private LocalDateTime time;
	
	@Column(name = "profilo_id")
	private Long profiloId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RuntimeOperationTaskEnum getTask() {
		return task;
	}

	public void setTask(RuntimeOperationTaskEnum task) {
		this.task = task;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Long getProfiloId() {
		return profiloId;
	}

	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((profiloId == null) ? 0 : profiloId.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuntimeOperation other = (RuntimeOperation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (profiloId == null) {
			if (other.profiloId != null)
				return false;
		} else if (!profiloId.equals(other.profiloId))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RuntimeOperation [id=" + id + ", task=" + task + ", time=" + time + ", profiloId=" + profiloId + "]";
	}
}
