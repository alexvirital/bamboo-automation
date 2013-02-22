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

public class VeracodeResultsConfigurator extends AbstractTaskConfigurator
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
        config.put("jira_username", params.getString("jira_username"));
        config.put("jira_password", params.getString("jira_password"));
        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        context.put("api_username", "${bamboo.capability.veracode.username}");
        context.put("api_password", "${bamboo.capability.veracode.password}");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        context.put("api_username", taskDefinition.getConfiguration().get("api_username"));
        context.put("api_password", taskDefinition.getConfiguration().get("api_password"));
        context.put("app_id", taskDefinition.getConfiguration().get("app_id"));
        context.put("jira_username", taskDefinition.getConfiguration().get("jira_username"));
        context.put("jira_password", taskDefinition.getConfiguration().get("jira_password"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("api_username", taskDefinition.getConfiguration().get("api_username"));
        context.put("api_password", taskDefinition.getConfiguration().get("api_password"));
        context.put("app_id", taskDefinition.getConfiguration().get("app_id"));
        context.put("jira_username", taskDefinition.getConfiguration().get("jira_username"));
        context.put("jira_password", taskDefinition.getConfiguration().get("jira_password"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
        final String apiUserValue = params.getString("api_username");
        final String apiPasswordValue = params.getString("api_password");
        final String appIdValue = params.getString("app_id");

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
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
