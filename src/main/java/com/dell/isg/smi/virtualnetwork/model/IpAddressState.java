/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

/**
 * The Enum IpAddressState.
 */
@ApiModel
public enum IpAddressState {
    ASSIGNED, AVAILABLE, RESERVED;

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return name();
    }


    /**
     * From value.
     *
     * @param v the v
     * @return the ip address state
     */
    public static IpAddressState fromValue(String v) {
        return valueOf(v);
    }
}
