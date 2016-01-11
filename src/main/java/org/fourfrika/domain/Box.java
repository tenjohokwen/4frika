package org.fourfrika.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "box")
@SQLDelete(sql="UPDATE box SET deleted = '1' WHERE id = ?")
@Where(clause="deleted <> '1'")
public class Box implements Serializable {
    @Id
    private Long id;

    @Column(name = "label")
    private String label;

    private int deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Box{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
