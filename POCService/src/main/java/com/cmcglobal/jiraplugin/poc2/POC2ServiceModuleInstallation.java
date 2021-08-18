package com.cmcglobal.jiraplugin.poc2;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc2.installation.issue.payment.PaymentInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.allocate.AllocateInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.issue.shopping.ShoppingInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.project.POC2ProjectInstallation;
import com.cmcglobal.jiraplugin.poc2.installation.workflow.WorkflowInstallation;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({POC2ServiceModuleInstallation.class})
@Named
public class POC2ServiceModuleInstallation {
    private AllocateInstallation _allocateInstallation;
    private ShoppingInstallation _shoppingInstallation;
    private PaymentInstallation _paymentInstallation;
    private POC2ProjectInstallation _projectInstallation;
    private WorkflowInstallation _workflowInstallation;

    @Inject
    public POC2ServiceModuleInstallation(ShoppingInstallation shoppingInstallation,
                                         AllocateInstallation allocateInstallation,
                                         PaymentInstallation paymentInstallation,
                                         POC2ProjectInstallation projectInstallation,
                                         WorkflowInstallation workflowInstallation) throws Exception {
        _allocateInstallation = allocateInstallation;
        _shoppingInstallation = shoppingInstallation;
        _paymentInstallation = paymentInstallation;
        _projectInstallation = projectInstallation;
        _workflowInstallation = workflowInstallation;
    }

    public void Install() throws Exception {
        _projectInstallation.Install();
        _allocateInstallation.Install();
        _shoppingInstallation.Install();
        _paymentInstallation.Install();
        _workflowInstallation.Install();
    }
}
