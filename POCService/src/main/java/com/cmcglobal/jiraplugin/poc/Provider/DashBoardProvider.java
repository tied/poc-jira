package com.cmcglobal.jiraplugin.poc.Provider;

/* 
    @created by 12:58 PM 8/13/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/

import com.atlassian.jira.bean.SubTask;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.webresource.JiraWebResourceManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.utils.CustomFieldUtils;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import org.apache.velocity.VelocityContext;

import java.util.*;

public class DashBoardProvider {
    VelocityContext velocity;
    ProjectRoleManager projectRoleManager;
    IssueManager issueManager;
    ProjectManager projectManager;
    CustomFieldUtils customFieldUtils;
    private JiraWebResourceManager resourceManager;

    public DashBoardProvider() {
        velocity = new VelocityContext();
        projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
        issueManager = ComponentAccessor.getIssueManager();
        customFieldUtils = new CustomFieldUtils();
        projectManager = ComponentAccessor.getProjectManager();
        resourceManager = ComponentAccessor.getComponent(JiraWebResourceManager.class);
    }

    public VelocityContext getContextMap() {
        velocity.put("resourceManager", resourceManager);
        Map<String, Object> map = new HashMap<>();
        Project project = projectManager.getProjectByCurrentKey(Constants2.projectKey);
        Collection<IssueType> collection = project.getIssueTypes();
        Collection<Long> poc2Ids = new ArrayList<>();
        List<Map<String, Object>> allocateIssues = new ArrayList<>();
        try {
            poc2Ids = issueManager.getIssueIdsForProject(project.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Long id : poc2Ids) {
            Issue allocateIssue = issueManager.getIssueObject(id);
            if (allocateIssue.getIssueType().getName().equalsIgnoreCase("Cấp Phát") && !allocateIssue.getIssueType().isSubTask() && allocateIssue.getStatus().getName().equalsIgnoreCase("Pending Shopping")) {
                Map<String, Object> allocate = new HashMap<>();
                allocate.put("id", allocateIssue.getId());
                allocate.put("key", allocateIssue.getKey());
                allocate.put("summary", allocateIssue.getSummary());
                allocate.put("description", allocateIssue.getDescription() == null ? "" : allocateIssue.getDescription());
                allocateIssues.add(allocate);
            }
        }
        List<Issue> subTasks = new ArrayList<>();
        List<IssueType> parentTasks = new ArrayList<>();
        for (IssueType issueType : collection) {
            if (!issueType.isSubTask() && issueType.getName().equalsIgnoreCase(Constants2.shoppingIssueTypeName)) {
                parentTasks.add(issueType);
            }
        }
        velocity.put("allocateIssues", allocateIssues);
        velocity.put("issueTypes", collection);
        velocity.put("parentTasks", parentTasks);
        velocity.put("subTasks", subTasks);
        velocity.put("projectId", project.getId());
        return velocity;
    }
}
