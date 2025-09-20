package com.spring.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class WorkspaceMemberId implements Serializable {
    private Integer usersId;
    private Integer workspacesId;

    public WorkspaceMemberId() {}

    public WorkspaceMemberId(Integer usersId, Integer workspacesId) {
        this.usersId = usersId;
        this.workspacesId = workspacesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceMemberId)) return false;
        WorkspaceMemberId that = (WorkspaceMemberId) o;
        return Objects.equals(usersId, that.usersId) &&
               Objects.equals(workspacesId, that.workspacesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersId, workspacesId);
    }
}
