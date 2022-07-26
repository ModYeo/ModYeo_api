package com.co.kr.modyeo.api.team.domain.entity.link;

import com.co.kr.modyeo.api.team.domain.entity.Team;
import com.co.kr.modyeo.common.entity.BaseEntity;
import com.co.kr.modyeo.api.category.domain.entity.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "TEAM_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamCategory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder(builderClassName = "of", builderMethodName = "of")
    public TeamCategory(Long id, Team team, Category category) {
        this.id = id;
        this.team = team;
        this.category = category;
    }
}
