package com.co.kr.modyeo.api.team.domain.dto.response;

import com.co.kr.modyeo.api.category.domain.dto.response.CategoryResponse;
import com.co.kr.modyeo.api.team.domain.entity.Team;
import com.co.kr.modyeo.api.team.domain.entity.enumerate.ScaleLevel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TeamResponse {

    private Long id;
    private String name;

    private String description;

    private ScaleLevel scaleLevel;

    private List<CategoryResponse> categoryResponseList = new ArrayList<>();


    @Builder(builderClassName = "of",builderMethodName = "of")
    public TeamResponse(Long id, String name,ScaleLevel scaleLevel, String description, List<CategoryResponse> categoryResponses) {
        this.id = id;
        this.name = name;
        this.scaleLevel = scaleLevel;
        this.description = description;
        this.categoryResponseList = categoryResponses;
    }

    public static TeamResponse toRes(Team team) {
        return of()
                .id(team.getId())
                .name(team.getName())
                .scaleLevel(team.getScaleLevel())
                .description(team.getDescription())
                .categoryResponses(team.getCategoryList().stream().map(crewCategory -> CategoryResponse.of()
                        .id(crewCategory.getCategory().getId())
                        .name(crewCategory.getCategory().getName())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
