//package com.cmcglobal.jiraplugin.poc.installation.issue;
//
//import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
//import com.atlassian.jira.issue.issuetype.IssueType;
//import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
//import com.cmcglobal.jiraplugin.poc.Constants;
//import com.cmcglobal.jiraplugin.poc.installation.project.ProjectInstallation;
//import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;
//import com.cmcglobal.jiraplugin.poc.utils.IIssueUtils;
//import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractIssueInstallation;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//import java.util.ArrayList;
//
//@ExportAsService({IssueInstallation.class})
//@Named
//public class IssueInstallation extends AbstractIssueInstallation {
//
//    @Inject
//    public IssueInstallation(
//            IIssueUtils issueUtils,IssueInstallation
//            IFieldUtils fieldUtils,
//            ProjectInstallation projectInstallation
//    ) {
//        super(issueUtils, fieldUtils, projectInstallation);
//    }
//
//    @Override
//    public void Install() throws Exception {
//        IssueType pocIssueType = _issueUtils.createIssueType(Constants.issueTypeName, null, Long.parseLong("10304"), _issueUtils.STANDARD());
//        IssueType shoppingSubtask = _issueUtils.createIssueType(Constants.shoppingIssueTypeName, null, Long.parseLong("10316"), _issueUtils.SUBTASK());
//        IssueType policySubtask = _issueUtils.createIssueType(Constants.policyIssueTypeName, null, Long.parseLong("10316"), _issueUtils.SUBTASK());
//        IssueType receiveSubtask = _issueUtils.createIssueType(Constants.receiveIssueTypeName, null, Long.parseLong("10316"), _issueUtils.SUBTASK());
//
//        _lisOfIssueTypeCreated.put(Constants.issueTypeName, pocIssueType);
//        _lisOfIssueTypeCreated.put(Constants.shoppingIssueTypeName, shoppingSubtask);
//        _lisOfIssueTypeCreated.put(Constants.policyIssueTypeName, policySubtask);
//        _lisOfIssueTypeCreated.put(Constants.receiveIssueTypeName, receiveSubtask);
//
//        ArrayList<String> options = new ArrayList<String>();
//        options.add(pocIssueType.getId());
//        options.add(shoppingSubtask.getId());
//        options.add(policySubtask.getId());
//        options.add(receiveSubtask.getId());
//        FieldConfigScheme pocScheme = _issueUtils.createIssueTypeScheme(Constants.issueTypeSchemeName, null, options);
//        _lisOfIssueTypeScreenSchemeCreated.put(Constants.issueTypeSchemeName, pocScheme);
//
//        _issueUtils.AddIssueTypeSchemeToProject(pocScheme, _projectInstallation.getListOfProjectCreated().get(Constants.projectName));
//    }
//}
