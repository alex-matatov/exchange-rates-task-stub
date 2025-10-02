package com.shipmonk.testingday.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;

import java.io.Serializable;

@MappedSuperclass
@Getter
public class BaseEntity implements Serializable {
    @Version
    private Long version;
}
