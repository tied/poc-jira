package com.cmcglobal.jiraplugin.poc2.installation.issue.shopping;


import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.utils.IScreenUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractScreenInstallation;
import com.cmcglobal.jiraplugin.poc.utils.installation.IssueOperationKey;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ShoppingScreenInstallation.class})
@Named
public class ShoppingScreenInstallation extends AbstractScreenInstallation {

    @Inject
    public ShoppingScreenInstallation(
            IScreenUtils screenUtils,
            ShoppingIssueInstallation issueInstallation,
            ShoppingFieldInstallation fieldInstallation,
            POC2ProjectInstallation projectInstallation
    ) {
        super(screenUtils, issueInstallation, fieldInstallation, projectInstallation);
    }

    @Override
    public void Install() throws Exception {
        FieldScreen createScreen = _screenUtils.createFieldScreen(Constants2.shoppingScreenCreateName, null);
        FieldScreen editScreen = _screenUtils.createFieldScreen(Constants2.shoppingScreenEditName, null);
        FieldScreen viewScreen = _screenUtils.createFieldScreen(Constants2.shoppingScreenViewName, null);

        _screenUtils.createScreenTab(createScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(editScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());
        _screenUtils.createScreenTab(viewScreen, Constants2.screenTabName, _fieldInstallation.getListOfFieldOnScreen());

        _lisOfScreenCreated.put(Constants2.shoppingScreenCreateName, createScreen);
        _lisOfScreenCreated.put(Constants2.shoppingScreenEditName, editScreen);
        _lisOfScreenCreated.put(Constants2.shoppingScreenViewName, viewScreen);

        FieldScreenScheme screenScheme = _screenUtils.createScreenScheme(Constants2.shoppingScreenSchemeName, null);

        _screenUtils.createSchemeItem(viewScreen, screenScheme, null);
        _screenUtils.createSchemeItem(viewScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.VIEW));
        _screenUtils.createSchemeItem(editScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.EDIT));
        _screenUtils.createSchemeItem(createScreen, screenScheme, _issueInstallation.getAllIssueOperation().get(IssueOperationKey.CREATE));

        _listOfScreenSchemeCreated.put(Constants2.shoppingScreenSchemeName, screenScheme);

        IssueTypeScreenScheme issueTypeScreenScheme = _screenUtils.createIssueScreenScheme(Constants2.poc2ScreenIssueTypeSchemeName, null);
        _screenUtils.createIssueScreenSchemeEntity(
                issueTypeScreenScheme,
                screenScheme,
                _issueInstallation.getListOfIssueTypeCreated().get(Constants2.shoppingIssueTypeName).getId()
        );

        _lisOfIssueTypeScreenSchemeCreated.put(Constants2.poc2ScreenIssueTypeSchemeName, issueTypeScreenScheme);

        _screenUtils.addIssueTypeScreenToProject(issueTypeScreenScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
