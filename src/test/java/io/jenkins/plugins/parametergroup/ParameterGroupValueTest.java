package io.jenkins.plugins.parametergroup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParameterGroupValueTest {

    @Test
    void buildEnvironmentDelegatesChildValues() {
        EnvVars env = new EnvVars();

        List<ParameterValue> children =
                List.of(new StringParameterValue("DEPLOY_ENV", "production"), new StringParameterValue("ZONE", "PAR1"));

        ParameterGroupValue group = new ParameterGroupValue("myGroup", "My Group", children);
        group.buildEnvironment(null, env);

        assertEquals("production", env.get("DEPLOY_ENV"));
        assertEquals("PAR1", env.get("ZONE"));
    }
}
