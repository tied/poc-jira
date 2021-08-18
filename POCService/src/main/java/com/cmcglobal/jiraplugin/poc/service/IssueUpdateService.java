package com.cmcglobal.jiraplugin.poc.service;


import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IssueUpdateService {

    WorkflowManager _workflowManager = ComponentAccessor.getWorkflowManager();
    JiraAuthenticationContext _authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    IssueService _issueService = ComponentAccessor.getIssueService();

    public boolean executeWorkflowTransition(Issue issue, String transitionName, String toStatusName, ApplicationUser userExecute) {
        try {
            if (issue.getStatus().getName().equalsIgnoreCase(toStatusName))
                return false;
            JiraWorkflow workflow = _workflowManager.getWorkflow(issue);
            if (userExecute == null){
                userExecute = _authenticationContext.getLoggedInUser();
            }
            List<Status> statuses = workflow.getLinkedStatusObjects();
            List<StepDescriptor> stepDescriptors = workflow.getDescriptor().getSteps();
            StepDescriptor resultStep = stepDescriptors.stream().filter(e -> e.getName().equalsIgnoreCase(toStatusName)).findFirst().get();
            Status inactiveWorkflowStatus = statuses.stream().filter(e -> e.getName().equalsIgnoreCase(toStatusName)).findFirst().get();
            Collection<ActionDescriptor> actions = workflow.getActionsWithResult(resultStep);
            List<ActionDescriptor> inactiveAction = actions.stream().filter(e -> e.getName().equalsIgnoreCase(transitionName)).collect(Collectors.toList());
            for (ActionDescriptor actionDescriptor : inactiveAction) {
                IssueInputParameters inputParameters = _issueService.newIssueInputParameters();
                inputParameters.setStatusId(inactiveWorkflowStatus.getId());
                IssueService.TransitionValidationResult validateResult = _issueService.validateTransition(userExecute, issue.getId(), actionDescriptor.getId(), inputParameters);
                if (!validateResult.isValid())
                    continue;
                IssueService.IssueResult result = _issueService.transition(userExecute, validateResult);
                return result.isValid();
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
}

