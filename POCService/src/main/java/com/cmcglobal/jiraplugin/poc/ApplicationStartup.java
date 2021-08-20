package com.cmcglobal.jiraplugin.poc;

import com.atlassian.event.api.EventListener;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc.service.IssueUpdateService;
import com.cmcglobal.jiraplugin.poc.utils.UtilConstaints;
import com.cmcglobal.jiraplugin.poc2.Constants2;
import com.cmcglobal.jiraplugin.poc2.POC2ServiceModuleInstallation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Objects;

@Scanned
@ExportAsService({ApplicationStartup.class})
@Component("componentListener")
public class ApplicationStartup implements InitializingBean, DisposableBean {
    final static Logger logger = Logger.getLogger(ApplicationStartup.class);
    private POC2ServiceModuleInstallation _poc2ServiceModuleInstallation;
    IssueManager issueManager;
    IssueUpdateService issueUpdateService = new IssueUpdateService();
    UserManager userManager;

    @Inject

    public ApplicationStartup(
            POC2ServiceModuleInstallation poc2ServiceModuleInstallation
    ) {
        _poc2ServiceModuleInstallation = poc2ServiceModuleInstallation;
        issueManager = ComponentAccessor.getIssueManager();
        userManager = ComponentAccessor.getUserManager();
    }

    public void destroy() {
        // remove custom field

    }

    public void afterPropertiesSet() {
        // create custom fields
        //importWorkflow();
        try {
            Init();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        //testFunc();
    }


    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        //=======================phase 1============================
        Issue issue = issueEvent.getIssue();
        if (issue.getIssueType().getName().equals(Constants2.paymentIssueTypeName) ||
                issue.getIssueType().getName().equals(Constants2.shoppingIssueTypeName)) {
            onCreatePayment(issue, issueEvent);
        }
    }


    private void Init() throws Exception {
        _poc2ServiceModuleInstallation.Install();
    }

    private void onCreatePayment(Issue issue, IssueEvent issueEvent) {
        String issueTypeName = Objects.requireNonNull(issue.getIssueType()).getName();
        if (issueEvent.getEventTypeId() == EventType.ISSUE_CREATED_ID && issueTypeName.equalsIgnoreCase(Constants2.paymentIssueTypeName)) {
            ApplicationUser admin = userManager.getUserByName(UtilConstaints.USERNAME);
            String summary = issue.getSummary();
            String[] allocateIds = summary.split(",");
            for (int i = 0; i < allocateIds.length; i++) {
                Issue allocateIssue = issueManager.getIssueObject(allocateIds[i]);
                issueUpdateService.executeWorkflowTransition(allocateIssue, "Approve", "Pending Payment", admin);
            }
        }
    }

}