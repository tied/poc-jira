package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.service.IssueUpdateService;
import com.cmcglobal.jiraplugin.poc.utils.UtilConstaints;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class DoneSubtaskPostFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(DoneSubtaskPostFunction.class);
    private IssueManager issueManager = ComponentAccessor.getIssueManager();
    private IssueUpdateService _issueUpdateService = new IssueUpdateService();
    private UserManager userManager = ComponentAccessor.getUserManager();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        Issue issue = getIssue(transientVars);
        IssueType issueType = issue.getIssueType();
        Issue parentIssue = issue.getParentObject();
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        if (issueType.getName().equalsIgnoreCase(Constants.shoppingIssueTypeName)) {
            _issueUpdateService.executeWorkflowTransition(parentIssue, "Approve", "Pending Receive", admin);
        } else if (issueType.getName().equalsIgnoreCase(Constants.policyIssueTypeName)) {
            _issueUpdateService.executeWorkflowTransition(parentIssue, "Approve", "Pending Inventory", admin);
        }
    }
}