/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The Enum NetworkConfigType.
 */
@ApiModel
@XmlType(name = "NetworkConfigType")
@XmlEnum
public enum NetworkConfigType {

    NONE, DHCP, STATIC;

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
     * @return the network config type
     */
    public static NetworkConfigType fromValue(String v) {
        return valueOf(v);
    }

}
