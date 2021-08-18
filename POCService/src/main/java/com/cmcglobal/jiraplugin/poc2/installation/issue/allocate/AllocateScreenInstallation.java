package com.cmcglobal.jiraplugin.poc2.installation.issue.allocate;


import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.utils.IScreenUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractScreenInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IssueOperationKey;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({AllocateScreenInstallation.class})
@Named
public class AllocateScreenInstallation extends AbstractScreenInstallation {

    @Inject
    public AllocateScreenInstallation(
            IScreenUtils screenUtils,
            AllocateIssueInstallation issueInstallation,
            AllocateFieldInstallation fieldInstallation,
            POC2ProjectInstallation projectInstallation
    ) {
        super(screenUtils, issueInstallation, fieldInstallation, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        FieldScreen createScreen = _screenUtils.createFieldScreen(Constants2.allocateScreenCreateName, null);
        FieldScreen editScreen = _screenUtils.createFieldScreen(Constants2.allocateScreenEditName, null);
        FieldScreen viewScreen = _screenUtils.createFieldScreen(Constants2.allocateScreenViewName, null);

        FieldScreen createPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyCreateName, null);
        FieldScreen editPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyEditName, null);
        FieldScreen viewPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyViewName, null);

        FieldScreen createReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveCreateName, null);
        FieldScreen editReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveEditName, null);
        FieldScreen viewReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveViewName, null);

        _screenUtils.createScreenTab(createScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());

        _screenUtils.createScreenTab(createPolicyScreen, Constants.screenPolicyCreateName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editPolicyScreen, Constants.screenPolicyEditName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewPolicyScreen, Constants.screenPolicyViewName, _fieldInstallation.getListOfFieldOnScreen());

        _screenUtils.createScreenTab(createReceiveScreen, Constants.screenReceiveCreateName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editReceiveScreen, Constants.screenReceiveEditName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewReceiveScreen, Constants.screenReceiveViewName, _fieldInstallation.getListOfFieldOnScreen());

        _lisOfScreenCreated.put(Constants2.allocateScreenCreateName, createScreen);
        _lisOfScreenCreated.put(Constants2.allocateScreenEditName, editScreen);
        _lisOfScreenCreated.put(Constants2.allocateScreenViewName, viewScreen);
        _lisOfScreenCreated.put(Constants.screenPolicyCreateName, createPolicyScreen);
        _lisOfScreenCreated.put(Constants.screenPolicyEditName, editPolicyScreen);
        _lisOfScreenCreated.put(Constants.screenPolicyViewName, viewPolicyScreen);

        _lisOfScreenCreated.put(Constants.screenReceiveCreateName, createReceiveScreen);
        _lisOfScreenCreated.put(Constants.screenReceiveEditName, editReceiveScreen);
        _lisOfScreenCreated.put(Constants.screenReceiveViewName, viewReceiveScreen);

        FieldScreenScheme screenScheme = _screenUtils.createScreenScheme(Constants2.allocateScreenSchemeName, null);
        FieldScreenScheme subPolicyScreenScheme = _screenUtils.createScreenScheme(Constants.screenPolicySchemeName, null);
        FieldScreenScheme subReceiveScreenScheme = _screenUtils.createScreenScheme(Constants.screenReceiveSchemeName, null);

        _screenUtils.createSchemeItem(viewScreen, screenScheme, null);
        _screenUtils.createSchemeItem(viewScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _screenUtils.createSchemeItem(viewPolicyScreen, subPolicyScreenScheme, null);
        _screenUtils.createSchemeItem(viewPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _screenUtils.createSchemeItem(viewReceiveScreen, subReceiveScreenScheme, null);
        _screenUtils.createSchemeItem(viewReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _listOfScreenSchemeCreated.put(Constants2.allocateScreenSchemeName, screenScheme);

        IssueTypeScreenScheme issueTypeScreenScheme = _screenUtils.createIssueScreenScheme(Constants2.poc2ScreenIssueTypeSchemeName, null);

        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                screenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants2.allocateIssueTypeName).getId()
        );


        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                subPolicyScreenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.policyIssueTypeName).getId()
        );


        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                subReceiveScreenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.receiveIssueTypeName).getId()
        );

        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.poc2ScreenIssueTypeSchemeName, issueTypeScreenScheme);

        _screenUtils.addIssueTypeScreenToProject(issueTypeScreenScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
