package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.admin.issuefields.configuration.AddFieldConfiguration;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;

import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ExportAsService({IFieldUtils.class})
@Named
public class FieldUtils implements IFieldUtils {

    private FieldLayoutManager _fieldLayoutManager;

    public FieldUtils(){
        _fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();
    }

    @Override
    public FieldLayoutScheme createFieldConfigurationScheme(String name, String description, Map<IssueType,EditableFieldLayout> listOfFieldConfiguration) throws Exception {
        List<FieldLayoutScheme> existsLayoutScheme = _fieldLayoutManager.getFieldLayoutSchemes();
        boolean isExistsFieldLayout = existsLayoutScheme.stream().anyMatch(e -> name.toUpperCase().equals(e.getName().toUpperCase()));
        if (isExistsFieldLayout){
            return updateFieldConfigurationScheme(
                    existsLayoutScheme.stream().filter(e -> name.toUpperCase().equals(e.getName().toUpperCase())).findFirst().get(),
                    name,
                    description,
                    listOfFieldConfiguration);
        }
        FieldLayoutScheme newLayout = _fieldLayoutManager.createFieldLayoutScheme(name, description);
        if (listOfFieldConfiguration != null && !listOfFieldConfiguration.isEmpty())
            for (IssueType iss : listOfFieldConfiguration.keySet()) {
                FieldLayoutSchemeEntity schemeEntity = new FieldLayoutSchemeEntityImpl(_fieldLayoutManager, null, ComponentAccessor.getConstantsManager());
                schemeEntity.setIssueTypeId(iss.getId());
                schemeEntity.setFieldLayoutId(listOfFieldConfiguration.get(iss).getId());
                schemeEntity.setFieldLayoutScheme(newLayout);
                _fieldLayoutManager.createFieldLayoutSchemeEntity(schemeEntity);
            }
        return _fieldLayoutManager.getMutableFieldLayoutScheme(newLayout.getId());
    }

    @Override
    public FieldLayoutScheme updateFieldConfigurationScheme(FieldLayoutScheme oldScheme, String name, String description, Map<IssueType, EditableFieldLayout> listOfFieldConfiguration) throws Exception {
        boolean canUpdate = false;
        if (name != null && !name.toUpperCase().equals(oldScheme.getName().toUpperCase())) {
            canUpdate = true;
            oldScheme.setName(name);
        }
        if (description != null && description != oldScheme.getDescription()) {
            oldScheme.setDescription(description);
            canUpdate = true;
        }
        if (canUpdate)
            _fieldLayoutManager.updateFieldLayoutScheme(oldScheme);
        ConstantsManager cManager = ComponentAccessor.getConstantsManager();
        Collection<FieldLayoutSchemeEntity> entities = oldScheme.getEntities();
        for (IssueType iss : listOfFieldConfiguration.keySet()) {
            boolean isExistsEntity = entities.stream().anyMatch(e -> iss.getId().equals(e.getIssueTypeId()));
            if (isExistsEntity)
                continue;
            else {
                FieldLayoutSchemeEntity schemeEntity = new FieldLayoutSchemeEntityImpl(_fieldLayoutManager, null, cManager);
                schemeEntity.setIssueTypeId(iss.getId());
                schemeEntity.setFieldLayoutId(listOfFieldConfiguration.get(iss).getId());
                schemeEntity.setFieldLayoutScheme(oldScheme);
                _fieldLayoutManager.createFieldLayoutSchemeEntity(schemeEntity);
            }
        }
        return _fieldLayoutManager.getMutableFieldLayoutScheme(oldScheme.getId());
    }

