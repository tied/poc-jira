package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.CustomField;
import com.cmcglobal.jiraplugin.poc.utils.ICustomFieldUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractFieldInstallation implements IFieldInstallation {
    protected ICustomFieldUtils _customFieldUtils;
    protected final List<String> _listOfFieldOnScreen;
    protected final HashMap<String, CustomField> _listOfFieldCreated;

    public AbstractFieldInstallation(
            ICustomFieldUtils customFieldUtils
    ){
        _customFieldUtils = customFieldUtils;
        _listOfFieldOnScreen = new ArrayList<String>();
        _listOfFieldCreated = new HashMap<String, CustomField>();
    }

    public abstract void Install() throws Exception;

    public final List<String> getListOfFieldOnScreen() {
        return _listOfFieldOnScreen;
    }

    public final HashMap<String, CustomField> getListOfFieldCreated() {
        return _listOfFieldCreated;
    }

    public final void lockCustomFieldCreated() throws Exception {
        for (String key : _listOfFieldCreated.keySet()) {
            _customFieldUtils.lockCustomField(_listOfFieldCreated.get(key));
        }
    }
}
