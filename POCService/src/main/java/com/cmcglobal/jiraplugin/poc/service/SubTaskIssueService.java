package com.cmcglobal.jiraplugin.poc.service;


import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.utils.IssueUtils;
import com.cmcglobal.jiraplugin.poc.utils.UtilConstaints;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import org.apache.log4j.Logger;

import java.util.Map;

public class SubTaskIssueService {
    private ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private SubTaskManager subTaskManager = ComponentAccessor.getSubTaskManager();
    private UserManager userManager = ComponentAccessor.getUserManager();
    private IssueService issueService = ComponentAccessor.getIssueService();
    private JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    private CustomFieldService customFieldService = ComponentAccessor.getOSGiComponentInstanceOfType(CustomFieldService.class);
    private IssueUtils issueUtils = new IssueUtils();
    private Logger logger = Logger.getLogger(IssueUtils.class);
    private IssueManager issueManager = ComponentAccessor.getIssueManager();

    public void createPolicySubTask(Issue parentIssue, Map<String, Object> map) throws Exception {
        Project pocProject = projectManager.getProjectByCurrentKey(Constants2.projectKey);
        IssueType subPolicyType = issueUtils.getAllIssueType().stream().filter(e -> e.getName().equals(Constants.policyIssueTypeName)).findFirst().orElseGet(null);
        IssueInputParameters inputParameters = issueService.newIssueInputParameters();
        ApplicationUser assigneeUser = userManager.getUserByName((String) map.get("assignee"));
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        if (assigneeUser == null)
            throw new Exception(String.format("User %s doesn't exists.", map.get("assignee")));
        inputParameters.setReporterId(parentIssue.getReporter().getUsername());
        inputParameters.setProjectId(pocProject.getId());
        inputParameters.setIssueTypeId(subPolicyType.getId());
        inputParameters.setSummary((String) map.get("summary"));
        inputParameters.setAssigneeId(assigneeUser.getUsername());
        IssueService.CreateValidationResult createValidationResult = issueService.validateSubTaskCreate(admin, parentIssue.getId(), inputParameters);
        if (createValidationResult.isValid()) {
            IssueService.IssueResult result = issueService.create(admin, createValidationResult);
            if (!result.isValid()) {
                throw new Exception("unable create sub-task " + map.get("assignee"));
            }
            if (!result.isValid()) {
                logger.error("start error.");
                result.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
                Map<String, String> errors = result.getErrorCollection().getErrors();
                errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
                result.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
                logger.error("end error.");
            }
            subTaskManager.createSubTaskIssueLink(parentIssue, result.getIssue(), admin);
            MutableIssue subIssue = result.getIssue();
            subIssue.setAssignee(assigneeUser);
            issueManager.updateIssue(admin, subIssue, EventDispatchOption.ISSUE_ASSIGNED, false);
        } else {
            logger.error("start error.\n\n\n");
            createValidationResult.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
            Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
            errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
            createValidationResult.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
            logger.error("end error.\n\n\n");
            throw new Exception(String.format("unable create sub-task for %s. please contact to administrators for more information.", map.get("assignee")));
        }
    }

    public void createReceiveSubTask(Issue parentIssue, Map<String, Object> map) throws Exception {
        Project pocProject = projectManager.getProjectByCurrentKey(Constants2.projectKey);
        IssueType subReceiveType = issueUtils.getAllIssueType().stream().filter(e -> e.getName().equals(Constants.receiveIssueTypeName)).findFirst().orElseGet(null);
        IssueInputParameters inputParameters = issueService.newIssueInputParameters();
        ApplicationUser assigneeUser = userManager.getUserByName((String) map.get("assignee"));
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        if (assigneeUser == null)
            throw new Exception(String.format("User %s doesn't exists.", map.get("assignee")));
        inputParameters.setReporterId(parentIssue.getReporter().getUsername());
        inputParameters.setProjectId(pocProject.getId());
        inputParameters.setIssueTypeId(subReceiveType.getId());
        inputParameters.setSummary((String) map.get("summary"));
        inputParameters.setAssigneeId(assigneeUser.getUsername());
        IssueService.CreateValidationResult createValidationResult = issueService.validateSubTaskCreate(admin, parentIssue.getId(), inputParameters);
        if (createValidationResult.isValid()) {
            IssueService.IssueResult result = issueService.create(admin, createValidationResult);
            if (!result.isValid()) {
                throw new Exception("unable create sub-task " + map.get("assignee"));
            }
            if (!result.isValid()) {
                logger.error("start error.");
                result.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
                Map<String, String> errors = result.getErrorCollection().getErrors();
                errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
                result.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
                logger.error("end error.");
            }
            subTaskManager.createSubTaskIssueLink(parentIssue, result.getIssue(), admin);
            MutableIssue subIssue = result.getIssue();
            subIssue.setAssignee(assigneeUser);
            issueManager.updateIssue(admin, subIssue, EventDispatchOption.ISSUE_ASSIGNED, false);
        } else {
            logger.error("start error.\n\n\n");
            createValidationResult.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
            Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
            errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
            createValidationResult.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
            logger.error("end error.\n\n\n");
            throw new Exception(String.format("unable create sub-task for %s. please contact to administrators for more information.", map.get("assignee")));
        }
    }

