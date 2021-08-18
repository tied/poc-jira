package com.cmcglobal.jiraplugin.poc.jira.webwork;

/* 
    @created by 12:33 PM 8/13/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/

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
import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServletRequest;

public class IssueDetails extends JiraWebActionSupport {

    private WebResourceManager _resourceManager = ComponentAccessor.getWebResourceManager();
    private String _contentBody = "";
    private String _title = "";
    private UserManager userManager = ComponentAccessor.getUserManager();
    private ApplicationUser userLogged = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    private String errorMessage = "Forbidden.";
    private String projectId;
    Project _projectKey = ComponentAccessor.getProjectManager().getProjectByCurrentKey(Constants.projectKey);

    @Override
    protected String doExecute() throws Exception {
            HttpServletRequest request = getHttpRequest();
            String viewItem = request.getParameter("selectedItem");
            if (viewItem != null && viewItem.equalsIgnoreCase("dashboard")) {
                renderDashBoard();
            } else renderMainContent();

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
        return UtilHelper.getContextPathFromHttpRequest(getHttpRequest());
    }

    protected void renderMainContent() throws Exception {
        _contentBody = "";
        _title = "C-Now";
        this.renderDashBoard();
    }

    protected void renderDashBoard() throws Exception {
        _contentBody = "";

     /*   if (!isViewCustomer) {
            _contentBody = TemplateRenderService.renderErrorContent(403, errorMessage);
            return;
        }*/
        _title = "C-Now";
        VelocityContext dashBoardProvider = (new DashBoardProvider()).getContextMap();
//        boolean hasExportAble = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.CUSTOMER, PermissionKeyMapping.Permission.EXPORT);
//        dashBoardProvider.put("hasExportAble", hasExportAble);
        _contentBody = TemplateRenderService.renderDashBoardContent(dashBoardProvider);
    }

}
