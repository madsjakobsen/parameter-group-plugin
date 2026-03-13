package io.jenkins.plugins.parametergroup;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

public class ParameterGroup extends ParameterDefinition {

    private final String groupLabel;
    private List<ParameterDefinition> parameters;

    @DataBoundConstructor
    public ParameterGroup(String name, String groupLabel) {
        super(name);
        this.groupLabel = groupLabel != null ? groupLabel : name;
        this.parameters = new ArrayList<>();
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @DataBoundSetter
    public void setParameters(List<ParameterDefinition> parameters) {
        this.parameters = parameters != null ? new ArrayList<>(parameters) : new ArrayList<>();
    }

    @Override
    public ParameterValue createValue(StaplerRequest2 req, JSONObject jo) {
        List<JSONObject> childDataList = new ArrayList<>();
        Object paramData = jo.opt("parameter");
        if (paramData instanceof JSONArray) {
            for (Object o : (JSONArray) paramData) {
                if (o instanceof JSONObject) {
                    childDataList.add((JSONObject) o);
                }
            }
        } else if (paramData instanceof JSONObject) {
            childDataList.add((JSONObject) paramData);
        }

        Map<String, ParameterDefinition> paramsByName =
                parameters.stream().collect(Collectors.toMap(ParameterDefinition::getName, p -> p));

        List<ParameterValue> childValues = new ArrayList<>();
        for (JSONObject childData : childDataList) {
            String childName = childData.optString("name");
            ParameterDefinition param = paramsByName.get(childName);
            if (param != null) {
                ParameterValue value = param.createValue(req, childData);
                if (value != null) {
                    childValues.add(value);
                }
            }
        }

        return new ParameterGroupValue(getName(), groupLabel, childValues);
    }

    @Override
    public ParameterValue createValue(StaplerRequest2 req) {
        return new ParameterGroupValue(getName(), groupLabel, new ArrayList<>());
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Parameter Group";
        }

        public List<ParameterDescriptor> getParameterDescriptors() {
            return ParameterDefinition.all().stream().toList();
        }
    }
}
