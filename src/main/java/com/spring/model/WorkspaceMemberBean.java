package com.spring.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "workspace_member")
public class WorkspaceMemberBean {

    @EmbeddedId
    private WorkspaceMemberId id;

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usersId") // maps to usersId field in WorkspaceMemberId
    @JoinColumn(name = "users_id")
    private UserBean user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workspacesId") // maps to workspacesId field in WorkspaceMemberId
    @JoinColumn(name = "workspaces_id")
    private WorkspaceBean workspace;
}
