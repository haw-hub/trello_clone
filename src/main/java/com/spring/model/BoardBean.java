package com.spring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "board")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BoardBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String title;

    @Column(name = "background_color", length = 7)
    private String background;

    // ✅ Proper ManyToOne mapping with writable foreign key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaces_id", nullable = false)
    @ToString.Exclude
    private WorkspaceBean workspace;

    // ✅ Keep this as helper column (optional)
    @Column(name = "workspaces_id", insertable = false, updatable = false)
    private Integer workspaces_id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ListBean> lists;

    @Transient
    private String currentUserRole;
}
