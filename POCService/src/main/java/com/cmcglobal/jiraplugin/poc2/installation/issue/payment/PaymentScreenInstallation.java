package com.cmcglobal.jiraplugin.poc2.installation.issue.payment;


import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.utils.IScreenUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractScreenInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IssueOperationKey;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.payment.PaymentIssueInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.payment.PaymentFieldInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({PaymentScreenInstallation.class})
@Named
public class PaymentScreenInstallation extends AbstractScreenInstallation {

    @Inject
    public PaymentScreenInstallation(
            IScreenUtils screenUtils,
            PaymentIssueInstallation issueInstallation,
            PaymentFieldInstallation fieldInstallation,
            POC2ProjectInstallation projectInstallation
    ) {
        super(screenUtils, issueInstallation, fieldInstallation, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        FieldScreen createScreen = _screenUtils.createFieldScreen(Constants2.paymentScreenCreateName, null);
        FieldScreen editScreen = _screenUtils.createFieldScreen(Constants2.paymentScreenEditName, null);
        FieldScreen viewScreen = _screenUtils.createFieldScreen(Constants2.paymentScreenViewName, null);

        _screenUtils.createScreenTab(createScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());

        _lisOfScreenCreated.put(Constants2.paymentScreenCreateName, createScreen);
        _lisOfScreenCreated.put(Constants2.paymentScreenEditName, editScreen);
        _lisOfScreenCreated.put(Constants2.paymentScreenViewName, viewScreen);

        FieldScreenScheme screenScheme = _screenUtils.createScreenScheme(Constants2.paymentScreenSchemeName, null);

        _screenUtils.createSchemeItem(viewScreen, screenScheme, null);
        _screenUtils.createSchemeItem(viewScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _listOfScreenSchemeCreated.put(Constants2.paymentScreenSchemeName, screenScheme);

        IssueTypeScreenScheme issueTypeScreenScheme = _screenUtils.createIssueScreenScheme(Constants2.poc2ScreenIssueTypeSchemeName, null);
        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                screenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants2.paymentIssueTypeName).getId()
        );

        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.poc2ScreenIssueTypeSchemeName, issueTypeScreenScheme);

        _screenUtils.addIssueTypeScreenToProject(issueTypeScreenScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
