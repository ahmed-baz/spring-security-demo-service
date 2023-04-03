package com.demo.skyros.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;


@Setter
@Getter
@ToString
@Embeddable
public class EntityAudit implements Serializable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @DateTimeFormat(pattern = "dd-M-yyyy hh:mm:ss")
    private Date createdDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    private Long createdById;

    @LastModifiedDate
    @DateTimeFormat(pattern = "dd-M-yyyy hh:mm:ss")
    private Date lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;

    private Long lastModifiedById;

}
