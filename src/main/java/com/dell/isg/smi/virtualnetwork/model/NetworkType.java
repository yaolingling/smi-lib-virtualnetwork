/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@ApiModel
@XmlType(name = "NetworkType")
@XmlEnum
public enum NetworkType {

    PUBLIC_LAN, PRIVATE_LAN, STORAGE_ISCSI_SAN, STORAGE_FCOE_SAN, OOB_OR_INFRASTRUCTURE_MANAGEMENT, HYPERVISOR_MANAGEMENT, HYPERVISOR_MIGRATION, HYPERVISOR_CLUSTER_PRIVATE, PXE, FILESHARE, FIP_SNOOPING, HARDWARE_MANAGEMENT;

    public String value() {
        return name();
    }


    public static NetworkType fromValue(String v) {
        return valueOf(v);
    }

}
