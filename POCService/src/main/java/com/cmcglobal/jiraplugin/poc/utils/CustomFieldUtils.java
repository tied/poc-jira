package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.customfield.CustomFieldService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItem;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemBuilder;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.NavigableField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ExportAsService({ICustomFieldUtils.class})
@Named
public class CustomFieldUtils implements ICustomFieldUtils {
    private static final Logger log = LoggerFactory.getLogger(CustomFieldUtils.class);

    private ManagedConfigurationItemService _managedConfigurationItemService;
    private CustomFieldService _customFieldService;
    private CustomFieldManager _customFieldManager;
    private ProjectManager _projectManager;
    private OptionsManager _optionsManager;
    private FieldManager _fieldManager;
    private UserManager _userManager;
    private ApplicationUser _user;

    public CustomFieldUtils() {
        _customFieldService = ComponentAccessor.getComponent(CustomFieldService.class);
        _customFieldManager = ComponentAccessor.getCustomFieldManager();
        _projectManager = ComponentAccessor.getProjectManager();
        _fieldManager = ComponentAccessor.getFieldManager();
        _userManager = ComponentAccessor.getUserManager();
        _optionsManager = ComponentAccessor.getOptionsManager();
        _managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService.class);
        _user = _userManager.getUserByName(UtilConstaints.USERNAME);
    }

    @Override
    public CustomField create(String name,
                              String description,
                              CustomFieldType<?, ?> customFieldType,
                              List<String> optionsForMultiSelect,
                              List<Long> projectId,
                              Boolean isGlobal) throws Exception {
        if (name == null ||
                customFieldType == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        Collection<CustomField> existsFields = _customFieldManager.getCustomFieldObjectsByName(name);
        if (existsFields != null && existsFields.size() > 0)
            return update(existsFields.stream().findFirst().get(),name,description,optionsForMultiSelect);
        ArrayList<JiraContextNode> contextNodes = new ArrayList<>();
        contextNodes.add(GlobalIssueContext.getInstance());
        CustomField ret = _customFieldManager.createCustomField(name, description, customFieldType, _customFieldManager.getDefaultSearcher(customFieldType), contextNodes, null);
        FieldConfigSchemeManager configSchemeManager = ComponentAccessor.getFieldConfigSchemeManager();
        FieldConfigScheme scheme = configSchemeManager.createDefaultScheme(ret, contextNodes);
        if (optionsForMultiSelect != null) {
            createOption(ret, optionsForMultiSelect);
        }
        log.info(UtilConstaints.CREATE_SUCCESS_FORMAT+ "CustomField", ret.getId(), ret.getName());
        return ret;
    }

    @Override
    public CustomField update(CustomField customField, String name, String description, List<String> optionsForMultiSelect) throws Exception {

        boolean canUpdate = false;
        if (description != null && description.equals(customField.getDescription())) {
            canUpdate = true;
            description = customField.getDescription();
        }
        ArrayList<String> newOptions = new ArrayList<>();
        List<Option> oldOptions = customField.getOptions(null, GlobalIssueContext.getInstance());
        for (String option : optionsForMultiSelect) {
            if (!oldOptions.stream().anyMatch(e -> e.getValue().equals(option))) {
                newOptions.add(option);
            }
        }
        if(name == null)
            name = customField.getName();
        if (newOptions.size() > 0)
            createOption(customField, newOptions);
        if (canUpdate)
            _customFieldManager.updateCustomField(customField.getIdAsLong(), name, description, _customFieldManager.getDefaultSearcher(customField.getCustomFieldType()));
        CustomField ret = _customFieldManager.getCustomFieldObject(customField.getIdAsLong());
        log.info(UtilConstaints.UPDATE_SUCCESS_FORMAT+ "CustomField", ret.getId(), ret.getName());
        return ret;
    }

    @Override
    public Boolean delete(Long customFieldId) {
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(_user);
        if (jiraServiceContext.getErrorCollection().hasAnyErrors())
            return false;
        CustomField removeItem = _customFieldManager.getCustomFieldObject(customFieldId);
        if (removeItem == null)
            return false;
        try {
            _customFieldManager.removeCustomField(removeItem);
        } catch (RemoveException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<CustomFieldType<?, ?>> getAllCustomFieldType() {
        return _customFieldManager == null ? null : _customFieldManager.getCustomFieldTypes();
    }

    @Override
    public Collection<NavigableField> getAllSystemField() {
        return _fieldManager.getNavigableFields();
    }

    @Override
    public Boolean lockCustomField(CustomField fieldToLock) throws Exception {
        ManagedConfigurationItem managedField = _managedConfigurationItemService.getManagedCustomField(fieldToLock);
        if (managedField != null) {
            ManagedConfigurationItemBuilder builder = ManagedConfigurationItemBuilder.builder(managedField);
            builder.setManaged(true);
            builder.setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED);
            ServiceOutcome<ManagedConfigurationItem> ret = _managedConfigurationItemService.updateManagedConfigurationItem(builder.build());
            if (ret.isValid()) {
                log.info("Lock CustomField: %s -- %s Successfully.", fieldToLock.getId(), fieldToLock.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean unlockCustomField(CustomField fieldToUnlock) throws Exception {
        ManagedConfigurationItem managedField = _managedConfigurationItemService.getManagedCustomField(fieldToUnlock);
        if (managedField != null) {
            ManagedConfigurationItemBuilder builder = ManagedConfigurationItemBuilder.builder(managedField);
            builder.setManaged(true);
            builder.setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.ADMIN);
            ServiceOutcome<ManagedConfigurationItem> ret = _managedConfigurationItemService.updateManagedConfigurationItem(builder.build());
            if (ret.isValid()) {
                log.info("Lock CustomField: %s -- %s Successfully.", fieldToUnlock.getId(), fieldToUnlock.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public CustomField getCustomFieldByName(String name) throws Exception {
        if (name == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        Collection<CustomField> existsFields = _customFieldManager.getCustomFieldObjectsByName(name);
        if (existsFields != null && existsFields.size() > 0)
            return existsFields.stream().findFirst().get();
        return null;
    }

    @Override
    public CustomFieldType getCustomFieldTypeByKey(String typeKey) {
        return _customFieldManager.getCustomFieldType(typeKey);
    }

    @Override
    public void createOption(CustomField customField, List<String> optionName) throws Exception {
        FieldConfigSchemeManager fieldConfigSchemeManager =
                ComponentAccessor.getComponent(FieldConfigSchemeManager.class);
        List<FieldConfigScheme> schemes =
                fieldConfigSchemeManager.getConfigSchemesForField(customField);
        if(schemes == null || schemes.isEmpty()) {
            FieldConfigSchemeManager configSchemeManager = ComponentAccessor.getFieldConfigSchemeManager();
            List<JiraContextNode> contextNodes = new ArrayList<>();
            contextNodes.add(GlobalIssueContext.getInstance());
            FieldConfigScheme scheme = configSchemeManager.createDefaultScheme(customField, contextNodes);
            FieldConfig config = scheme.getOneAndOnlyConfig();
            _optionsManager.createOptions(config, null, Long.parseLong("0"), optionName);
        }
        else {
            for (FieldConfigScheme scheme : schemes) {
                FieldConfig config = scheme.getOneAndOnlyConfig();
                _optionsManager.createOptions(config, null, Long.parseLong("0"), optionName);
            }
        }
    }

}
