package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.cmcglobal.jiraplugin.poc.service.IssueUpdateService;
import com.cmcglobal.jiraplugin.poc.utils.UtilConstaints;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.basic.ComboPopup;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class FinishPaymentPostFunction extends AbstractJiraFunctionProvider {

    IssueManager issueManager = ComponentAccessor.getIssueManager();
    IssueUpdateService _issueUpdateService = new IssueUpdateService();
    UserManager userManager = ComponentAccessor.getUserManager();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue mutableIssue = getIssue(transientVars);
        Issue issue = getIssue(transientVars);

        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        String summary = issue.getSummary();
        String[] ids = summary.split(",");
        for (int i = 0; i < ids.length; i++) {
            try {
                Issue allocateIssue = issueManager.getIssueObject(ids[i]);
                _issueUpdateService.executeWorkflowTransition(allocateIssue, "Approve", "Pending Receive", admin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}