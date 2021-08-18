package com.cmcglobal.jiraplugin.poc2.installation.issue.allocate;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;
import com.cmcglobal.jiraplugin.poc.utils.IIssueUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractIssueInstallation;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;

@ExportAsService({AllocateIssueInstallation.class})
@Named
public class AllocateIssueInstallation extends AbstractIssueInstallation {

    @Inject
    public AllocateIssueInstallation(
            IIssueUtils issueUtils,
            IFieldUtils fieldUtils,
            POC2ProjectInstallation projectInstallation
            ) {
        super(issueUtils, fieldUtils, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        IssueType allocateIssueType = _issueUtils.createIssueType(Constants2.allocateIssueTypeName, null, Long.parseLong("10315"), _issueUtils.STANDARD());
        IssueType policySubtask = _issueUtils.createIssueType(Constants.policyIssueTypeName, null, Long.parseLong("10316"), _issueUtils.SUBTASK());
        IssueType receiveSubtask = _issueUtils.createIssueType(Constants.receiveIssueTypeName, null, Long.parseLong("10316"), _issueUtils.SUBTASK());

        _lisOfIssueTypeCreated.put(Constants.policyIssueTypeName, policySubtask);
        _lisOfIssueTypeCreated.put(Constants.receiveIssueTypeName, receiveSubtask);
        _lisOfIssueTypeCreated.put(Constants2.allocateIssueTypeName, allocateIssueType);

        ArrayList<String> options = new ArrayList<String>();
        options.add(allocateIssueType.getId());
        options.add(policySubtask.getId());
        options.add(receiveSubtask.getId());

        FieldConfigScheme pocScheme = _issueUtils.createIssueTypeScheme(Constants2.pocIssueTypeSchemeName, null, options);
        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.pocIssueTypeSchemeName, pocScheme);

        _issueUtils.AddIssueTypeSchemeToProject(pocScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
