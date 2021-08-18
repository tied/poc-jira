package com.cmcglobal.jiraplugin.poc.postfunction;

import java.util.Map;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
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
public class RejectSubtaskPostFunction extends AbstractJiraFunctionProvider {
    private UserManager userManager = ComponentAccessor.getUserManager();
    private IssueUpdateService _issueUpdateService = new IssueUpdateService();
    private IssueManager issueManager = ComponentAccessor.getIssueManager();
    private ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
    private ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private Logger logger = LoggerFactory.getLogger(RejectSubtaskPostFunction.class);

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        Issue issue = getIssue(transientVars);
        MutableIssue mutableIssue = getIssue(transientVars);
        IssueType issueType = issue.getIssueType();
        Issue parentIssue = issue.getParentObject();
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);

        if (issueType.getName().equalsIgnoreCase(Constants.policyIssueTypeName)) {
            ProjectRole tbpRole = projectRoleManager.getProjectRole(Constants.tbpRole);
            Project pocProject = projectManager.getProjectByCurrentKey(Constants.projectKey);
            try {
                Set<ApplicationUser> tbpList = projectRoleManager.getProjectRoleActors(tbpRole, pocProject).getApplicationUsers();

                if (tbpList.isEmpty())
                    throw new Exception("Not found TBP Role");
                ApplicationUser assignee = null;
                for (ApplicationUser applicationUser : tbpList) {
                    assignee = applicationUser;
                    break;
                }
                mutableIssue.setAssignee(assignee);
                issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                _issueUpdateService.executeWorkflowTransition(parentIssue, "Reject", "Pending TBP Approve", admin);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}