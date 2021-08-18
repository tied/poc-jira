package com.cmcglobal.jiraplugin.poc.jira.webwork;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

/*
    @created by 5:47 PM 8/13/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/
public class WebItemCondition extends AbstractWebCondition {
    public boolean shouldDisplay(ApplicationUser currentUser, JiraHelper jiraHelper){

//        _permissionService = new PermissionService(currentUser.getUsername(), false);
//        boolean hasPermission = false;
//
//        try {
//            boolean isViewMasterData = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.MASTERDATA, PermissionKeyMapping.Permission.VIEW);
//            boolean isViewPlanningSales = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.PLANNINGSALE, PermissionKeyMapping.Permission.VIEW);
//            boolean isViewPlanningRevenue = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.PLANNING, PermissionKeyMapping.Permission.VIEW);
//            boolean isViewPlanningHistory = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.PLANNINGHISTORY, PermissionKeyMapping.Permission.VIEW);
//            boolean isViewContact = _permissionService.hasViewContact();
//            boolean isViewPipeline = _permissionService.hasViewPipeline();
//            boolean isViewCustomer = _permissionService.hasViewCustomer();
//            boolean isViewImportData = _permissionService.hasPermissionOnPage(PermissionKeyMapping.Page.IMPORTDATA, PermissionKeyMapping.Permission.VIEW);
//
//            if (isViewMasterData || isViewPlanningSales || isViewPlanningRevenue || isViewPlanningHistory
//                    || isViewContact || isViewPipeline || isViewCustomer || isViewImportData){
//                hasPermission = true;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return hasPermission;

        return true;
    }
}
