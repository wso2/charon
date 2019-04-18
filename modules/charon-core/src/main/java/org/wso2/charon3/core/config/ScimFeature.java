package org.wso2.charon3.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * this class represents a complex type for the scim service provider configuration. Such types are "patch", "bulk",
 * "filter" etc.
 * <br><br>
 * created at: 17.04.2019
 * @author Pascal Knüppel
 */
public class ScimFeature {

    /**
     * if this feature is supported or not
     */
    private boolean supported;

    public ScimFeature() {
    }

    public ScimFeature(boolean supported) {
        this.supported = supported;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScimFeature)) {
            return false;
        }
        ScimFeature that = (ScimFeature) o;
        return supported == that.supported;
    }

    @Override
    public int hashCode() {
        return Objects.hash(supported);
    }

    @Override
    public String toString() {
        return "ScimFeature{" + "supported=" + supported + '}';
    }
}
