package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.cmcglobal.jiraplugin.poc.utils.IWorkflowUtils;

import java.util.HashMap;

public abstract class AbstractWorkflowInstallation implements IWorkflowInstallation {
    protected final HashMap<String, JiraWorkflow> _lisOfWorkflowCreated;
    protected final HashMap<String, AssignableWorkflowScheme> _lisOfSchemeCreated;
    protected IWorkflowUtils _workflowUtils;
    protected IProjectInstallation _projectInstallation;

    public AbstractWorkflowInstallation(
            IWorkflowUtils workflowUtils,
            IProjectInstallation projectInstallation
    ){
        _workflowUtils = workflowUtils;
        _projectInstallation = projectInstallation;
        _lisOfSchemeCreated = new HashMap<String, AssignableWorkflowScheme>();
        _lisOfWorkflowCreated = new HashMap<String, JiraWorkflow>();
    }

    public abstract void Install() throws Exception;

    public HashMap<String, JiraWorkflow> getListOfWorkflowCreated() {
        return _lisOfWorkflowCreated;
    }

    public HashMap<String, AssignableWorkflowScheme> getListOfWorkflowSchemeCreated() {
        return _lisOfSchemeCreated;
    }
}
