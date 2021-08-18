package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleImpl;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.List;

@ExportAsService({IProjectUtils.class})
@Named
public class ProjectUtils implements IProjectUtils {
    private static final Logger log = LoggerFactory.getLogger(ProjectUtils.class);

    private ProjectManager _projectManager;
    private ProjectService _projectService;
    private UserManager _userManager;
    private ProjectTypeManager _projectTypeManager;
    private ProjectRoleManager _projectRoleManager;
    private ApplicationUser _user;

    public ProjectUtils() {
        _projectManager = ComponentAccessor.getProjectManager();
        _projectService = ComponentAccessor.getComponent(ProjectService.class);
        _userManager = ComponentAccessor.getUserManager();
        _projectTypeManager = ComponentAccessor.getComponent(ProjectTypeManager.class);
        _projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
        _user = _userManager.getUserByName(UtilConstaints.USERNAME);
    }

    @Override
    public Project create(
            String name,
            String userNameOfLead,
            String key,
            String description,
            ProjectTypeKey projectTypeKey,
            Long assigneeTypeId,
            Long avatarId,
            String url) throws Exception {
        if (name == null ||
                key == null ||
                userNameOfLead == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        Project existsProject = _projectManager.getProjectObjByName(name);
        ApplicationUser user = _userManager.getUserByName(userNameOfLead);
        if (user == null)
            throw new Exception(UtilConstaints.ERROR_USERNOTFOUND);
        if (existsProject != null)
            return existsProject;
        ProjectCreationData.Builder newProjectBuilder = new ProjectCreationData.Builder();
        newProjectBuilder.withName(name)
                .withLead(user)
                .withKey(key.toUpperCase())
                .withType(projectTypeKey);
        if (assigneeTypeId != null)
            newProjectBuilder.withAssigneeType(assigneeTypeId);
        if (avatarId != null)
            newProjectBuilder.withAvatarId(avatarId);
        if (url != null)
            newProjectBuilder.withUrl(url);
        if (description != null)
            newProjectBuilder.withDescription(description);
        ProjectCreationData newProject = newProjectBuilder.build();
        ProjectService.CreateProjectValidationResult validationResult = _projectService.validateCreateProject(_user, newProject);
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_PROJECT_CREATEVALIDATEFAIL);
        }
        Project ret = _projectService.createProject(validationResult);
        log.info("Create project: %s -- %s Successful.", ret.getKey(), ret.getName());
        return ret;
    }

    @Override
    public Boolean delete(String key) throws Exception {
        ProjectService.DeleteProjectValidationResult validationResult = _projectService.validateDeleteProject(_user, key);
        if (!validationResult.isValid()) {
            validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
            throw new Exception(UtilConstaints.ERROR_PROJECT_DELETEVALIDATEFAIL);
        }
        ProjectService.DeleteProjectResult ret = _projectService.deleteProject(_user, validationResult);
        if (ret.isValid())
            return true;
        return false;
    }

    @Override
    public List<ProjectType> getAllProjectType() {
        return _projectTypeManager.getAllProjectTypes();
    }

    @Override
    public Project update(Project oldProject, String name, String userNameOfLead, String key, String description, String url, Long assigneeType) throws Exception {
        if (oldProject == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        boolean canUpdate = false;
        ProjectService.UpdateProjectRequest updateRequest = new ProjectService.UpdateProjectRequest(oldProject);
        if (name != null && !name.toUpperCase().equals(oldProject.getName().toUpperCase())) {
            updateRequest.name(name);
            canUpdate = true;
        }
        if (userNameOfLead != null && !userNameOfLead.toUpperCase().equals(oldProject.getLeadUserName().toUpperCase())) {
            ApplicationUser lead = _userManager.getUserByName(userNameOfLead);
            if (lead == null)
                throw new Exception(UtilConstaints.ERROR_USERNOTFOUND);
            updateRequest.leadUserKey(lead.getKey());
            canUpdate = true;
        }
        if (key != null && !key.toUpperCase().equals(oldProject.getKey().toUpperCase())) {
            updateRequest.key(key);
            canUpdate = true;
        }
        if (description != null && !description.equals(oldProject.getDescription())) {
            updateRequest.description(description);
            canUpdate = true;
        }
        if (url != null && !url.equals(oldProject.getUrl())) {
            updateRequest.url(url);
            canUpdate = true;
        }
        if (assigneeType != null && assigneeType != oldProject.getAssigneeType()) {
            updateRequest.assigneeType(assigneeType);
            canUpdate = true;
        }
        if(canUpdate) {
            ProjectService.UpdateProjectValidationResult validationResult = _projectService.validateUpdateProject(_user, updateRequest);
            if (!validationResult.isValid()) {
                validationResult.getErrorCollection().getErrorMessages().forEach(e -> log.error(e));
                throw new Exception(UtilConstaints.ERROR_PROJECT_UPDATEVALIDATEFAIL);
            }
            return _projectService.updateProject(validationResult);
        }
        return oldProject;
    }

    @Override
    public ProjectRole createProjectRole(String name, @Nullable String description) throws Exception {
        if(name == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        if(_projectRoleManager.getProjectRoles().stream().anyMatch(e->name.toUpperCase().equals(e.getName().toUpperCase()))){
            return updateProjectRole(
                    _projectRoleManager.getProjectRoles().stream().filter(e -> name.toUpperCase().equals(e.getName().toUpperCase())).findFirst().get(),
                    name,
                    description);
        }
        ProjectRole newRole = new ProjectRoleImpl(name, description);
        _projectRoleManager.createRole(newRole);
        return newRole;
    }

    @Override
    public ProjectRole updateProjectRole(ProjectRole oldRole, @Nullable String name, @Nullable String description) {
        boolean canUpdate = false;
        ProjectRoleImpl.Builder newRoleBuilder = ProjectRoleImpl.Builder.from(oldRole);
        if (name != null && !name.toUpperCase().equals(oldRole.getName().toUpperCase())) {
            canUpdate = true;
            newRoleBuilder.name(name);
        }
        if (description != null && !description.equals(oldRole.getDescription())){
            canUpdate = true;
            newRoleBuilder.description(description);
        }
        if(canUpdate) {
            _projectRoleManager.updateRole(newRoleBuilder.build());
            return _projectRoleManager.getProjectRole(oldRole.getId());
        }
        return oldRole;
    }

    @Override
    public ProjectCategory createProjectCategory(String name, String description) throws Exception {
        if(name == null)
            throw new Exception(UtilConstaints.ERROR_PARAMINPUTINVALID);
        boolean isCategoryExists = _projectManager.getAllProjectCategories().stream().anyMatch(e -> name.toUpperCase().equals(e.getName().toUpperCase()));
        if(isCategoryExists)
            return _projectManager.getAllProjectCategories().stream().filter(e -> name.toUpperCase().equals(e.getName().toUpperCase())).findFirst().get();
        return _projectManager.createProjectCategory(name, description);
    }

    @Override
    public void AddProjectToProjectCategory(Project project, ProjectCategory projectCategory) {
        _projectManager.setProjectCategory(project, projectCategory);
    }
}
