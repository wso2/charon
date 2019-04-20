package org.wso2.charon3.core.config;

import java.util.Objects;

/**
 * A complex type that specifies FILTER options.  REQUIRED.  See Section 3.4.2.2 of [RFC7644].
 * <br><br>
 * created at: 17.04.2019
 *
 * @author Pascal Knüppel
 */
public class FilterFeature extends ScimFeature {

    /**
     * the maximum number of results to be returned<br>
     * <br>
     * <b>NOTE:</b><br>
     * this value is set intentionally to 1 pe default. This ensure that the developer is aware that this value should
     * be configured in order to prevent unexpected behaviour
     */
    private int maxResults = 1;

    public FilterFeature () {
    }

    public FilterFeature (boolean supported, int maxResults) {
        super(supported);
        this.maxResults = maxResults;
    }

    public int getMaxResults () {
        return maxResults;
    }

    public void setMaxResults (int maxResults) {
        this.maxResults = maxResults;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (!( o instanceof FilterFeature )) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        FilterFeature that = (FilterFeature) o;
        return maxResults == that.maxResults && super.equals(that);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), maxResults);
    }


    @Override
    public String toString () {
        return "{\n" + super.toString() + "\nFilterFeature{" + "maxResults=" + maxResults + "}\n}";
    }
}
