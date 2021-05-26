package com.moss.javatest.shared.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * ValueObject를 Id로 가지는 Entity의 Base Class
 * 최초 Entity 생성 후, save(INSERT)시 존재여부 조회 없이 save(INSERT)를 처리하기 위한 Base Class
 * DB에 저장 하였거나, 조회한 경우(@PostPersist, PostLoad) isNew를 false로 설정하여 DB에 존재하는 값임을 알림
 * @param <ID>
 */
@MappedSuperclass
public abstract class ValueObjectIdEntity<ID extends Serializable> implements Persistable<ID> {
    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    private void markNotNew() {
        this.isNew = false;
    }
}
