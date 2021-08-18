package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;
import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;
import com.cmcglobal.jiraplugin.poc.utils.IIssueUtils;

import java.util.Collection;
import java.util.HashMap;

public abstract class AbstractIssueInstallation implements IIssueInstallation {
    protected IIssueUtils _issueUtils;
    protected IFieldUtils _fieldUtils;
    protected IProjectInstallation _projectInstallation;
    protected final HashMap<String, IssueType> _lisOfIssueTypeCreated;
    protected final HashMap<String, FieldConfigScheme> _lisOfIssueTypeScreenSchemeCreated;
    protected final HashMap<String, ScreenableIssueOperation> _lisOfIssueOperation;

    public AbstractIssueInstallation(
            IIssueUtils issueUtils,
            IFieldUtils fieldUtils,
            IProjectInstallation projectInstallation
    ){
        _issueUtils = issueUtils;
        _fieldUtils = fieldUtils;
        _projectInstallation = projectInstallation;
        _lisOfIssueTypeCreated = new HashMap<String, IssueType>();
        _lisOfIssueTypeScreenSchemeCreated = new HashMap<String, FieldConfigScheme>();
        _lisOfIssueOperation = new HashMap<String, ScreenableIssueOperation>();
    }

    public abstract void Install() throws Exception;

    public HashMap<String, IssueType> getListOfIssueTypeCreated() {
        return _lisOfIssueTypeCreated;
    }

    public HashMap<String, FieldConfigScheme> getListOfIssueTypeSchemeCreated() {
        return _lisOfIssueTypeScreenSchemeCreated;
    }

    public HashMap<String, ScreenableIssueOperation> getAllIssueOperation() {
        if(_lisOfIssueOperation.isEmpty()){
            Collection<ScreenableIssueOperation> operations =_issueUtils.getAllOperation();
            _lisOfIssueOperation.put(IssueOperationKey.CREATE, operations.stream().filter(e -> e.getNameKey().contains(IssueOperationKey.CREATE)).findFirst().get());
            _lisOfIssueOperation.put(IssueOperationKey.EDIT, operations.stream().filter(e -> e.getNameKey().contains(IssueOperationKey.EDIT)).findFirst().get());
            _lisOfIssueOperation.put(IssueOperationKey.VIEW, operations.stream().filter(e -> e.getNameKey().contains(IssueOperationKey.VIEW)).findFirst().get());
        }
        return _lisOfIssueOperation;
    }
}
