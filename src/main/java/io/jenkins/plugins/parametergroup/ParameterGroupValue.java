package io.jenkins.plugins.parametergroup;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.Run;
import java.util.List;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class ParameterGroupValue extends ParameterValue {

    private final String groupLabel;
    private final List<ParameterValue> childValues;

    public ParameterGroupValue(String name, String groupLabel, List<ParameterValue> childValues) {
        super(name);
        this.groupLabel = groupLabel;
        this.childValues = childValues;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public List<ParameterValue> getChildValues() {
        return childValues;
    }

    @Override
    public void buildEnvironment(Run<?, ?> run, EnvVars env) {
        for (ParameterValue child : childValues) {
            child.buildEnvironment(run, env);
        }
    }

    @Override
    public Object getValue() {
        return childValues;
    }
}
