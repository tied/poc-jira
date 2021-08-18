package com.cmcglobal.jiraplugin.poc.utils.installation;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.cmcglobal.jiraplugin.poc.utils.IProjectUtils;

import java.util.HashMap;

public abstract class AbstractProjectInstallation implements IProjectInstallation {
    protected IProjectUtils _projectUtils;
    protected final HashMap<String, Project> _lisOfProjectCreated;
    protected final HashMap<String, ProjectRole> _listOfProjectRoleCreated;

    public AbstractProjectInstallation(
            IProjectUtils projectUtils
    ){
        _projectUtils = projectUtils;
        _lisOfProjectCreated = new HashMap<String, Project>();
        _listOfProjectRoleCreated = new HashMap<String, ProjectRole>();
    }

    public abstract void Install() throws Exception;

    public HashMap<String, Project> getListOfProjectCreated() {
        return _lisOfProjectCreated;
    }

    public HashMap<String,ProjectRole> getListOfProjectRoleCreated(){
        return _listOfProjectRoleCreated;
    }
}
