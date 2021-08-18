package com.cmcglobal.jiraplugin.poc2.installation.issue.shopping;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;
import com.cmcglobal.jiraplugin.poc.utils.IIssueUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractIssueInstallation;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;

@ExportAsService({ShoppingIssueInstallation.class})
@Named
public class ShoppingIssueInstallation extends AbstractIssueInstallation {

    @Inject
    public ShoppingIssueInstallation(
            IIssueUtils issueUtils,
            IFieldUtils fieldUtils,
            POC2ProjectInstallation projectInstallation
    ) {
        super(issueUtils, fieldUtils, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        IssueType shoppingIssueType = _issueUtils.createIssueType(Constants2.shoppingIssueTypeName, null, Long.parseLong("10315"), _issueUtils.STANDARD());

        _lisOfIssueTypeCreated.put(Constants2.shoppingIssueTypeName, shoppingIssueType);

        ArrayList<String> options = new ArrayList<String>();
        options.add(shoppingIssueType.getId());
        FieldConfigScheme pocScheme = _issueUtils.createIssueTypeScheme(Constants2.pocIssueTypeSchemeName, null, options);
        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.pocIssueTypeSchemeName, pocScheme);

        _issueUtils.AddIssueTypeSchemeToProject(pocScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
