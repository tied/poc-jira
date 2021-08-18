package com.cmcglobal.jiraplugin.poc;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.cmcglobal.jiraplugin.poc2.POC2ServiceModuleInstallation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Scanned
@ExportAsService({ApplicationStartup.class})
@Component("componentListener")
public class ApplicationStartup implements InitializingBean, DisposableBean {
    final static Logger logger = Logger.getLogger(ApplicationStartup.class);
    private POC2ServiceModuleInstallation _poc2ServiceModuleInstallation;

    @Inject
    public ApplicationStartup(
            POC2ServiceModuleInstallation poc2ServiceModuleInstallation
    ) {
        _poc2ServiceModuleInstallation = poc2ServiceModuleInstallation;
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

    private void Init() throws Exception {
        _poc2ServiceModuleInstallation.Install();
    }
}