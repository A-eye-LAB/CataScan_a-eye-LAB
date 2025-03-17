package org.cataract.web.domain;

public enum Role {
    USER,
    MANAGER,
    ADMIN,
    DELETED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}