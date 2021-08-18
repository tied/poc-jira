package com.cmcglobal.jiraplugin.poc2.installation.issue.payment;

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

@ExportAsService({PaymentIssueInstallation.class})
@Named
public class PaymentIssueInstallation extends AbstractIssueInstallation {

    @Inject
    public PaymentIssueInstallation(
            IIssueUtils issueUtils,
            IFieldUtils fieldUtils,
            POC2ProjectInstallation projectInstallation
    ) {
        super(issueUtils, fieldUtils, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        IssueType paymentIssueType = _issueUtils.createIssueType(Constants2.paymentIssueTypeName, null, Long.parseLong("10315"), _issueUtils.STANDARD());

        _lisOfIssueTypeCreated.put(Constants2.paymentIssueTypeName, paymentIssueType);

        ArrayList<String> options = new ArrayList<String>();
        options.add(paymentIssueType.getId());
        FieldConfigScheme pocScheme = _issueUtils.createIssueTypeScheme(Constants2.pocIssueTypeSchemeName, null, options);
        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.pocIssueTypeSchemeName, pocScheme);

        _issueUtils.AddIssueTypeSchemeToProject(pocScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
