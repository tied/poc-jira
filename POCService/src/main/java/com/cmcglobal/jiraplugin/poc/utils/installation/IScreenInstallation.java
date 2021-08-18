package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;

import java.util.HashMap;

public interface IScreenInstallation {
    abstract void Install() throws Exception;

    HashMap<String, FieldScreen> getFieldScreenCreated();

    HashMap<String, FieldScreenScheme> getFieldScreenSchemeCreated();

    HashMap<String, IssueTypeScreenScheme> getIssueTypeScreenSchemeCreated();
}
