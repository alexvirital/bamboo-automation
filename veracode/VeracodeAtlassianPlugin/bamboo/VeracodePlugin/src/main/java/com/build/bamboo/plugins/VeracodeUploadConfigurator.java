package com.build.bamboo.plugins;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;

public class VeracodeUploadConfigurator extends AbstractTaskConfigurator
{
    private TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("api_username", params.getString("api_username"));
        config.put("api_password", params.getString("api_password"));
        config.put("app_id", params.getString("app_id"));
        config.put("app_platform", params.getString("app_platform"));
        config.put("build_id", params.getString("build_id"));
        config.put("source_file", params.getString("source_file"));
        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        context.put("api_username", "${bamboo.capability.veracode.username}");
        context.put("api_password", "${bamboo.capability.veracode.password}");
        context.put("build_id", "${bamboo.repository.revision.number}");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        context.put("api_username", taskDefinition.getConfiguration().get("api_username"));
        context.put("api_password", taskDefinition.getConfiguration().get("api_password"));
        context.put("app_id", taskDefinition.getConfiguration().get("app_id"));
        context.put("app_platform", taskDefinition.getConfiguration().get("app_platform"));
        context.put("build_id", taskDefinition.getConfiguration().get("build_id"));
        context.put("source_file", taskDefinition.getConfiguration().get("source_file"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("api_username", taskDefinition.getConfiguration().get("api_username"));
        context.put("api_password", taskDefinition.getConfiguration().get("api_password"));
        context.put("app_id", taskDefinition.getConfiguration().get("app_id"));
        context.put("app_platform", taskDefinition.getConfiguration().get("app_platform"));
        context.put("build_id", taskDefinition.getConfiguration().get("build_id"));
        context.put("source_file", taskDefinition.getConfiguration().get("source_file"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
        final String apiUserValue = params.getString("api_username");
        final String apiPasswordValue = params.getString("api_password");
        final String appIdValue = params.getString("app_id");
        final String appPlatformValue = params.getString("app_platform");
        final String buildIdValue = params.getString("build_id");
        final String sourceFileValue = params.getString("source_file");

        if (StringUtils.isEmpty(apiUserValue))
        {
        	errorCollection.addError("api_username", textProvider.getText("myfirstplugin.api_username.error"));	
        }
        if (StringUtils.isEmpty(apiPasswordValue))
        {
	        errorCollection.addError("api_password", textProvider.getText("myfirstplugin.api_password.error"));
        }
        if (StringUtils.isEmpty(appIdValue))
        {
        	errorCollection.addError("app_id", textProvider.getText("myfirstplugin.app_id.error"));
        }
        if (StringUtils.isEmpty(appPlatformValue))
        {
        	errorCollection.addError("app_platform", textProvider.getText("myfirstplugin.app_platform.error"));
        }
        if (StringUtils.isEmpty(buildIdValue))
        {
        	errorCollection.addError("build_id", textProvider.getText("myfirstplugin.build_id.error"));
        }
        if (StringUtils.isEmpty(sourceFileValue))
        {
        	errorCollection.addError("source_file", textProvider.getText("myfirstplugin.source_file.error"));
        }
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
