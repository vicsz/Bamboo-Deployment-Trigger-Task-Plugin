package com.example.bamboo;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class DeployTaskConfigurator extends AbstractTaskConfigurator {

    private final DeploymentProjectService deploymentProjectService;

    public DeployTaskConfigurator(DeploymentProjectService deploymentProjectService)
    {
        this.deploymentProjectService = deploymentProjectService;
    }

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put("deploymentProject", params.getString("deploymentProject"));
        config.put("environment", params.getString("environment"));

        return config;
    }

    public void populateContextForEdit(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("deploymentProject", taskDefinition.getConfiguration().get("deploymentProject"));
        context.put("environment", taskDefinition.getConfiguration().get("environment"));
    }

    public void validate(final ActionParametersMap params, final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String deploymentProjectName = params.getString("deploymentProject");
        final String environmentName = params.getString("environment");

        if (StringUtils.isEmpty(deploymentProjectName))
            errorCollection.addError("deploymentProject", "Deployment Project cannot be empty.");

        if (StringUtils.isEmpty(environmentName))
            errorCollection.addError("environment", "Environment can not be empty.");

        if (StringUtils.isEmpty(environmentName) || StringUtils.isEmpty(deploymentProjectName))
            return;

        DeploymentProject deploymentProject = getMatchingDeploymentProject(deploymentProjectName);

        if (deploymentProject == null){
            errorCollection.addError("deploymentProject", "Unable to find Deployment Project named: " + deploymentProjectName);
            return;
        }

        if(getMatchingEnvironment(deploymentProject, environmentName) == null)
            errorCollection.addError("environment", "Unable to find Environment named: " + environmentName + " for deployment project : " + deploymentProjectName);

    }

    private DeploymentProject getMatchingDeploymentProject(String name){

        List<DeploymentProject> allDeploymentProjects = deploymentProjectService.getAllDeploymentProjects();

        for (DeploymentProject deploymentProject : allDeploymentProjects) {
            if(deploymentProject.getName().equals(name))
                return deploymentProject;
        }

        return null;
    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, String name) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getName().equals(name))
                return environment;
        }

        return null;
    }
}
