/*
package com.cmcglobal.jiraplugin.poc.installation.workflow;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.installation.issue.IssueInstallation;
import com.cmcglobal.jiraplugin.poc.postfunction.*;
import com.cmcglobal.jiraplugin.poc.utils.IWorkflowUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractWorkflowInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IProjectInstallation;
import org.apache.commons.collections4.map.HashedMap;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;

@ExportAsService({WorkflowInstallation.class})
@Named
public class WorkflowInstallation extends AbstractWorkflowInstallation {
    private IssueInstallation _issueInstallation;

    @Inject
    public WorkflowInstallation(IWorkflowUtils workflowUtils, IProjectInstallation projectInstallation, IssueInstallation issueInstallation) {
        super(workflowUtils, projectInstallation);
        _issueInstallation = issueInstallation;
    }

    public void Install() throws Exception {
        _workflowUtils.runImportFromJWBFile(Constants.workflowForTaskName, Constants.mainWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants.workflowForSubtaskShoppingName, Constants.shoppingWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants.workflowForSubtaskPolicyName, Constants.policyWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants.workflowForSubtaskReceiveName, Constants.receiveWFPathFile);

        JiraWorkflow wForTask = _workflowUtils.getWorkflowByName(Constants.workflowForTaskName);
        JiraWorkflow wForSubTaskShopping = _workflowUtils.getWorkflowByName(Constants.workflowForSubtaskShoppingName);
        JiraWorkflow wForSubTaskPolicy = _workflowUtils.getWorkflowByName(Constants.workflowForSubtaskPolicyName);
        JiraWorkflow wForSubTaskReceive = _workflowUtils.getWorkflowByName(Constants.workflowForSubtaskReceiveName);


        _lisOfWorkflowCreated.put(Constants.workflowForTaskName, wForTask);
        _lisOfWorkflowCreated.put(Constants.workflowForSubtaskShoppingName, wForSubTaskShopping);
        _lisOfWorkflowCreated.put(Constants.workflowForSubtaskPolicyName, wForSubTaskPolicy);
        _lisOfWorkflowCreated.put(Constants.workflowForSubtaskReceiveName, wForSubTaskReceive);

        ArrayList<String> issueTypeForSubTaskShopping = new ArrayList<>();
        IssueType subShoppingType = _issueInstallation.getListOfIssueTypeCreated().get(Constants.shoppingIssueTypeName);
        issueTypeForSubTaskShopping.add(subShoppingType.getId());

        ArrayList<String> issueTypeForSubTaskPolicy = new ArrayList<>();
        IssueType subPolicyType = _issueInstallation.getListOfIssueTypeCreated().get(Constants.policyIssueTypeName);
        issueTypeForSubTaskPolicy.add(subPolicyType.getId());

        ArrayList<String> issueTypeForTask = new ArrayList<>();
        IssueType pocType = _issueInstallation.getListOfIssueTypeCreated().get(Constants.issueTypeName);
        issueTypeForTask.add(pocType.getId());

        ArrayList<String> issueTypeForSubTaskReceive = new ArrayList<>();
        IssueType subReceiveType = _issueInstallation.getListOfIssueTypeCreated().get(Constants.receiveIssueTypeName);
        issueTypeForSubTaskReceive.add(subReceiveType.getId());

        _workflowUtils.createWorkflowScheme(Constants.workflowSchemeName, null, wForSubTaskShopping, issueTypeForSubTaskShopping);
        _workflowUtils.createWorkflowScheme(Constants.workflowSchemeName, null, wForSubTaskPolicy, issueTypeForSubTaskPolicy);
        _workflowUtils.createWorkflowScheme(Constants.workflowSchemeName, null, wForSubTaskReceive, issueTypeForSubTaskReceive);

        AssignableWorkflowScheme scheme = _workflowUtils.createWorkflowScheme(Constants.workflowSchemeName, null, wForTask, issueTypeForTask);

        _lisOfSchemeCreated.put(Constants.workflowSchemeName, scheme);

        _workflowUtils.addWorkflowSchemeToProject(scheme, _projectInstallation.getListOfProjectCreated().get(Constants.projectName));

        _workflowUtils.addPostFunctionToTransition(wForTask, "Create", "Pending TBP Approve", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashMap<String, Object>() {{
            put("toStep", "Create");
        }});
        _workflowUtils.addPostFunctionToTransition(wForTask, "Approve", "Policy", "com.cmcglobal.jiraplugin.poc.POCService.policy-post-function", PolicyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForTask, "Unavailable", "Pending Shopping", "com.cmcglobal.jiraplugin.poc.POCService.unabvailable-property-post-function", UnavailablePropertyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForTask, "Available", "Pending Receive", "com.cmcglobal.jiraplugin.poc.POCService.available-property-post-function", AvailablePropertyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForTask, "Approve", "Pending Receive", "com.cmcglobal.jiraplugin.poc.POCService.available-property-post-function", AvailablePropertyPostFunction.class, new HashedMap<>());

        _workflowUtils.addPostFunctionToTransition(wForSubTaskPolicy, "Approve", "Done", "com.cmcglobal.jiraplugin.poc.POCService.done-subtask-post-function", DoneSubtaskPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskPolicy, "Reject", "Rejected", "com.cmcglobal.jiraplugin.poc.POCService.reject-subtask-post-function", RejectSubtaskPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskPolicy, "Approve", "Pending CEO Approve", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<>());

        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Done", "Done", "com.cmcglobal.jiraplugin.poc.POCService.done-subtask-post-function", DoneSubtaskPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Seldom", "Pending Sign Contract", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Approve", "Request Payment", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Request Approve", "Pending Approve Request", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function",
                ChangeAssigneePostFunction.class, new HashMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Reject", "Request Payment", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function",
                ChangeAssigneePostFunction.class, new HashMap<String, Object>() {{
                    put("toStep", "Reject");
                }});
        _workflowUtils.addPostFunctionToTransition(wForSubTaskShopping, "Approve", "Pending Payment", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<String, Object>() {{
            put("toStep", "Approve");
        }});
        _workflowUtils.addPostFunctionToTransition(wForSubTaskReceive, "Delivered", "Pending Confirm", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskReceive, "Reject", "Pending Receive", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(wForSubTaskReceive, "Confirm", "Done", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashedMap<String, Object>() {{
            put("toStep", "Confirm");
        }});

    }

}
*/
