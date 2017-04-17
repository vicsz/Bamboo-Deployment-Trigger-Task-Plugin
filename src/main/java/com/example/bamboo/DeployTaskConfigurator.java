package com.example;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;

import java.util.Map;

public class DeployTaskConfigurator extends AbstractTaskConfigurator {

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put("environment", params.getString("environment"));
        config.put("deploymentProject", params.getString("deploymentProject"));

        return config;
    }

    public void populateContextForEdit(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("environment", taskDefinition.getConfiguration().get("environment"));
        context.put("deploymentProject", taskDefinition.getConfiguration().get("deploymentProject"));
    }
}
