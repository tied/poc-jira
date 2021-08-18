package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.security.roles.ProjectRole;

import javax.annotation.Nullable;
import java.util.List;

public interface IProjectUtils {
    Project create(String name,
                   String userNameOfLead,
                   String key,
                   String description,
                   ProjectTypeKey projectTypeKey,
                   @Nullable Long assigneeTypeId,
                   @Nullable Long avatarId,
                   @Nullable String url) throws Exception;

    Boolean delete(String key) throws Exception;

    List<ProjectType> getAllProjectType();

    Project update(Project oldProject,
                   @Nullable String name,
                   @Nullable String userNameOfLead,
                   @Nullable String key,
                   @Nullable String description,
                   @Nullable String url,
                   @Nullable Long assigneeType) throws Exception;

    ProjectRole createProjectRole(
            String name,
            @Nullable String description
    ) throws Exception;

    ProjectRole updateProjectRole(
            ProjectRole oldRole,
            @Nullable String name,
            @Nullable String description
    );

    ProjectCategory createProjectCategory(
            String name,
            @Nullable String description
    ) throws Exception;

    void AddProjectToProjectCategory(Project project, ProjectCategory projectCategory);
}
