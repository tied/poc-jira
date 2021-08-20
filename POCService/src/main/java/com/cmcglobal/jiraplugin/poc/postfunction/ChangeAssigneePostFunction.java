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
public class ChangeAssigneePostFunction extends AbstractJiraFunctionProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChangeAssigneePostFunction.class);
    private ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
    private ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private UserManager userManager = ComponentAccessor.getUserManager();
    private IssueManager issueManager = ComponentAccessor.getIssueManager();
    private IssueUpdateService issueUpdateService = new IssueUpdateService();

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue mutableIssue = getIssue(transientVars);
        Issue issue = getIssue(transientVars);
        Issue parentIssue = issue.getParentObject();
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        ProjectRole ceoRole = projectRoleManager.getProjectRole(Constants.ceoRole);
        ProjectRole adminRole = projectRoleManager.getProjectRole(Constants.adminRole);
        ProjectRole accountingRole = projectRoleManager.getProjectRole(Constants.accountingRole);
        Project pocProject = projectManager.getProjectByCurrentKey(Constants.projectKey);
        ApplicationUser ceoUser = null;
        ApplicationUser accountingUser = null;
        ApplicationUser adminUser = null;
        try {
            Set<ApplicationUser> ceoList = projectRoleManager.getProjectRoleActors(ceoRole, pocProject).getApplicationUsers();
            Set<ApplicationUser> accountingList = projectRoleManager.getProjectRoleActors(accountingRole, pocProject).getApplicationUsers();
            Set<ApplicationUser> adminList = projectRoleManager.getProjectRoleActors(adminRole, pocProject).getApplicationUsers();

            for (ApplicationUser applicationUser : ceoList) {
                ceoUser = applicationUser;
                break;
            }
            for (ApplicationUser applicationUser : accountingList) {
                accountingUser = applicationUser;
                break;
            }
            for (ApplicationUser applicationUser : adminList) {
                adminUser = applicationUser;
                break;
            }
            if (issue.getIssueType().getName().equalsIgnoreCase("PoC")) {
                if (args.get("toStep") != null && args.get("toStep").toString().equalsIgnoreCase("Create")) {
                    if (adminUser == null)
                        throw new Exception("Not found Admin User");
                    mutableIssue.setAssignee(adminUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                }
            } else if (issue.getIssueType().getName().equalsIgnoreCase("Policy Subtask")) {
                if (issue.getStatus().getName().equalsIgnoreCase("Pending G Lead Approve")) {
                    if (ceoUser == null)
                        throw new Exception("Not found Admin User");
                    mutableIssue.setAssignee(ceoUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                }
            } else if (issue.getIssueType().getName().equalsIgnoreCase("Mua Sắm")) {
                if (issue.getStatus().getName().equalsIgnoreCase("Quotation List") || issue.getStatus().getName().equalsIgnoreCase("Request Payment")) {
                    if (ceoUser == null)
                        throw new Exception("Not found CEO User");
                    mutableIssue.setAssignee(ceoUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                } else if (issue.getStatus().getName().equalsIgnoreCase("Pending Approve Request") && args.get("toStep") != null
                        && args.get("toStep").toString().equalsIgnoreCase("Approve")) {
                    if (accountingUser == null)
                        throw new Exception("Not found Accounting User");
                    mutableIssue.setAssignee(accountingUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                } else if ((issue.getStatus().getName().equalsIgnoreCase("Pending Approve Request") && args.get("toStep") != null
                        && args.get("toStep").toString().equalsIgnoreCase("Reject")) ||
                        issue.getStatus().getName().equalsIgnoreCase("Pending Sign Contract")) {
                    if (adminUser == null)
                        throw new Exception("Not found Admin User");
                    mutableIssue.setAssignee(adminUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                }
            } else if (issue.getIssueType().getName().equalsIgnoreCase("Receive Subtask")) {
                if (issue.getStatus().getName().equalsIgnoreCase("Pending Delivery")) {
                    mutableIssue.setAssignee(issue.getReporterUser());
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                } else if (issue.getStatus().getName().equalsIgnoreCase("Pending Confirm")) {
                    if (adminUser == null)
                        throw new Exception("Not found Admin User");
                    mutableIssue.setAssignee(adminUser);
                    issueManager.updateIssue(admin, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
                    if (args.get("toStep") != null
                            && args.get("toStep").toString().equalsIgnoreCase("Confirm")) {
                        issueUpdateService.executeWorkflowTransition(parentIssue, "Done", "Done", admin);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}