    /***
     *
     * @param name
     * @param description
     * @param options = Map < Name of field, Field Setting></>
     * @return
     * @throws Exception
     */
    @Override
    public EditableFieldLayout createFieldConfiguration(String name, String description, Map<String, FieldConfigurationOption> options) throws Exception {
        if(name == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        List<EditableFieldLayout> existsField = _fieldLayoutManager.getEditableFieldLayouts();
        boolean isExistsField = existsField.stream().anyMatch(e -> name.toUpperCase().equals(e.getName().toUpperCase()));
        if (isExistsField)
            return updateFieldConfiguration(existsField.stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get(), name, description, options);

        AddFieldConfiguration newFieldConfiguration = new AddFieldConfiguration(_fieldLayoutManager);
        newFieldConfiguration.setFieldLayoutName(name);
        if(description != null)
            newFieldConfiguration.setFieldLayoutDescription(description);
        try {
            newFieldConfiguration.execute();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        EditableFieldLayout ret = _fieldLayoutManager.getEditableFieldLayouts().stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get();
        if (options != null && !options.isEmpty()) {
            Collection<FieldLayoutItem> fieldLayoutItems = ret.getFieldLayoutItems();
            for (String key : options.keySet()) {
                boolean isExistsItem = fieldLayoutItems.stream().anyMatch(
                        e -> e.getOrderableField().getName().toUpperCase().equals(key.toUpperCase()) ||
                                e.getOrderableField().getNameKey().toUpperCase().equals(key.toUpperCase())
                );
                if(!isExistsItem)
                    continue;
                FieldLayoutItem item = fieldLayoutItems.stream().filter(
                        e -> e.getOrderableField().getName().toUpperCase().equals(key.toUpperCase()) ||
                                e.getOrderableField().getNameKey().toUpperCase().equals(key.toUpperCase())
                ).findFirst().get();
                switch (options.get(key)) {
                    case FieldConfigShow:
                        ret.show(item);
                        break;
                    case FieldConfigRequired:
                        ret.makeRequired(item);
                        break;
                    case FieldConfigHide:
                        ret.hide(item);
                        break;
                    case FieldConfigOptional:
                        ret.makeOptional(item);
                        break;
                    default:
                        throw new Exception(String.format("Field Configuration %s Option with name &s invalid.", options.get(key), key));
                }
            }
        }
        return _fieldLayoutManager.storeAndReturnEditableFieldLayout(ret);
    }

    @Override
    public EditableFieldLayout updateFieldConfiguration(EditableFieldLayout oldFieldConfiguration, String name, String description, Map<String, FieldConfigurationOption> options) throws Exception {
        EditableFieldLayout ret = _fieldLayoutManager.getEditableFieldLayouts().stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get();
        if (options != null && !options.isEmpty()) {
            Collection<FieldLayoutItem> fieldLayoutItems = ret.getFieldLayoutItems();
            for (String key : options.keySet()) {
                boolean isExistsItem = fieldLayoutItems.stream().anyMatch(
                        e -> e.getOrderableField().getName().toUpperCase().equals(key.toUpperCase()) ||
                                e.getOrderableField().getNameKey().toUpperCase().equals(key.toUpperCase())
                );
                if(!isExistsItem)
                    continue;
                FieldLayoutItem item = fieldLayoutItems.stream().filter(
                        e -> e.getOrderableField().getName().toUpperCase().equals(key.toUpperCase()) ||
                                e.getOrderableField().getNameKey().toUpperCase().equals(key.toUpperCase())
                ).findFirst().get();
                switch (options.get(key)) {
                    case FieldConfigShow:
                        ret.show(item);
                        break;
                    case FieldConfigRequired:
                        ret.makeRequired(item);
                        break;
                    case FieldConfigHide:
                        ret.hide(item);
                        break;
                    case FieldConfigOptional:
                        ret.makeOptional(item);
                        break;
                    default:
                        throw new Exception(String.format("Field Configuration %s Option with name &s invalid.", options.get(key), key));
                }
            }
        }
        return _fieldLayoutManager.storeAndReturnEditableFieldLayout(ret);
    }

    @Override
    public void addFieldConfigurationSchemeToProject(FieldLayoutScheme fieldLayoutScheme, Project project) {
        _fieldLayoutManager.addSchemeAssociation(project, fieldLayoutScheme.getId());
    }
}
