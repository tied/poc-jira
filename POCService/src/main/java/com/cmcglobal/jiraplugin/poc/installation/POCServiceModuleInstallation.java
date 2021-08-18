/*
package com.cmcglobal.jiraplugin.poc.installation;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.installation.issue.PoCInstallation;
import com.cmcglobal.jiraplugin.poc.installation.project.ProjectInstallation;
import com.cmcglobal.jiraplugin.poc.installation.workflow.WorkflowInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({POCServiceModuleInstallation.class})
@Named
public class POCServiceModuleInstallation {
    private PoCInstallation _poCInstallation;
    private ProjectInstallation _projectInstallation;
    private WorkflowInstallation _workflowInstallation;

    @Inject
    public POCServiceModuleInstallation(PoCInstallation poCInstallation,
                                        ProjectInstallation projectInstallation,
                                        WorkflowInstallation workflowInstallation) throws Exception {
        _poCInstallation = poCInstallation;
        _projectInstallation = projectInstallation;
        _workflowInstallation = workflowInstallation;
    }

    public void Install() throws Exception {
        _projectInstallation.Install();
        _poCInstallation.Install();
        _workflowInstallation.Install();
    }
}
*/
