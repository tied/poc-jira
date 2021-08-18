package com.cmcglobal.jiraplugin.poc2.installation.workflow;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.postfunction.*;
import com.cmcglobal.jiraplugin.poc.utils.IWorkflowUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractWorkflowInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IProjectInstallation;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.issue.allocate.AllocateIssueInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.payment.PaymentIssueInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.shopping.ShoppingIssueInstallation;
import org.apache.commons.collections4.map.HashedMap;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ExportAsService({WorkflowInstallation.class})
@Named
public class WorkflowInstallation extends AbstractWorkflowInstallation {
    private AllocateIssueInstallation _allocateIssueInstallation;
    private ShoppingIssueInstallation _shoppingIssueInstallation;
    private PaymentIssueInstallation _paymentIssueInstallation;

    @Inject
    public WorkflowInstallation(IWorkflowUtils workflowUtils,
                                IProjectInstallation projectInstallation,
                                AllocateIssueInstallation allocateIssueInstallation,
                                ShoppingIssueInstallation shoppingIssueInstallation,
                                PaymentIssueInstallation paymentIssueInstallation) {
        super(workflowUtils, projectInstallation);
        _allocateIssueInstallation = allocateIssueInstallation;
        _shoppingIssueInstallation = shoppingIssueInstallation;
        _paymentIssueInstallation = paymentIssueInstallation;
    }

    public void Install() throws Exception {
        _workflowUtils.runImportFromJWBFile(Constants2.allocateWorkflowName, Constants2.allocateWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants2.shoppingWorkflowName, Constants2.shoppingWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants2.paymentWorkflowName, Constants2.paymentWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants.workflowForSubtaskPolicyName, Constants.policyWFPathFile);
        _workflowUtils.runImportFromJWBFile(Constants.workflowForSubtaskReceiveName, Constants.receiveWFPathFile);

        JiraWorkflow wForSubTaskPolicy = _workflowUtils.getWorkflowByName(Constants.workflowForSubtaskPolicyName);
        JiraWorkflow wForSubTaskReceive = _workflowUtils.getWorkflowByName(Constants.workflowForSubtaskReceiveName);
        JiraWorkflow allocateWF = _workflowUtils.getWorkflowByName(Constants2.allocateWorkflowName);
        JiraWorkflow shoppingWF = _workflowUtils.getWorkflowByName(Constants2.shoppingWorkflowName);
        JiraWorkflow paymentWF = _workflowUtils.getWorkflowByName(Constants2.paymentWorkflowName);

        _lisOfWorkflowCreated.put(Constants2.allocateWorkflowName, allocateWF);
        _lisOfWorkflowCreated.put(Constants2.shoppingWorkflowName, shoppingWF);
        _lisOfWorkflowCreated.put(Constants2.paymentWorkflowName, paymentWF);
        _lisOfWorkflowCreated.put(Constants.workflowForSubtaskPolicyName, wForSubTaskPolicy);
        _lisOfWorkflowCreated.put(Constants.workflowForSubtaskReceiveName, wForSubTaskReceive);

        ArrayList<String> issueTypeForSubTaskPolicy = new ArrayList<>();
        IssueType subPolicyType = _allocateIssueInstallation.getListOfIssueTypeCreated().get(Constants.policyIssueTypeName);
        issueTypeForSubTaskPolicy.add(subPolicyType.getId());

        ArrayList<String> allocateIssueTypeList = new ArrayList<>();
        IssueType allocateIssueType = _allocateIssueInstallation.getListOfIssueTypeCreated().get(Constants2.allocateIssueTypeName);
        allocateIssueTypeList.add(allocateIssueType.getId());

        ArrayList<String> shoppingIssueTypeList = new ArrayList<>();
        IssueType shoppingIssueType = _shoppingIssueInstallation.getListOfIssueTypeCreated().get(Constants2.shoppingIssueTypeName);
        shoppingIssueTypeList.add(shoppingIssueType.getId());

        ArrayList<String> paymentIssueTypeList = new ArrayList<>();
        IssueType paymentIssueType = _paymentIssueInstallation.getListOfIssueTypeCreated().get(Constants2.paymentIssueTypeName);
        paymentIssueTypeList.add(paymentIssueType.getId());

        ArrayList<String> issueTypeForSubTaskReceive = new ArrayList<>();
        IssueType subReceiveType = _allocateIssueInstallation.getListOfIssueTypeCreated().get(Constants.receiveIssueTypeName);
        issueTypeForSubTaskReceive.add(subReceiveType.getId());

        _workflowUtils.createWorkflowScheme(Constants2.poc2WorkflowSchemeName, null, wForSubTaskPolicy, issueTypeForSubTaskPolicy);
        _workflowUtils.createWorkflowScheme(Constants2.poc2WorkflowSchemeName, null, wForSubTaskReceive, issueTypeForSubTaskReceive);
        _workflowUtils.createWorkflowScheme(Constants2.poc2WorkflowSchemeName, null, allocateWF, allocateIssueTypeList);
        _workflowUtils.createWorkflowScheme(Constants2.poc2WorkflowSchemeName, null, shoppingWF, shoppingIssueTypeList);
        AssignableWorkflowScheme scheme = _workflowUtils.createWorkflowScheme(Constants2.poc2WorkflowSchemeName, null, paymentWF, paymentIssueTypeList);

        _lisOfSchemeCreated.put(Constants2.poc2WorkflowSchemeName, scheme);

        _workflowUtils.addWorkflowSchemeToProject(scheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));

       /* _workflowUtils.addPostFunctionToTransition(allocateWF, "Create", "Pending TBP Approve", "com.cmcglobal.jiraplugin.poc.POCService.change-assignee-post-function", ChangeAssigneePostFunction.class, new HashMap<String, Object>() {{
            put("toStep", "Create");
        }});
        _workflowUtils.addPostFunctionToTransition(allocateWF, "Approve", "Policy", "com.cmcglobal.jiraplugin.poc.POCService.policy-post-function", PolicyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(allocateWF, "Unavailable", "Pending Shopping", "com.cmcglobal.jiraplugin.poc.POCService.unabvailable-property-post-function", UnavailablePropertyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(allocateWF, "Available", "Pending Receive", "com.cmcglobal.jiraplugin.poc.POCService.available-property-post-function", AvailablePropertyPostFunction.class, new HashedMap<>());
        _workflowUtils.addPostFunctionToTransition(allocateWF, "Approve", "Pending Receive", "com.cmcglobal.jiraplugin.poc.POCService.available-property-post-function", AvailablePropertyPostFunction.class, new HashedMap<>());

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
*/
    }

}
