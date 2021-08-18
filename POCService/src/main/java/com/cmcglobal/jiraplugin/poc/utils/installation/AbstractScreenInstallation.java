package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.cmcglobal.jiraplugin.poc.utils.IScreenUtils;

import java.util.HashMap;

public abstract class AbstractScreenInstallation implements IScreenInstallation {
    protected IScreenUtils _screenUtils;
    protected IFieldInstallation _fieldInstallation;
    protected IIssueInstallation _issueInstallation;
    protected IProjectInstallation _projectInstallation;

    protected final HashMap<String, FieldScreen> _lisOfScreenCreated;
    protected final HashMap<String, FieldScreenScheme> _listOfScreenSchemeCreated;
    protected final HashMap<String, IssueTypeScreenScheme> _lisOfIssueTypeScreenSchemeCreated;

    public AbstractScreenInstallation(
            IScreenUtils screenUtils,
            IIssueInstallation issueInstallation,
            IFieldInstallation fieldInstallation,
            IProjectInstallation projectInstallation
    ){
        _screenUtils = screenUtils;
        _fieldInstallation = fieldInstallation;
        _issueInstallation = issueInstallation;
        _projectInstallation = projectInstallation;
        _lisOfScreenCreated = new HashMap<String, FieldScreen>();
        _listOfScreenSchemeCreated = new HashMap<String, FieldScreenScheme>();
        _lisOfIssueTypeScreenSchemeCreated = new HashMap<String, IssueTypeScreenScheme>();
    }

    public abstract void Install() throws Exception;

    public HashMap<String, FieldScreen> getFieldScreenCreated() {
        return _lisOfScreenCreated;
    }

    public HashMap<String, FieldScreenScheme> getFieldScreenSchemeCreated() {
        return _listOfScreenSchemeCreated;
    }

    public HashMap<String, IssueTypeScreenScheme> getIssueTypeScreenSchemeCreated() {
        return _lisOfIssueTypeScreenSchemeCreated;
    }
}
