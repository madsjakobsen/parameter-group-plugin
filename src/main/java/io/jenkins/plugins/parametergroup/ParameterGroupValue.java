package io.jenkins.plugins.parametergroup;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.Run;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class ParameterGroupValue extends ParameterValue {

    private String groupLabel = "";
    private final List<ParameterValue> childValues;

    @DataBoundConstructor
    public ParameterGroupValue(String name, List<ParameterValue> childValues) {
        super(name);
        this.childValues = childValues;
    }

    public ParameterGroupValue(String name, String groupLabel, List<ParameterValue> childValues) {
        this(name, childValues);
        this.groupLabel = groupLabel;
    }

    public String getGroupLabel() {
        return (groupLabel == null || groupLabel.isEmpty()) ? getName() : groupLabel;
    }

    @DataBoundSetter
    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
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
    public Map<String, Object> getValue() {
        Map<String, Object> values = new LinkedHashMap<>();
        for (ParameterValue child : childValues) {
            values.put(child.getName(), child.getValue());
        }
        return values;
    }
}
