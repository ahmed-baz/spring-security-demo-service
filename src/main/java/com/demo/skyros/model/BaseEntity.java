package com.demo.skyros.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


/**
 * this class is base for all entities in the system
 * it will contain basic features common between all entities
 */
@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Embedded
    private EntityAudit audit = new EntityAudit();

    public abstract Long getId();

    public abstract void setId(Long id);

}
