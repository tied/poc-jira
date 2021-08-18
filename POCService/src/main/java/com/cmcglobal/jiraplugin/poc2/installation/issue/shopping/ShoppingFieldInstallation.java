package com.cmcglobal.jiraplugin.poc2.installation.issue.shopping;

import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.utils.ICustomFieldUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractFieldInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ShoppingFieldInstallation.class})
@Named
public class ShoppingFieldInstallation extends AbstractFieldInstallation {

    @Inject
    public ShoppingFieldInstallation(ICustomFieldUtils customFieldUtils) {
        super(customFieldUtils);
    }

    public void Install() throws Exception{
        _listOfFieldOnScreen.add(IssueFieldConstants.ISSUE_TYPE);
        _listOfFieldOnScreen.add(IssueFieldConstants.SUMMARY);
        _listOfFieldOnScreen.add(IssueFieldConstants.DESCRIPTION);
        _listOfFieldOnScreen.add(IssueFieldConstants.ATTACHMENT);
        _listOfFieldOnScreen.add(IssueFieldConstants.PRIORITY);
        _listOfFieldOnScreen.add(IssueFieldConstants.DUE_DATE);
    }
}
