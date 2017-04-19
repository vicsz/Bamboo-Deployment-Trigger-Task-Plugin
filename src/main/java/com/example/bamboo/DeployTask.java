package com.example.bamboo;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.execution.DeploymentContext;
import com.atlassian.bamboo.deployments.execution.service.DeploymentExecutionService;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.deployments.versions.DeploymentVersion;
import com.atlassian.bamboo.deployments.versions.service.DeploymentVersionService;
import com.atlassian.bamboo.task.*;

import java.util.List;


public class DeployTask implements TaskType
{
    private final DeploymentVersionService deploymentVersionService;


    private final DeploymentProjectService deploymentProjectService;

    private final DeploymentExecutionService deploymentExecutionService;

    public DeployTask(DeploymentVersionService deploymentVersionService, DeploymentProjectService deploymentProjectService, DeploymentExecutionService deploymentExecutionService)
    {
        this.deploymentVersionService = deploymentVersionService;
        this.deploymentProjectService = deploymentProjectService;
        this.deploymentExecutionService = deploymentExecutionService;
    }

    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        try {

            final String environmentName = taskContext.getConfigurationMap().get("environment");

            final String deploymentProjectName = taskContext.getConfigurationMap().get("deploymentProject");

            DeploymentProject deploymentProject = getMatchingDeploymentProject(deploymentProjectName);

            DeploymentVersion deploymentVersion = deploymentVersionService.getOrCreateDeploymentVersion(deploymentProject.getId(), taskContext.getBuildContext().getParentBuildContext().getPlanResultKey());

            Environment environment = getMatchingEnvironment(deploymentProject, environmentName);

            DeploymentContext deploymentContext = deploymentExecutionService.prepareDeploymentContext(environment, deploymentVersion, taskContext.getBuildContext().getTriggerReason());

            buildLogger.addBuildLogEntry(deploymentVersion.getName() + " deployment to " + environment + " triggered.");

            return TaskResultBuilder.create(taskContext).success().build();

        }
        catch (Exception exception){

            buildLogger.addErrorLogEntry(exception.getMessage());

            return TaskResultBuilder.create(taskContext).failed().build();

        }

    }

    private DeploymentProject getMatchingDeploymentProject(String name){

        List<DeploymentProject> allDeploymentProjects = deploymentProjectService.getAllDeploymentProjects();

        for (DeploymentProject deploymentProject : allDeploymentProjects) {
            if(deploymentProject.getName().equals(name))
                return deploymentProject;
        }

        throw new RuntimeException("Unable to find deployment project: " + name);
    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, String name) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getName().equals(name))
                return environment;
        }

        throw new RuntimeException("Unable to find environment: " + name);
    }

}