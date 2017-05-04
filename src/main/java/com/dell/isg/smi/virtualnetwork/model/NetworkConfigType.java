/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@ApiModel
@XmlType(name = "NetworkConfigType")
@XmlEnum
public enum NetworkConfigType {

    NONE, DHCP, STATIC;

    public String value() {
        return name();
    }


    public static NetworkConfigType fromValue(String v) {
        return valueOf(v);
    }

}
