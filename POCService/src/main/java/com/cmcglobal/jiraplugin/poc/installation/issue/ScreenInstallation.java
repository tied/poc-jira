/*
package com.cmcglobal.jiraplugin.poc.installation.issue;


import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeEntity;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.installation.project.ProjectInstallation;
import com.cmcglobal.jiraplugin.poc.utils.IScreenUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractScreenInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IssueOperationKey;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ScreenInstallation.class})
@Named
public class ScreenInstallation extends AbstractScreenInstallation {

    @Inject
    public ScreenInstallation(
            IScreenUtils screenUtils,
            IssueInstallation issueInstallation,
            FieldInstallation fieldInstallation,
            ProjectInstallation projectInstallation
    ) {
        super(screenUtils, issueInstallation, fieldInstallation, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        FieldScreen createScreen = _screenUtils.createFieldScreen(Constants.screenCreateName, null);
        FieldScreen editScreen = _screenUtils.createFieldScreen(Constants.screenEditName, null);
        FieldScreen viewScreen = _screenUtils.createFieldScreen(Constants.screenViewName, null);

        FieldScreen createShoppingScreen = _screenUtils.createFieldScreen(Constants.screenShoppingCreateName, null);
        FieldScreen editShoppingScreen = _screenUtils.createFieldScreen(Constants.screenShoppingEditName, null);
        FieldScreen viewShoppingScreen = _screenUtils.createFieldScreen(Constants.screenShoppingViewName, null);

        FieldScreen createPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyCreateName, null);
        FieldScreen editPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyEditName, null);
        FieldScreen viewPolicyScreen = _screenUtils.createFieldScreen(Constants.screenPolicyViewName, null);

        FieldScreen createReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveCreateName, null);
        FieldScreen editReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveEditName, null);
        FieldScreen viewReceiveScreen = _screenUtils.createFieldScreen(Constants.screenReceiveViewName, null);

        _screenUtils.createScreenTab(createScreen, Constants.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editScreen, Constants.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewScreen, Constants.screenTabName, _fieldInstallation.getListOfFieldOnScreen());

        _screenUtils.createScreenTab(createShoppingScreen, Constants.screenShoppingCreateName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editShoppingScreen, Constants.screenShoppingEditName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewShoppingScreen, Constants.screenShoppingViewName, _fieldInstallation.getListOfFieldOnScreen());

        _screenUtils.createScreenTab(createPolicyScreen, Constants.screenPolicyCreateName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editPolicyScreen, Constants.screenPolicyEditName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewPolicyScreen, Constants.screenPolicyViewName, _fieldInstallation.getListOfFieldOnScreen());

        _screenUtils.createScreenTab(createReceiveScreen, Constants.screenReceiveCreateName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editReceiveScreen, Constants.screenReceiveEditName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewReceiveScreen, Constants.screenReceiveViewName, _fieldInstallation.getListOfFieldOnScreen());

        _lisOfScreenCreated.put(Constants.screenCreateName, createScreen);
        _lisOfScreenCreated.put(Constants.screenEditName, editScreen);
        _lisOfScreenCreated.put(Constants.screenViewName, viewScreen);

        _lisOfScreenCreated.put(Constants.screenShoppingCreateName, createShoppingScreen);
        _lisOfScreenCreated.put(Constants.screenShoppingEditName, editShoppingScreen);
        _lisOfScreenCreated.put(Constants.screenShoppingViewName, viewShoppingScreen);

        _lisOfScreenCreated.put(Constants.screenPolicyCreateName, createPolicyScreen);
        _lisOfScreenCreated.put(Constants.screenPolicyEditName, editPolicyScreen);
        _lisOfScreenCreated.put(Constants.screenPolicyViewName, viewPolicyScreen);

        _lisOfScreenCreated.put(Constants.screenReceiveCreateName, createReceiveScreen);
        _lisOfScreenCreated.put(Constants.screenReceiveEditName, editReceiveScreen);
        _lisOfScreenCreated.put(Constants.screenReceiveViewName, viewReceiveScreen);

        FieldScreenScheme screenScheme = _screenUtils.createScreenScheme(Constants.screenSchemeName, null);
        FieldScreenScheme subShoppingScreenScheme = _screenUtils.createScreenScheme(Constants.screenShoppingSchemeName, null);
        FieldScreenScheme subPolicyScreenScheme = _screenUtils.createScreenScheme(Constants.screenPolicySchemeName, null);
        FieldScreenScheme subReceiveScreenScheme = _screenUtils.createScreenScheme(Constants.screenReceiveSchemeName, null);

        _screenUtils.createSchemeItem(viewScreen, screenScheme, null);
        _screenUtils.createSchemeItem(viewScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _screenUtils.createSchemeItem(viewShoppingScreen, subShoppingScreenScheme, null);
        _screenUtils.createSchemeItem(viewShoppingScreen, subShoppingScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editShoppingScreen, subShoppingScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createShoppingScreen, subShoppingScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _screenUtils.createSchemeItem(viewPolicyScreen, subPolicyScreenScheme, null);
        _screenUtils.createSchemeItem(viewPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createPolicyScreen, subPolicyScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _screenUtils.createSchemeItem(viewReceiveScreen, subReceiveScreenScheme, null);
        _screenUtils.createSchemeItem(viewReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createReceiveScreen, subReceiveScreenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _listOfScreenSchemeCreated.put(Constants.screenSchemeName, screenScheme);
        _listOfScreenSchemeCreated.put(Constants.screenShoppingSchemeName, subShoppingScreenScheme);
        _listOfScreenSchemeCreated.put(Constants.screenPolicySchemeName, subPolicyScreenScheme);
        _listOfScreenSchemeCreated.put(Constants.screenReceiveSchemeName, subReceiveScreenScheme);

        IssueTypeScreenScheme issueTypeScreenScheme = _screenUtils.createIssueScreenScheme(Constants.screenIssueTypeSchemeName, null);
        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                screenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.issueTypeName).getId()
        );
        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                subPolicyScreenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.shoppingIssueTypeName).getId()
        );
        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                subShoppingScreenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.policyIssueTypeName).getId()
        );

        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                subReceiveScreenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants.receiveIssueTypeName).getId()
        );

        _lisOfIssueTypeScreenSchemeCreated.put(Constants.screenIssueTypeSchemeName, issueTypeScreenScheme);

        _screenUtils.addIssueTypeScreenToProject(issueTypeScreenScheme, _projectInstallation.getListOfProjectCreated().get(Constants.projectName));
    }
}
*/
