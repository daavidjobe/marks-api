package com.marks.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import org.mongodb.morphia.annotations.Version;

import java.sql.Timestamp;
import java.util.Date;

public abstract class BaseEntity {

    @Id
    protected ObjectId id;

    @Version
    private Long version;

    protected Timestamp creationDate;
    protected Timestamp lastChange;

    @PrePersist
    public void prePersist() {
        creationDate = (creationDate == null) ? new Timestamp(new Date().getTime()) : creationDate;
        lastChange = (lastChange == null) ? creationDate : new Timestamp(new Date().getTime());
    }

    public BaseEntity() {
        super();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

}