package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.service.SubTaskIssueService;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class UnavailablePropertyPostFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(UnavailablePropertyPostFunction.class);
    private SubTaskIssueService subTaskIssueService = new SubTaskIssueService();
    private IssueManager issueManager = ComponentAccessor.getIssueManager();
    private ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
    private ProjectManager projectManager = ComponentAccessor.getProjectManager();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue mutableIssue = getIssue(transientVars);
        Issue issue = issueManager.getIssueByCurrentKey(mutableIssue.getKey());
        if (issue.getStatus().getName().equalsIgnoreCase("Pending Inventory")) {
            try {
                Project pocProject = projectManager.getProjectByCurrentKey(Constants.projectKey);
                ProjectRole adminRole = projectRoleManager.getProjectRole(Constants.adminRole);
                ApplicationUser adminUser = null;
                Set<ApplicationUser> adminList = projectRoleManager.getProjectRoleActors(adminRole, pocProject).getApplicationUsers();
                for (ApplicationUser applicationUser : adminList) {
                    adminUser = applicationUser;
                    break;
                }
                if (adminUser == null)
                    throw new Exception("Not found Admin User");

                Map<String, Object> map = new HashMap<>();
                map.put("assignee", adminUser.getUsername());
                map.put("summary", issue.getSummary());
                subTaskIssueService.createShoppingSubTask(issue, map);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}