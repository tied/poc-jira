package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.CustomField;

import java.util.HashMap;
import java.util.List;

public interface IFieldInstallation {
    void Install() throws Exception;
    List<String> getListOfFieldOnScreen();

    HashMap<String, CustomField> getListOfFieldCreated();

    void lockCustomFieldCreated() throws Exception;
}
