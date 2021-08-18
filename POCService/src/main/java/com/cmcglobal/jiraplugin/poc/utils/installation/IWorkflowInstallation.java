package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;

import java.util.HashMap;

public interface IWorkflowInstallation {
    void Install() throws Exception;

    HashMap<String, JiraWorkflow> getListOfWorkflowCreated();

    HashMap<String, AssignableWorkflowScheme> getListOfWorkflowSchemeCreated();
}
