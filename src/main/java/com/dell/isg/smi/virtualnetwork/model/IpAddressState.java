/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum IpAddressState {
    ASSIGNED, AVAILABLE, RESERVED;

    public String value() {
        return name();
    }


    public static IpAddressState fromValue(String v) {
        return valueOf(v);
    }
}
