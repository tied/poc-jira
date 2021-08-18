package com.cmcglobal.jiraplugin.poc.jira.webwork;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.cmcglobal.jiraplugin.poc.Constants;
import com.cmcglobal.jiraplugin.poc.Provider.DashBoardProvider;
import com.cmcglobal.jiraplugin.poc.service.TemplateRenderService;
import com.cmcglobal.jiraplugin.poc.utils.UtilHelper;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServletRequest;

public class CNOWDashBoardAction extends JiraWebActionSupport {

    WebResourceManager _resourceManager = ComponentAccessor.getWebResourceManager();
    String _contentBody = "";
    String _title = "";
    ApplicationUser userLogged = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    String errorMessage = "Forbidden.";
    String projectId;
    Project _projectKey = ComponentAccessor.getProjectManager().getProjectByCurrentKey(Constants2.projectKey);
    String _urlPath = "";

    @Override
    public String doExecute() throws Exception {
        _urlPath = UtilHelper.getContextPathFromHttpRequest(getHttpRequest());
        if (userLogged != null) {
        HttpServletRequest request = getHttpRequest();
        String viewItem = request.getParameter("selectedItem");
        if (viewItem != null && viewItem.equalsIgnoreCase("dashboard")) {
            renderDashBoard();
        } else renderMainContent();
        } else _contentBody = TemplateRenderService.renderErrorContent(401, errorMessage);

        return "view";
    }

    public WebResourceManager getResourceManager() {
        return _resourceManager;
    }

    public String getProjectId() {
        if (projectId == null)
            projectId = _projectKey.getId().toString();
        return projectId;
    }

    public String getContentBody() {
        return _contentBody;
    }

    public String getUrlPath() {
        return _urlPath;
    }

    public void renderMainContent() throws Exception {
        _contentBody = "";
        _title = "C-Now";
        this.renderDashBoard();
    }

    public String getTitle() {
        return _title;
    }

    public void renderDashBoard() throws Exception {
        _contentBody = "";

     /*   if (!isViewCustomer) {
            _contentBody = TemplateRenderService.renderErrorContent(403, errorMessage);
            return;
        }*/
        _title = "C-Now";
        DashBoardProvider dashBoardProvider = new DashBoardProvider();
        VelocityContext velocityContext = dashBoardProvider.getContextMap();
        velocityContext.put("webResourceManager", ComponentAccessor.getWebResourceManager());
//        boolean hasExportAble = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.CUSTOMER, PermissionKeyMapping.Permission.EXPORT);
//        dashBoardProvider.put("hasExportAble", hasExportAble);
        _contentBody = TemplateRenderService.renderDashBoardContent(velocityContext);
    }

}