package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;

import java.util.List;
import java.util.Map;

public interface IWorkflowUtils {
    JiraWorkflow ImportFromXMLFile(String pathOfFile, String workflowName) throws Exception;

    AssignableWorkflowScheme createWorkflowScheme(String name, String description, JiraWorkflow workflow, List<String> issueTypeIds) throws Exception;

    void addWorkflowSchemeToProject(AssignableWorkflowScheme workflowScheme, Project project);

    JiraWorkflow getWorkflowByName(String name) throws Exception;

    Boolean lockWorkflow(JiraWorkflow workflowToLock);

    Boolean lockWorkflowScheme(AssignableWorkflowScheme workflowSchemeToLock);

    void addWorkflowToScheme(JiraWorkflow workflow, AssignableWorkflowScheme workflowScheme, List<String> issueTypeId);

    JiraWorkflow runImportFromJWBFile(String workflowName, String pathJWBFile) throws Exception;

    JiraWorkflow addPostFunctionToTransition(
            JiraWorkflow workflow,
            String transitionName,
            String toStep,
            String postFunctionKey,
            Class<?> postFunctionClass,
            Map<String, ?> passParams) throws Exception;

    JiraWorkflow createBackupAndPublicWorkflow(String workflowName, boolean isCloneBackup) throws Exception;

    public JiraWorkflow addValidatorToTransition(JiraWorkflow workflow,
                                                 String transitionName,
                                                 String toStep,
                                                 Class<?> validatorClass,
                                                 Map<String, ?> passParams) throws Exception;

    public JiraWorkflow addConditionToTransition(JiraWorkflow workflow, String transitionName, String toStep, Class<?> conditionClass) throws Exception;

}
