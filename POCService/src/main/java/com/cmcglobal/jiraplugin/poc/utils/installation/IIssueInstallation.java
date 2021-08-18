package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;

import java.util.HashMap;

public interface IIssueInstallation {
    void Install() throws Exception;

    HashMap<String, IssueType> getListOfIssueTypeCreated();

    HashMap<String, FieldConfigScheme> getListOfIssueTypeSchemeCreated();

    HashMap<String, ScreenableIssueOperation> getAllIssueOperation();
}
