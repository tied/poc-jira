package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.IssueTypeService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.context.ProjectContext;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.operation.IssueOperations;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ExportAsService({IIssueUtils.class})
@Named
public class IssueUtils implements IIssueUtils {
    private static final Logger log = LoggerFactory.getLogger(IssueUtils.class);

    private IssueTypeService _issueTypeService;
    private IssueService _issueService;
    private IssueManager _issueManager;
    private IssueTypeSchemeManager _issueTypeSchemeManager;
    private IssueTypeManager _issueTypeManager;
    private UserManager _userManager;
    private FieldConfigSchemeManager _fieldConfigSchemeManager;
    private ApplicationUser _user;

    public IssueUtils() {
        _issueTypeService = ComponentAccessor.getComponent(IssueTypeService.class);
        _issueManager = ComponentAccessor.getIssueManager();
        _issueService = ComponentAccessor.getIssueService();
        _issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
        _issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
        _userManager = ComponentAccessor.getUserManager();
        _fieldConfigSchemeManager = ComponentAccessor.getFieldConfigSchemeManager();
        _user = _userManager.getUserByName(UtilConstaints.USERNAME);
    }

    @Override
    public IssueTypeService.IssueTypeCreateInput.Type STANDARD() {
        return IssueTypeService.IssueTypeCreateInput.Type.STANDARD;
    }

    @Override
    public IssueTypeService.IssueTypeCreateInput.Type SUBTASK() {
        return IssueTypeService.IssueTypeCreateInput.Type.SUBTASK;
    }

    @Override
    public Collection<ScreenableIssueOperation> getAllOperation() {
        return IssueOperations.getIssueOperations();
    }

    @Override
    public Collection<IssueType> getAllIssueType() {
        return _issueTypeManager.getIssueTypes();
    }

    @Override
    public void setDefaultIssueType(FieldConfigScheme scheme, String issueTypeId) {
        _issueTypeSchemeManager.setDefaultValue(scheme.getOneAndOnlyConfig(), issueTypeId);
    }

    @Override
    public FieldConfigScheme AddIssueTypeSchemeToProject(FieldConfigScheme scheme, Project project) throws Exception {
        JiraContextNode projectContext = new ProjectContext(project.getId());
        List<JiraContextNode> existsContextNode = scheme.getContexts();
        List<JiraContextNode> newContextNode = new ArrayList<>();
        boolean isExistsContextNode = existsContextNode.stream().anyMatch(e -> project.getId().equals(e.getProjectId()));
        if (isExistsContextNode)
            return scheme;
        newContextNode.addAll(existsContextNode);
        newContextNode.add(projectContext);
        return _fieldConfigSchemeManager.updateFieldConfigScheme(scheme, newContextNode, scheme.getField());
    }

