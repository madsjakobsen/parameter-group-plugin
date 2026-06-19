package io.jenkins.plugins.parametergroup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.TaskListener;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ParameterGroupIntegrationTest {

    @Test
    void parameterGroupSetsEnvVarsInBuild(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();

        ParameterGroupDefinition group = new ParameterGroupDefinition("deployConfig", "Deploy Configuration");
        group.setParameters(List.of(
                new StringParameterDefinition("DEPLOY_ENV", "staging"), new StringParameterDefinition("ZONE", "AMS1")));

        project.addProperty(new ParametersDefinitionProperty(group));

        List<ParameterValue> childValues =
                List.of(new StringParameterValue("DEPLOY_ENV", "production"), new StringParameterValue("ZONE", "PAR1"));
        ParameterGroupValue groupValue = new ParameterGroupValue("deployConfig", "Deploy Configuration", childValues);

        FreeStyleBuild build =
                project.scheduleBuild2(0, new ParametersAction(groupValue)).get();

        jenkins.assertBuildStatusSuccess(build);

        var env = build.getEnvironment(TaskListener.NULL);
        assertEquals("production", env.get("DEPLOY_ENV"));
        assertEquals("PAR1", env.get("ZONE"));
    }
}
