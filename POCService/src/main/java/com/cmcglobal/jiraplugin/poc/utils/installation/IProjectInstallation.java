package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;

import java.util.HashMap;

public interface IProjectInstallation {
    void Install() throws Exception;

    HashMap<String, Project> getListOfProjectCreated();

    HashMap<String, ProjectRole> getListOfProjectRoleCreated();
}
