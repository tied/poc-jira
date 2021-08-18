package com.cmcglobal.jiraplugin.poc.utils;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.gzipfilter.org.apache.commons.lang.StringUtils;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.workflow.WorkflowService;
import com.atlassian.jira.bc.workflow.events.WorkflowPublished;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItem;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemBuilder;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.permission.ProjectPermissionHelper;
import com.atlassian.jira.plugins.workflow.sharing.importer.JiraWorkflowSharingImporter;
import com.atlassian.jira.plugins.workflow.sharing.importer.SharedWorkflowImportPlan;
import com.atlassian.jira.plugins.workflow.sharing.importer.component.WorkflowImporterFactory;
import com.atlassian.jira.plugins.workflow.sharing.importer.component.WorkflowStatusHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.util.workflow.WorkflowEditorTransitionConditionUtil;
import com.atlassian.jira.workflow.*;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.opensymphony.workflow.loader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.plugins.workflow.sharing.importer.component.DefaultWorkflowBundleFactory;
import com.atlassian.jira.plugins.workflow.sharing.importer.component.WorkflowBundle;
import com.atlassian.plugin.util.ClassLoaderUtils;
import org.apache.commons.io.IOUtils;
import com.atlassian.jira.plugins.workflow.sharing.importer.StatusMapping;
import com.atlassian.sal.api.message.I18nResolver;

import javax.inject.Named;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExportAsService({IWorkflowUtils.class})
@Named
public class WorkflowUtils implements IWorkflowUtils {
    private static final Logger log = LoggerFactory.getLogger(WorkflowUtils.class);

    private PluginAccessor _pluginAccessor;
    private WorkflowManager _workflowManager;
    private UserManager _userManager;
    private WorkflowSchemeManager _workflowSchemeManager;
    private ApplicationUser _user;
    private IssueTypeManager _issueTypeManager;
    private ManagedConfigurationItemService _managedConfigurationItemService;
    private I18nResolver _i18nResolver;
    private WorkflowImporterFactory _workflowImporterFactory;
    private WorkflowStatusHelper _workflowStatusHelper;
    private EventPublisher _eventPublisher;
    private WorkflowService _workflowService;

