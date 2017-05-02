package com.example.bamboo;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.execution.DeploymentContext;
import com.atlassian.bamboo.deployments.execution.service.DeploymentExecutionService;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.deployments.versions.DeploymentVersion;
import com.atlassian.bamboo.deployments.versions.service.DeploymentVersionService;
import com.atlassian.bamboo.spring.ComponentAccessor;
import com.atlassian.bamboo.task.*;
import org.jetbrains.annotations.NotNull;


public class DeployTask implements TaskType
{
    private DeploymentVersionService deploymentVersionService;

    private DeploymentProjectService deploymentProjectService;

    private DeploymentExecutionService deploymentExecutionService;

    private BuildLogger buildLogger;

    public DeployTask()
    {
        this.deploymentProjectService = ComponentAccessor.DEPLOYMENT_PROJECT_SERVICE.get();
        this.deploymentExecutionService = ComponentAccessor.DEPLOYMENT_EXECUTION_SERVICE.get();
    }

    public void setDeploymentVersionService(DeploymentVersionService deploymentVersionService) {
        this.deploymentVersionService = deploymentVersionService;
    }

    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {

        buildLogger = taskContext.getBuildLogger();

        try {
            final long deploymentId = taskContext.getConfigurationMap().getAsLong("deploymentProjectId");

            final long environmentId = taskContext.getConfigurationMap().getAsLong("environmentId");

            DeploymentProject deploymentProject = deploymentProjectService.getDeploymentProject(deploymentId);

            DeploymentVersion deploymentVersion = deploymentVersionService.getOrCreateDeploymentVersion(deploymentId, taskContext.getBuildContext().getParentBuildContext().getPlanResultKey());

            Environment environment = getMatchingEnvironment(deploymentProject, environmentId);

            DeploymentContext deploymentContext = deploymentExecutionService.prepareDeploymentContext(environment, deploymentVersion, taskContext.getBuildContext().getTriggerReason());

            deploymentExecutionService.execute(deploymentContext);

            buildLogger.addBuildLogEntry(deploymentVersion.getName() + " deployment to " + environment + " triggered.");

            waitForDeploymentToComplete(environment);

            return TaskResultBuilder.create(taskContext).success().build();

        }
        catch (Exception exception){

            buildLogger.addErrorLogEntry(exception.getMessage());

            return TaskResultBuilder.create(taskContext).failed().build();

        }

    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, long id) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getId() == id)
                return environment;
        }

        throw new RuntimeException("Unable to find environment with id: " + id);
    }

    private void waitForDeploymentToComplete(Environment environment) {

        do {
            try {
                buildLogger.addBuildLogEntry("Deployment already in progress - delaying 5 seconds");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                buildLogger.addErrorLogEntry("Waiting for deployment error: " + e.getMessage(), e);
            }
        } while(deploymentExecutionService.isEnvironmentBeingDeployedTo(environment.getId()));
    }

}