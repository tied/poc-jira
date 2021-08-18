/*
package com.cmcglobal.jiraplugin.poc.installation.project;

import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc.utils.IProjectUtils;
import com.cmcglobal.jiraplugin.poc.utils.installation.AbstractProjectInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ProjectInstallation.class})
@Named
public class ProjectInstallation extends AbstractProjectInstallation {

    @Inject
    public ProjectInstallation(IProjectUtils projectUtils) {
        super(projectUtils);
    }

    public void Install() throws Exception {
        ProjectType softwareType = _projectUtils.getAllProjectType().stream().filter(e -> e.getFormattedKey().contains("Business")).findFirst().get();
        Project pocProject = _projectUtils.create(
                Constants2.projectName,
                Constants2.projectLead,
                Constants2.projectKey,
                Constants2.projectDescription,
                softwareType.getKey(),
                null,
                Long.parseLong("10201"),
                null
        );
        _lisOfProjectCreated.put(Constants2.projectName, pocProject);

        ProjectRole tbpRole = _projectUtils.createProjectRole(Constants.tbpRole, Constants.tbpRole);
        ProjectRole adminRole = _projectUtils.createProjectRole(Constants.adminRole, Constants.adminRole);
        ProjectRole ceoRole = _projectUtils.createProjectRole(Constants.ceoRole, Constants.ceoRole);
        ProjectRole accountingRole = _projectUtils.createProjectRole(Constants.accountingRole, Constants.accountingRole);

        _listOfProjectRoleCreated.put(Constants.tbpRole, tbpRole);
        _listOfProjectRoleCreated.put(Constants.adminRole, adminRole);
        _listOfProjectRoleCreated.put(Constants.ceoRole, ceoRole);
        _listOfProjectRoleCreated.put(Constants.accountingRole, accountingRole);
    }
}
*/
