package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.NavigableField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface ICustomFieldUtils {
    CustomField create(String name,
                       @Nullable String description,
                       CustomFieldType<?, ?> customFieldType,
                       @Nullable List<String> optionsForMultiSelect,
                       @Nonnull List<Long> projectId,
                       Boolean isGlobal) throws Exception;

    CustomField update(CustomField customField, String name, String description, List<String> optionsForMultiSelect) throws Exception;

    Boolean delete(Long customFieldId);

    List<CustomFieldType<?, ?>> getAllCustomFieldType();

    Collection<NavigableField> getAllSystemField();

    Boolean lockCustomField(CustomField fieldToLock) throws Exception;

    Boolean unlockCustomField(CustomField fieldToUnlock) throws Exception;

    CustomField getCustomFieldByName(String name) throws Exception;

    CustomFieldType getCustomFieldTypeByKey(String typeKey);

    void createOption(CustomField customField, List<String> optionName) throws Exception;
}
