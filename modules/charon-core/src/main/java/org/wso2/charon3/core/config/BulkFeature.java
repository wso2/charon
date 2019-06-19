package org.wso2.charon3.core.config;

import java.util.Objects;

/**
 *  A complex type that specifies bulk configuration options.  See Section 3.7 of [RFC7644].  REQUIRED.
 * <br><br>
 * created at: 17.04.2019
 * @author Pascal Knüppel
 */
public class BulkFeature extends ScimFeature {

    /**
     *  An integer value specifying the maximum number of
     *  operations.  REQUIRED.
     */
    private int maxOperations = Integer.MAX_VALUE;

    /**
     * the maximum payload that is allowed to be processed.
     */
    private int maxPayLoadSize = Integer.MAX_VALUE;

    public BulkFeature() {
    }

    public BulkFeature(boolean supported, int maxOperations, int maxPayLoadSize) {
        super(supported);
        this.maxOperations = maxOperations;
        this.maxPayLoadSize = maxPayLoadSize;
    }

    public int getMaxOperations() {
        return maxOperations;
    }

    public void setMaxOperations(int maxOperations) {
        this.maxOperations = maxOperations;
    }

    public int getMaxPayLoadSize() {
        return maxPayLoadSize;
    }

    public void setMaxPayLoadSize(int maxPayLoadSize) {
        this.maxPayLoadSize = maxPayLoadSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BulkFeature)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BulkFeature that = (BulkFeature) o;
        return maxOperations == that.maxOperations && maxPayLoadSize == that.maxPayLoadSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxOperations, maxPayLoadSize);
    }

    @Override
    public String toString() {
        return "BulkFeature{" + "maxOperations=" + maxOperations + ", maxPayLoadSize=" + maxPayLoadSize + '}';
    }
}