    @Override
    public IssueType createIssueType(
            String name,
            String description,
            Long avatarId,
            IssueTypeService.IssueTypeCreateInput.Type type) throws Exception {
        if (name == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        boolean isExistsType = _issueTypeManager.getIssueTypes().stream().anyMatch(e -> name.toUpperCase().equals(e.getName().toUpperCase()));
        if (isExistsType)
            return _issueTypeManager.getIssueTypes().stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get();
        IssueTypeService.IssueTypeCreateInput.Builder newTypeBuilder = IssueTypeService.IssueTypeCreateInput.builder();
        newTypeBuilder.setName(name);
        if (type != null)
            newTypeBuilder.setType(type);
        if (description != null)
            newTypeBuilder.setDescription(description);
        IssueTypeService.CreateValidationResult validationResult = _issueTypeService.validateCreateIssueType(_user, newTypeBuilder.build());
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_ISSUETYPE_CREATEVALIDATEFAIL);
        }
        IssueType ret = null;
        switch (type) {
            case STANDARD:
                ret = _issueTypeManager.createIssueType(name, description, avatarId);
                break;
            case SUBTASK:
                ret = _issueTypeManager.createSubTaskIssueType(name, description, avatarId);
                break;
            default:
                throw new Exception("Issue Type Invalid.");
        }
        if (ret == null) {
            throw new Exception(UtilConstaints.ERROR_ISSUETYPE_CRETEFAIL);
        }
        log.info(UtilConstaints.CREATE_SUCCESS_FORMAT + "IssueType", ret.getId(), ret.getName());
        return ret;
    }

    @Override
    public Issue create(IssueInputParameters params) throws Exception {
        if (params == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        IssueService.CreateValidationResult validationResult = _issueService.validateCreate(_user, params);
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_ISSUE_CREATEVALIDATEFAIL);
        }
        IssueService.IssueResult result = _issueService.create(_user, validationResult);
        if (!result.isValid()) {
            result.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_ISSUE_CREATEFAIL);
        }
        log.info(UtilConstaints.CREATE_SUCCESS_FORMAT + "Issue", result.getIssue().getId().toString(), result.getIssue().getSummary());
        return result.getIssue();
    }

    @Override
    public IssueType updateIssueType(IssueType issueType, String name, String description, Long avatarId) throws Exception {
        IssueTypeService.IssueTypeUpdateInput.Builder updateBuilder = IssueTypeService.IssueTypeUpdateInput.builder();
        if (name != null)
            updateBuilder.setName(name);
        if (description != null)
            updateBuilder.setDescription(description);
        if (avatarId != null)
            updateBuilder.setAvatarId(avatarId);
        updateBuilder.setIssueTypeToUpdateId(Long.parseLong(issueType.getId()));
        IssueTypeService.UpdateValidationResult validationResult = _issueTypeService.validateUpdateIssueType(_user, issueType.getId(), updateBuilder.build());
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_ISSUETYPE_UPDATEVALIDATEFAIL);
        }
        IssueTypeService.IssueTypeResult result = _issueTypeService.updateIssueType(_user, validationResult);
        log.info(UtilConstaints.UPDATE_SUCCESS_FORMAT + "IssueType", result.getIssueType().getId(), result.getIssueType().getName());
        return result.getIssueType();
    }

    @Override
    public FieldConfigScheme createIssueTypeScheme(String schemeName, String schemeDescription, List<String> optionIDs) {
        boolean isExistsScheme = _issueTypeSchemeManager.getAllSchemes().stream().anyMatch(e -> schemeName.toUpperCase().equals(e.getName().toUpperCase()));
        if (isExistsScheme)
            return updateIssueTypeScheme(
                    _issueTypeSchemeManager.getAllSchemes().stream().filter(e -> schemeName.toUpperCase().equals(e.getName().toUpperCase())).findFirst().get(),
                    schemeName,
                    schemeDescription,
                    optionIDs);
        FieldConfigScheme result = _issueTypeSchemeManager.create(schemeName, schemeDescription, optionIDs);
        log.info(UtilConstaints.CREATE_SUCCESS_FORMAT + "IssueType Scheme", result.getId().toString(), result.getName());
        return result;
    }

    @Override
    public FieldConfigScheme updateIssueTypeScheme(FieldConfigScheme oldScheme, String schemeName, String schemeDescription, List<String> optionIDs) {
        OptionSetManager optionSetManager = ComponentAccessor.getComponent(OptionSetManager.class);
        boolean canUpdate = false;
        FieldConfigScheme.Builder newSchemeBuilder = new FieldConfigScheme.Builder(oldScheme);
        ArrayList<String> newOptions = new ArrayList<>();
        if (schemeName != null && !schemeName.toUpperCase().equals(oldScheme.getName().toUpperCase())) {
            canUpdate = true;
            newSchemeBuilder.setName(schemeName);
        }
        if (schemeDescription != null && !schemeDescription.equals(oldScheme.getDescription())) {
            canUpdate = true;
            newSchemeBuilder.setDescription(schemeDescription);
        }
        if (optionIDs != null) {
            Collection<String> oldOptionIds = optionSetManager.getOptionsForConfig(oldScheme.getOneAndOnlyConfig()).getOptionIds();
            if (oldOptionIds == null || optionIDs.isEmpty()) {
                optionIDs.forEach(newOptions::add);
                canUpdate = true;
            } else {
                oldOptionIds.forEach(newOptions::add);
                for (String optionID : optionIDs) {
                    if (!oldOptionIds.contains(optionID)) {
                        newOptions.add(optionID);
                        canUpdate = true;
                    }
                }
            }
        }
        if (canUpdate) {
            FieldConfigScheme newScheme = newSchemeBuilder.toFieldConfigScheme();
            return _issueTypeSchemeManager.update(newScheme, newOptions);
        }
        return oldScheme;
    }

    @Override
    public Boolean deleteIssueType(String typeId) {
        IssueTypeService.IssueTypeDeleteInput deleteParam = new IssueTypeService.IssueTypeDeleteInput(typeId, null);
        IssueTypeService.DeleteValidationResult validationResult = _issueTypeService.validateDeleteIssueType(_user, deleteParam);
        if (!validationResult.isValid())
            return false;
        _issueTypeService.deleteIssueType(_user, validationResult);
        return true;
    }

    @Override
    public Issue update(String userName, Long issueId, IssueInputParameters params) throws Exception {
        if (params == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        IssueService.UpdateValidationResult validationResult = _issueService.validateUpdate(_user, issueId, params);
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_ISSUE_UPDATEVALIDATEFAIL);
        }
        IssueService.IssueResult result = _issueService.update(_user, validationResult);
        return result.isValid() ? result.getIssue() : null;
    }

    @Override
    public Boolean delete(Long issueId) {
        IssueService.DeleteValidationResult validationResult = _issueService.validateDelete(_user, issueId);
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            return false;
        }
        ErrorCollection result = _issueService.delete(_user, validationResult);
        if (result.hasAnyErrors()) {
            log.error("Delete Issue Error: %d", issueId);
            result.getErrorMessages().forEach(e -> log.error(e));
            return false;
        }
        return true;
    }

    @Override
    public IssueInputParameters newInputParameters() {
        return _issueService.newIssueInputParameters();
    }

}