    public WorkflowUtils() {
        _pluginAccessor = ComponentAccessor.getPluginAccessor();
        _workflowManager = ComponentAccessor.getWorkflowManager();
        _userManager = ComponentAccessor.getUserManager();
        _workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager();
        _issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
        _managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService.class);
        _user = _userManager.getUserByName(UtilConstaints.USERNAME);
        _workflowImporterFactory = ComponentAccessor.getOSGiComponentInstanceOfType(WorkflowImporterFactory.class);
        _i18nResolver = ComponentAccessor.getOSGiComponentInstanceOfType(I18nResolver.class);
        _workflowStatusHelper = ComponentAccessor.getOSGiComponentInstanceOfType(WorkflowStatusHelper.class);
        _eventPublisher = ComponentAccessor.getComponent(EventPublisher.class);
        _workflowService = ComponentAccessor.getComponent(WorkflowService.class);

    }

    @Override
    public JiraWorkflow ImportFromXMLFile(String pathOfFile, String workflowName) throws Exception {
        if (pathOfFile == null ||
                workflowName == null)
            throw new Exception(UtilConstaints.ERROR_FILENOTFOUND);
        boolean isExistsWorkflow = _workflowManager.getActiveWorkflows().stream().anyMatch(e -> e.getName().equals(workflowName));
        if (isExistsWorkflow)
            return _workflowManager.getActiveWorkflows().stream().filter(e -> e.getName().equals(workflowName)).findFirst().get();
        try {
            //JiraWorkflow existsWorkflow = _workflowManager
            WorkflowDescriptor workflowDescriptor;
            InputStream inputStream = _pluginAccessor.getDynamicResourceAsStream(pathOfFile);
            workflowDescriptor = WorkflowLoader.load(inputStream, true);
            ConfigurableJiraWorkflow newWorkflow = new ConfigurableJiraWorkflow(workflowName, _workflowManager);
            newWorkflow.setDescriptor(workflowDescriptor);
            _workflowManager.createWorkflow(_user, newWorkflow);
            return newWorkflow;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(UtilConstaints.ERROR_IMPORTXMLFAIL);
        }
    }

    @Override
    public Boolean lockWorkflow(JiraWorkflow workflowToLock) {
        ManagedConfigurationItem mngWorkflow = _managedConfigurationItemService.getManagedWorkflow(workflowToLock);
        if (mngWorkflow != null) {
            ManagedConfigurationItemBuilder builder = ManagedConfigurationItemBuilder.builder(mngWorkflow);
            builder.setManaged(true);
            builder.setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED);
            ServiceOutcome<ManagedConfigurationItem> ret = _managedConfigurationItemService.updateManagedConfigurationItem(builder.build());
            if (ret.isValid()) {
                log.info("Lock Workflow: %s Successfully.", workflowToLock.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean lockWorkflowScheme(AssignableWorkflowScheme workflowSchemeToLock) {
        ManagedConfigurationItem mngWorkflowScheme = _managedConfigurationItemService.getManagedWorkflowScheme(workflowSchemeToLock);
        if (mngWorkflowScheme != null) {
            ManagedConfigurationItemBuilder builder = ManagedConfigurationItemBuilder.builder(mngWorkflowScheme);
            builder.setManaged(true);
            builder.setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED);
            ServiceOutcome<ManagedConfigurationItem> ret = _managedConfigurationItemService.updateManagedConfigurationItem(builder.build());
            if (ret.isValid()) {
                log.info("Lock Workflow Scheme: %s Successfully.", workflowSchemeToLock.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public void addWorkflowToScheme(JiraWorkflow workflow, AssignableWorkflowScheme workflowScheme, List<String> issueTypeId) {
        AssignableWorkflowScheme.Builder updateSchemeBuilder = workflowScheme.builder();
        Map<String, String> listOfIssueType = new HashMap<String, String>();
        issueTypeId.forEach(e -> listOfIssueType.put(e, workflow.getName()));
        Map<String, String> existsMap = workflowScheme.getMappings();
        Map<String, String> newMap = new HashMap<String, String>();
        for (String key : existsMap.keySet()) {
            if(!issueTypeId.contains(key))
                newMap.put(key, existsMap.get(key));
        }
        issueTypeId.forEach(e -> newMap.put(e, workflow.getName()));
        updateSchemeBuilder.setMappings(newMap);
        AssignableWorkflowScheme ret = _workflowSchemeManager.updateWorkflowScheme(updateSchemeBuilder.build());
    }

    @Override
    public AssignableWorkflowScheme createWorkflowScheme(String name, String description, JiraWorkflow workflow, List<String> issueTypeIds) throws Exception {
        if (workflow == null)
            throw new Exception(UtilConstaints.ERROR_WORKFLOW_NOTFOUND);
        List<AssignableWorkflowScheme> schemes = new ArrayList<>();
        _workflowSchemeManager.getAssignableSchemes().forEach(schemes::add);
        boolean isExistsScheme = schemes.stream().anyMatch(e -> e.getName().toUpperCase().equals(name.toUpperCase()));
        if (isExistsScheme) {
            addWorkflowToScheme(workflow, schemes.stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get(), issueTypeIds);
            return _workflowSchemeManager.getWorkflowSchemeObj(schemes.stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get().getId());
        }
        AssignableWorkflowScheme.Builder newScheme = _workflowSchemeManager.assignableBuilder();
        newScheme.setName(name);
        if (description != null)
            newScheme.setDescription(description);
        newScheme.setDefaultWorkflow(workflow.getName());
        if(issueTypeIds != null && issueTypeIds.size() > 0) {
            Map<String, String> listOfIssueType = newScheme.getMappings();
            issueTypeIds.forEach(e -> listOfIssueType.put(e, workflow.getName()));
            newScheme.setMappings(listOfIssueType);
        }
        AssignableWorkflowScheme ret = _workflowSchemeManager.createScheme(newScheme.build());
        log.info(UtilConstaints.CREATE_SUCCESS_FORMAT+ "Workflow Scheme", ret.getId().toString(), ret.getName());
        return ret;
    }



    @Override
    public void addWorkflowSchemeToProject(AssignableWorkflowScheme workflowScheme, Project project) {
        Stream<Scheme> wSchemeSearcher = _workflowSchemeManager.getSchemeObjects().stream().filter(e -> e.getName().equals(workflowScheme.getName()));
        if (wSchemeSearcher != null && wSchemeSearcher.count() > 0) {
            Scheme wScheme = _workflowSchemeManager.getSchemeObjects().stream().filter(e -> e.getName().equals(workflowScheme.getName())).findFirst().get();
            Scheme existsScheme = _workflowSchemeManager.getSchemeFor(project);
            if (existsScheme != null && existsScheme.getName().equals(workflowScheme.getName()))
                return;
            _workflowSchemeManager.addSchemeToProject(project, wScheme);
        }
    }

    @Override
    public JiraWorkflow getWorkflowByName(String name) throws Exception {
        Stream<JiraWorkflow> existsWorkflow = _workflowManager.getWorkflows().stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase()));
        if (existsWorkflow != null && existsWorkflow.count() > 0)
            return _workflowManager.getWorkflows().stream().filter(e -> e.getName().toUpperCase().equals(name.toUpperCase())).findFirst().get();
        throw new Exception("Workflow doesn't exists.");
    }

    @Override
    public JiraWorkflow runImportFromJWBFile(String workflowName, String pathJWBFile) throws Exception {
        if (_workflowManager.getWorkflows().stream().anyMatch(e -> e.getName().equalsIgnoreCase(workflowName))) {
            return _workflowManager.getWorkflows().stream().filter(e -> e.getName().equalsIgnoreCase(workflowName)).findFirst().get();
        }

        PluginAccessor pluginAccessor = ComponentAccessor.getPluginAccessor();
        WorkflowBundle.Factory bundleFactory = new DefaultWorkflowBundleFactory(_i18nResolver);

        try {

            JiraWorkflowSharingImporter importer = _workflowImporterFactory.newImporter();
            InputStream inputStream = ClassLoaderUtils.getResourceAsStream(pathJWBFile, WorkflowUtils.class);

            try {
                JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext();
                authContext.setLoggedInUser(ComponentAccessor.getUserManager().getUserByName(UtilConstaints.USERNAME));

                SharedWorkflowImportPlan plan = new SharedWorkflowImportPlan(pluginAccessor, bundleFactory.bundle(inputStream, WorkflowBundle.BundleSource.MANUAL));
                Map<String, StatusMapping> statusHolders = _workflowStatusHelper.getStatusHolders(plan.getWorkflowXml(), plan.getStatusInfo());
                List statusMappings = new ArrayList<>(statusHolders.values());
                plan.setStatusMappings(statusMappings);
                plan.setWorkflowName(workflowName);

                importer.importWorkflow(plan);
                return _workflowManager.getWorkflow(workflowName);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }

        } catch (Exception ex) {
            throw new Exception("Not found JWB file. Please check again!!!");
        }
    }

    @Override
    public JiraWorkflow addPostFunctionToTransition(
            JiraWorkflow workflow,
            String transitionName,
            String toStep,
            String postFunctionKey,
            Class<?> postFunctionClass,
            Map<String, ?> passParams
    ) throws Exception {

        List<StepDescriptor> stepDescriptors = workflow.getDescriptor().getSteps();
        if (stepDescriptors.stream().anyMatch(e -> e.getName().equalsIgnoreCase(toStep))) {
            StepDescriptor stepDescriptor = stepDescriptors.stream().filter(e -> e.getName().equalsIgnoreCase(toStep)).findFirst().get();
            boolean anyMatch = workflow.getActionsWithResult(stepDescriptor).stream().anyMatch(e -> e.getName().equalsIgnoreCase(transitionName));
            if (anyMatch) {
                JiraWorkflow workflowDraft = getEditableWorkflow(workflow);

                List<ActionDescriptor> actionDescriptors = workflowDraft.getActionsWithResult(stepDescriptor).stream().filter(e -> e.getName().equalsIgnoreCase(transitionName)).collect(Collectors.toList());

                for (ActionDescriptor actionDescriptor : actionDescriptors) {
                    List<FunctionDescriptor> functions = actionDescriptor.getUnconditionalResult().getPostFunctions();
                    if (functions.stream().anyMatch(e -> e.getArgs().containsKey("class.name") && e.getArgs().get("class.name").equals(postFunctionClass.getTypeName())))
                        continue;

                    FunctionDescriptor functionDescriptor = DescriptorFactory.getFactory().createFunctionDescriptor();
                    functionDescriptor.setType("class");
                    functionDescriptor.getArgs().put("class.name", postFunctionClass.getTypeName());
                    functionDescriptor.getArgs().put("full.module.key", postFunctionKey);
                    if (passParams != null && passParams.size() > 0) {
                        functionDescriptor.getArgs().putAll(passParams);
                    }
                    ResultDescriptor unconditionalResult = actionDescriptor.getUnconditionalResult();
                    if (unconditionalResult == null) {
                        unconditionalResult = DescriptorFactory.getFactory().createResultDescriptor();
                        actionDescriptor.setUnconditionalResult(unconditionalResult);
                    }
                    List postFunctions = unconditionalResult.getPostFunctions();
                    postFunctions.add(0, functionDescriptor);
                }

                WorkflowService workflowService = ComponentAccessor.getComponent(WorkflowService.class);
                workflowService.updateWorkflow(new JiraServiceContextImpl(_user), workflowDraft);
                createBackupAndPublicWorkflow(workflowDraft.getName(), false);
                return workflowDraft;
            }
            throw new Exception("Not found action transition " + transitionName);
        }
        throw new Exception("Not found step name " + toStep);
    }

    @Override
    public JiraWorkflow addValidatorToTransition(JiraWorkflow workflow,
                                                 String transitionName,
                                                 String toStep,
                                                 Class<?> validatorClass,
                                                 Map<String, ?> passParams) throws Exception {

        List<StepDescriptor> stepDescriptors = workflow.getDescriptor().getSteps();

        if (!stepDescriptors.stream().anyMatch(e -> e.getName().equalsIgnoreCase(toStep))) {
            throw new Exception("Not found step name " + toStep);
        }

        StepDescriptor stepDescriptor = stepDescriptors.stream().filter(e -> e.getName().equalsIgnoreCase(toStep)).findFirst().get();

        JiraWorkflow workflowDraft = null;
        if (workflow.hasDraftWorkflow()) {
            if (workflow.isDraftWorkflow()) workflowDraft = workflow;
            else workflowDraft = _workflowManager.getDraftWorkflow(workflow.getName());
        } else {
            workflowDraft = _workflowManager.createDraftWorkflow(_user, workflow.getName());
        }

        boolean anyMatch = workflowDraft.getActionsWithResult(stepDescriptor).stream().anyMatch(e -> e.getName().equalsIgnoreCase(transitionName));
        List<ActionDescriptor> theList = new ArrayList(workflowDraft.getActionsWithResult(stepDescriptor));
//        Collections.reverse(theList);
        List<ActionDescriptor> actionDescriptors = theList.stream().filter(e -> e.getName().equalsIgnoreCase(transitionName)).collect(Collectors.toList());
        for (ActionDescriptor actionDescriptor : actionDescriptors) {
            if (!anyMatch || actionDescriptor == null) {
                throw new Exception("Not found action transition " + transitionName);
            }

            ValidatorDescriptor validatorDescriptor = DescriptorFactory.getFactory().createValidatorDescriptor();
            validatorDescriptor.setType("class");
            validatorDescriptor.getArgs().put("class.name", validatorClass.getTypeName());

            if (passParams != null && passParams.size() > 0) {
                validatorDescriptor.getArgs().putAll(passParams);
            }

            List<ValidatorDescriptor> validators = actionDescriptor.getValidators();

            if (validators == null) {
                throw new Exception("Validators collection is null for workflow '" + workflowDraft.getName() + "' step '" + toStep + "' transition '" + transitionName + "'.");
            }
            if (validators.size() == 0 || validators.stream().anyMatch(e -> !e.getArgs().containsKey("class.name") || !e.getArgs().get("class.name").equals(validatorClass.getTypeName()))) {
                actionDescriptor.getValidators().add(validatorDescriptor);
            }
        }
        WorkflowService workflowService = ComponentAccessor.getComponent(WorkflowService.class);
        workflowService.updateWorkflow(new JiraServiceContextImpl(_user), workflowDraft);
        this.createBackupAndPublicWorkflow(workflowDraft.getName(), false);
        return workflowDraft;
    }

    @Override
    public JiraWorkflow createBackupAndPublicWorkflow(String workflowName, boolean isCloneBackup) throws Exception {
        JiraWorkflow workflow = this.getWorkflowByName(workflowName);
        JiraWorkflow workflowDraft = null;
        if (workflow.isDraftWorkflow()) {
            workflowDraft = workflow;
        } else {
            if (workflow.hasDraftWorkflow()) {
                workflowDraft = _workflowManager.getDraftWorkflow(workflow.getName());
            }
        }

        if (workflowDraft != null) {
            String originalWorkflowName = workflowDraft.getName();
            if (isCloneBackup) {
                String newWorkflowName = "Backup of " + originalWorkflowName + " - (" + (new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(Calendar.getInstance().getTime())) + ")";
                JiraWorkflow jiraWorkflowActive = _workflowManager.getWorkflow(originalWorkflowName);
                JiraWorkflow copyJiraWorkflow = _workflowManager.copyWorkflow(_user, StringUtils.trim(newWorkflowName), (String) null, jiraWorkflowActive);
            }

            ProjectWorkflowSchemeHelper projectWorkflowSchemeHelper = ComponentAccessor.getComponent(ProjectWorkflowSchemeHelper.class);
            ProjectPermissionHelper projectPermissionHelper = ComponentAccessor.getComponent(ProjectPermissionHelper.class);
            boolean isWorkflowIsolated = projectWorkflowSchemeHelper.isWorkflowIsolated(originalWorkflowName);
            _workflowManager.overwriteActiveWorkflow(_user, originalWorkflowName);
            List<Project> allProjectsForWorkflow = projectWorkflowSchemeHelper.getAllProjectsForWorkflow(originalWorkflowName);
            _eventPublisher.publish(
                    new WorkflowPublished(
                            isWorkflowIsolated,
                            true,
                            projectPermissionHelper.hasExtPermission(allProjectsForWorkflow),
                            originalWorkflowName,
                            allProjectsForWorkflow.size() == 1 ? (allProjectsForWorkflow.iterator().next()).getId() : null
                    )
            );
            return workflow;
        }
        return null;
    }

    private JiraWorkflow getEditableWorkflow(JiraWorkflow workflow) {
        if (workflow.hasDraftWorkflow()) {
            if (workflow.isDraftWorkflow())
                return workflow;
            else
                return _workflowManager.getDraftWorkflow(workflow.getName());
        } else {
            return _workflowManager.createDraftWorkflow(_user, workflow.getName());
        }
    }

    @Override
    public JiraWorkflow addConditionToTransition(JiraWorkflow workflow, String transitionName, String toStep, Class<?> conditionClass) throws Exception {
        JiraWorkflow workflowDraft = getEditableWorkflow(workflow);
        List<StepDescriptor> stepDescriptors = workflowDraft.getDescriptor().getSteps();
        if (!stepDescriptors.stream().anyMatch(e -> e.getName().equalsIgnoreCase(toStep))) {
            throw new Exception("Unable to add condition to transition.");
        }

        StepDescriptor stepDescriptor = stepDescriptors.stream().filter(e -> e.getName().equalsIgnoreCase(toStep)).findFirst().get();

        boolean anyMatch = workflowDraft.getActionsWithResult(stepDescriptor).stream().anyMatch(e -> e.getName().equalsIgnoreCase(transitionName));

        if (!anyMatch) {
            throw new Exception("Unable to find transaction with name: " + transitionName);
        }

        DescriptorFactory descriptorFactory = DescriptorFactory.getFactory();
        List<ActionDescriptor> actionDescriptors = workflowDraft.getActionsWithResult(stepDescriptor).stream().filter(e -> e.getName().equalsIgnoreCase(transitionName)).collect(Collectors.toList());

        ConditionDescriptor conditionDescriptor = null;
        for (ActionDescriptor actionDescriptor : actionDescriptors) {
            if (actionDescriptor.getRestriction() != null && actionDescriptor.getRestriction().getConditionsDescriptor() != null) {
                List<ConditionDescriptor> conditionDescriptors = actionDescriptor.getRestriction().getConditionsDescriptor().getConditions();
                if (conditionDescriptors.stream().anyMatch(e -> e.getArgs().containsKey("class.name") && e.getArgs().get("class.name").equals(conditionClass.getTypeName()))) {
                    continue;
                }
            }
            conditionDescriptor = descriptorFactory.createConditionDescriptor();
            conditionDescriptor.setType("class");
            conditionDescriptor.getArgs().put("class.name", conditionClass.getTypeName());
            WorkflowEditorTransitionConditionUtil wetcu = new WorkflowEditorTransitionConditionUtil();
            wetcu.addCondition(actionDescriptor, "", conditionDescriptor);

        }
        _workflowService.updateWorkflow(new JiraServiceContextImpl(_user), workflowDraft);
        createBackupAndPublicWorkflow(workflowDraft.getName(), false);
        return workflowDraft;
    }
}
