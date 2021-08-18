package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.cmcglobal.jiraplugin.poc.service.SubTaskIssueService;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class PolicyPostFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(PolicyPostFunction.class);
    private IssueManager issueManager = ComponentAccessor.getIssueManager();
    private SubTaskIssueService subTaskIssueService = new SubTaskIssueService();


    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue mutableIssue = getIssue(transientVars);
        Issue issue = issueManager.getIssueByCurrentKey(mutableIssue.getKey());
        if (issue.getStatus().getName().equalsIgnoreCase("Pending TBP Approve")) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("assignee", "crm-system");
                map.put("summary", issue.getSummary());
                subTaskIssueService.createPolicySubTask(issue, map);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }
}