package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import javax.annotation.Nullable;
import java.util.Map;

public interface IFieldUtils {
    FieldLayoutScheme createFieldConfigurationScheme(
            String name,
            @Nullable String description,
            @Nullable Map<IssueType, EditableFieldLayout> listOfFieldConfiguration
    ) throws Exception;

    FieldLayoutScheme updateFieldConfigurationScheme(
            FieldLayoutScheme oldScheme,
            @Nullable String name,
            @Nullable String description,
            @Nullable Map<IssueType, EditableFieldLayout> listOfFieldConfiguration
    ) throws Exception;

    EditableFieldLayout createFieldConfiguration(
            String name,
            @Nullable String description,
            @Nullable Map<String, FieldConfigurationOption> options
    ) throws Exception;

    EditableFieldLayout updateFieldConfiguration(
            EditableFieldLayout oldFieldConfiguration,
            String name,
            String description,
            Map<String, FieldConfigurationOption> options
    ) throws Exception;

    void addFieldConfigurationSchemeToProject(FieldLayoutScheme fieldLayoutScheme, Project project);
}
