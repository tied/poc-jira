package com.cmcglobal.jiraplugin.poc.service;

import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;

import javax.inject.Named;

@ExportAsService({CustomFieldService.class})
@Named("Phrase2CustomFieldService")
@JiraComponent
public class CustomFieldService {
}
