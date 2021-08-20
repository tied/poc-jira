package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.service.IssueUpdateService;
import com.cmcglobal.jiraplugin.poc.service.SubTaskIssueService;
import com.cmcglobal.jiraplugin.poc.utils.IssueUtils;
import com.cmcglobal.jiraplugin.poc.utils.UtilConstaints;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class FinishShoppingPostFunction extends AbstractJiraFunctionProvider {

    IssueManager issueManager = ComponentAccessor.getIssueManager();
    SubTaskIssueService subTaskIssueService = new SubTaskIssueService();
    IssueUpdateService issueUpdateService = new IssueUpdateService();
    UserManager userManager = ComponentAccessor.getUserManager();
    IssueUtils issueUtils = new IssueUtils();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue mutableIssue = getIssue(transientVars);
        Issue issue = getIssue(transientVars);
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);

        String summary = issue.getSummary();
        String[] allocateIds = summary.split(",");
        try {
            IssueType paymentIssueType = issueUtils.getAllIssueType().stream().filter(e -> e.getName().equals(Constants2.paymentIssueTypeName)).findFirst().orElseGet(null);
            Map<String, Object> map = new HashMap<>();
            map.put("assignee", admin.getUsername());
            map.put("summary", issue.getSummary());
            map.put("reporter", issue.getReporter().getUsername());
            subTaskIssueService.createIssue(paymentIssueType, map, issue.getReporter());
            for (int i = 0; i < allocateIds.length; i++) {
                try {
                    Issue allocateIssue = issueManager.getIssueObject(allocateIds[i]);
                    issueUpdateService.executeWorkflowTransition(allocateIssue, "Approve", "Pending Payment", admin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}