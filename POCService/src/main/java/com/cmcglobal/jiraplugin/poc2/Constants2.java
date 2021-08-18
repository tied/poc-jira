package com.cmcglobal.jiraplugin.poc2;

/* 
    @created by 5:01 PM 8/11/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

public class Constants2 {
    public static final String BASE_URL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

    public static String projectName = "PoC 2 Service";
    public static String projectDescription = "Proof of Concept 2";
    public static String projectKey = "POC2";
    public static String projectLead = "crm-system";

    public static String allocateIssueTypeName = "Cấp Phát";
    public static String shoppingIssueTypeName = "Mua Sắm";
    public static String paymentIssueTypeName = "Thanh Toán";

    public static String pocIssueTypeSchemeName = "PoC 2 Service Issue Type Scheme";
    public static String allocateIssueTypeSchemeName = "Allocate Service Issue Type Scheme";
    public static String shoppingIssueTypeSchemeName = "Shopping Service Issue Type Scheme";
    public static String paymentIssueTypeSchemeName = "Payment Service Issue Type Scheme";

    public static String shoppingWorkflowName = "Standard Shopping FW";
    public static String allocateWorkflowName = "Standard Allocate FW";
    public static String paymentWorkflowName = "Standard Payment FW";

    public static String poc2WorkflowSchemeName = "Standard PoC 2 WF Scheme";
    public static String shoppingWorkflowSchemeName = "Standard Shopping WF Scheme";
    public static String allocateWorkflowSchemeName = "Standard Allocate WF Scheme";
    public static String paymentWorkflowSchemeName = "Standard Payment WF Scheme";

    public static String shoppingWFPathFile = "/jwbfiles/POC-2-Shopping-WF.jwb";
    public static String allocateWFPathFile = "/jwbfiles/POC-2-Allocate-WF.jwb";
    public static String paymentWFPathFile = "/jwbfiles/POC-2-Payment-WF.jwb";

    public static String shoppingScreenCreateName = "Shopping Create Screen";
    public static String shoppingScreenEditName = "Shopping Edit Screen";
    public static String shoppingScreenViewName = "Shopping View Screen";

    public static String allocateScreenCreateName = "Allocate Create Screen";
    public static String allocateScreenEditName = "Allocate Edit Screen";
    public static String allocateScreenViewName = "Allocate View Screen";

    public static String paymentScreenCreateName = "Payment Create Screen";
    public static String paymentScreenEditName = "Payment Edit Screen";
    public static String paymentScreenViewName = "Payment View Screen";

    public static String shoppingScreenSchemeName = "Shopping Screen Scheme";
    public static String allocateScreenSchemeName = "Allocate Screen Scheme";
    public static String paymentScreenSchemeName = "Payment Screen Scheme";

    public static String screenTabName = "Basic Info";

    public static String poc2ScreenIssueTypeSchemeName = "PoC 2 Issue Type Screen Scheme";
    public static String paymentScreenIssueTypeSchemeName = "Payment Issue Type Screen Scheme";
    public static String allocateScreenIssueTypeSchemeName = "Allocate Issue Type Screen Scheme";
    public static String shoppingScreenIssueTypeSchemeName = "Shopping Issue Type Screen Scheme";

    public static String paymentFieldConfigurationSchemeName = "Payment Field Configuration Scheme";
    public static String paymentFieldConfigurationName = "Payment Field Configuration";

    public static String allocateFieldConfigurationSchemeName = "Allocate Field Configuration Scheme";
    public static String allocateFieldConfigurationName = "Allocate Field Configuration";

    public static String shoppingFieldConfigurationSchemeName = "Shopping Field Configuration Scheme";
    public static String shoppingFieldConfigurationName = "Shopping Field Configuration";
}
