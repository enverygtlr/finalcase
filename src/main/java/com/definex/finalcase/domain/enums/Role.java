package com.definex.finalcase.domain.enums;

public enum Role {
    PROJECT_MANAGER, TEAM_LEADER, TEAM_MEMBER;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}

