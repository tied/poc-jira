package com.cmcglobal.jiraplugin.poc.service;

/* 
    @created by 12:48 PM 8/13/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class TemplateRenderService {

    static String CNOWPath = "templates/webpanel/dashboard.vm";
    static String ErrorCommonViewPath = "templates/webpanel/error/errorCommon.vm";

    public static String renderDashBoardContent(VelocityContext contextMap) throws Exception {
        return renderTemplate(CNOWPath, contextMap);
    }

    public static String renderErrorContent(int ErrorCode, String errorMessage) throws Exception {
        String errorViewPath = ErrorCommonViewPath;

        VelocityContext contextMap = new VelocityContext();
        contextMap.put("errorCode", ErrorCode);
        contextMap.put("errorMessage", errorMessage);

        switch (ErrorCode) {
            case 401:
            case 403:
            case 404:
                errorViewPath = "templates/webpanel/error/error"+ ErrorCode +".vm";
                break;
            default:
                errorViewPath = ErrorCommonViewPath;
        }

        return renderTemplate(errorViewPath, contextMap);
    }

    protected static String renderTemplate(String templatePath, VelocityContext contextMap) throws Exception {
        Writer writer = new StringWriter();
        TemplateRenderer templateRenderer = ComponentAccessor.getOSGiComponentInstanceOfType(TemplateRenderer.class);
        if (templateRenderer != null && templateRenderer.resolve(templatePath)) {
            HashMap<String, Object> templateMap = new HashMap<>();
            for (Object key : contextMap.getKeys()) {
                templateMap.put(key.toString(), contextMap.get(key.toString()));
            }
            templateRenderer.render(templatePath, templateMap, writer);
        } else {
            ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(TemplateRenderService.class.getClassLoader());
            VelocityEngine velocity = new VelocityEngine();
            velocity.setProperty(Velocity.RESOURCE_LOADER, "class");
            velocity.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocity.setProperty("runtime.log.logsystem.class", NullLogChute.class.getName());
            velocity.setProperty("input.encoding", "UTF-8");
            velocity.init();
            velocity.getTemplate(templatePath).merge(contextMap, writer);
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
        return writer.toString();
    }
}
