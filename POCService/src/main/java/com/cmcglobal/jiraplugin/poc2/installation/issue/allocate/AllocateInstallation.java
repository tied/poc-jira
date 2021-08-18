package com.cmcglobal.jiraplugin.poc2.installation.issue.allocate;

import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.utils.FieldConfigurationOption;
import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@ExportAsService({AllocateInstallation.class})
@Named
public class AllocateInstallation {
    private POC2ProjectInstallation _projectInstallation;
    private AllocateFieldInstallation _fieldInstallation;
    private AllocateScreenInstallation _screenInstallation;
    private AllocateIssueInstallation _issueInstallation;
    private IFieldUtils _fieldUtils;

    @Inject
    public AllocateInstallation(AllocateFieldInstallation fieldInstallation,
                                AllocateScreenInstallation screenInstallation,
                                AllocateIssueInstallation issueInstallation,
                                POC2ProjectInstallation projectInstallation,
                                IFieldUtils fieldUtils) {
        _fieldInstallation = fieldInstallation;
        _screenInstallation = screenInstallation;
        _issueInstallation = issueInstallation;
        _projectInstallation = projectInstallation;
        _fieldUtils = fieldUtils;
    }

    public void Install() throws Exception {
        _fieldInstallation.Install();
        _issueInstallation.Install();
        _screenInstallation.Install();

        Map<String, FieldConfigurationOption> fieldConfigOptions = new HashMap<>();
        EditableFieldLayout configurationAccountAndPassword = _fieldUtils.createFieldConfiguration(Constants2.allocateFieldConfigurationName, null, fieldConfigOptions);
        Map<IssueType, EditableFieldLayout> options = new HashMap<>();
        options.put(_issueInstallation.getListOfIssueTypeCreated().get(Constants2.allocateIssueTypeName), configurationAccountAndPassword);
        FieldLayoutScheme configurationScheme = _fieldUtils.createFieldConfigurationScheme(Constants2.allocateFieldConfigurationSchemeName, null, options);
        _fieldUtils.addFieldConfigurationSchemeToProject(configurationScheme, _projectInstallation.getListOfProjectCreated().get(Constants2.projectName));
    }
}
