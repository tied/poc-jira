/*
package com.cmcglobal.jiraplugin.poc.installation.issue;

import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.installation.project.ProjectInstallation;
import com.cmcglobal.jiraplugin.poc.utils.FieldConfigurationOption;
import com.cmcglobal.jiraplugin.poc.utils.IFieldUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@ExportAsService({PoCInstallation.class})
@Named
public class PoCInstallation {
    private ProjectInstallation _projectInstallation;
    private FieldInstallation _fieldInstallation;
    private ScreenInstallation _screenInstallation;
    private IssueInstallation _issueInstallation;
    private IFieldUtils _fieldUtils;

    @Inject
    public PoCInstallation(FieldInstallation fieldInstallation,
                           ScreenInstallation screenInstallation,
                           IssueInstallation issueInstallation,
                           ProjectInstallation projectInstallation,
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
        EditableFieldLayout configurationAccountAndPassword = _fieldUtils.createFieldConfiguration(Constants.fieldConfigurationName, null, fieldConfigOptions);
        Map<IssueType, EditableFieldLayout> options = new HashMap<>();
        options.put(_issueInstallation.getListOfIssueTypeCreated().get(Constants.issueTypeName), configurationAccountAndPassword);
        FieldLayoutScheme configurationScheme = _fieldUtils.createFieldConfigurationScheme(Constants.fieldConfigurationSchemeName, null, options);
        _fieldUtils.addFieldConfigurationSchemeToProject(configurationScheme, _projectInstallation.getListOfProjectCreated().get(Constants.projectName));
    }
}
*/
