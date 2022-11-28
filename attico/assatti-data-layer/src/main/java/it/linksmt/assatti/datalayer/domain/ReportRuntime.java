package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ReportRuntime.
 */
@Entity
@Table(name = "REPORTRUNTIME")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReportRuntime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Type(type="org.hibernate.type.StringClobType")
    @Column(name = "html")
    private String html;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReportRuntime reportRuntime = (ReportRuntime) o;

        if ( ! Objects.equals(id, reportRuntime.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ReportRuntime{" +
                "id=" + id +
                ", html='" + html + "'" +
                '}';
    }
}