    public void createShoppingSubTask(Issue parentIssue, Map<String, Object> map) throws Exception {
        Project pocProject = projectManager.getProjectByCurrentKey(Constants.projectKey);
        IssueType subShoppingType = issueUtils.getAllIssueType().stream().filter(e -> e.getName().equals(Constants.shoppingIssueTypeName)).findFirst().orElseGet(null);
        IssueInputParameters inputParameters = issueService.newIssueInputParameters();
        ApplicationUser assigneeUser = userManager.getUserByName((String) map.get("assignee"));
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        if (assigneeUser == null)
            throw new Exception(String.format("User %s - doesn't exists.", map.get("assignee")));
        inputParameters.setReporterId(parentIssue.getReporter().getUsername());
        inputParameters.setProjectId(pocProject.getId());
        inputParameters.setIssueTypeId(subShoppingType.getId());
        inputParameters.setSummary((String) map.get("summary"));
        inputParameters.setAssigneeId(assigneeUser.getUsername());
        IssueService.CreateValidationResult createValidationResult = issueService.validateSubTaskCreate(admin, parentIssue.getId(), inputParameters);
        if (createValidationResult.isValid()) {
            IssueService.IssueResult result = issueService.create(admin, createValidationResult);
            if (!result.isValid()) {
                throw new Exception("unable create sub-task " + map.get("assignee"));
            }
            if (!result.isValid()) {
                logger.error("start error.");
                result.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
                Map<String, String> errors = result.getErrorCollection().getErrors();
                errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
                result.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
                logger.error("end error.");
            }
            subTaskManager.createSubTaskIssueLink(parentIssue, result.getIssue(), admin);
            MutableIssue subIssue = result.getIssue();
            subIssue.setAssignee(assigneeUser);
            issueManager.updateIssue(admin, subIssue, EventDispatchOption.ISSUE_ASSIGNED, false);
        } else {
            logger.error("start error.\n\n\n");
            createValidationResult.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
            Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
            errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
            createValidationResult.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
            logger.error("end error.\n\n\n");
            throw new Exception(String.format("unable create sub-task for %s. please contact to administrators for more information.", map.get("assignee")));
        }
    }

    public void createIssue(IssueType issueType, Map<String, Object> map,ApplicationUser reporter) throws Exception {
        Project poc2Project = projectManager.getProjectByCurrentKey(Constants2.projectKey);
        IssueType paymentType = issueType;
        IssueInputParameters inputParameters = issueService.newIssueInputParameters();
        ApplicationUser assigneeUser = userManager.getUserByName((String) map.get("assignee"));
        ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
        if (assigneeUser == null)
            throw new Exception(String.format("User %s - doesn't exists.", map.get("assignee")));
        inputParameters.setReporterId(reporter.getUsername());
        inputParameters.setProjectId(poc2Project.getId());
        inputParameters.setIssueTypeId(paymentType.getId());
        inputParameters.setSummary((String) map.get("summary"));
        inputParameters.setAssigneeId(assigneeUser.getUsername());
        IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(admin, inputParameters);
        if (createValidationResult.isValid()) {
            IssueService.IssueResult result = issueService.create(admin, createValidationResult);
            if (!result.isValid()) {
                throw new Exception("unable create sub-task " + map.get("assignee"));
            }
            if (!result.isValid()) {
                logger.error("start error.");
                result.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
                Map<String, String> errors = result.getErrorCollection().getErrors();
                errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
                result.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
                logger.error("end error.");
            }
            /*subTaskManager.createSubTaskIssueLink(parentIssue, result.getIssue(), admin);
            MutableIssue subIssue = result.getIssue();
            subIssue.setAssignee(assigneeUser);
            issueManager.updateIssue(admin, subIssue, EventDispatchOption.ISSUE_ASSIGNED, false);*/
        } else {
            logger.error("start error.\n\n\n");
            createValidationResult.getErrorCollection().getReasons().forEach(reason -> logger.error(reason.toString()));
            Map<String, String> errors = createValidationResult.getErrorCollection().getErrors();
            errors.keySet().forEach(key -> logger.error(String.format("%s --- %s", key, errors.get(key))));
            createValidationResult.getErrorCollection().getErrorMessages().forEach(message -> logger.error(message));
            logger.error("end error.\n\n\n");
            throw new Exception(String.format("unable create sub-task for %s. please contact to administrators for more information.", map.get("assignee")));
        }
    }


}
