package com.example.bamboo;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import org.apache.commons.lang.StringUtils;

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

    public void validate(final ActionParametersMap params, final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String environment = params.getString("environment");
        final String deploymentProject = params.getString("deploymentProject");

        if (StringUtils.isEmpty(environment))
            errorCollection.addError("environment", "Environment can not be empty.");

        if (StringUtils.isEmpty(deploymentProject))
            errorCollection.addError("deploymentProject", "Deployment Project cannot be empty.");
    }
}
