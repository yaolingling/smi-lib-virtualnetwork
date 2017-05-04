/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.configuration;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
public class NetworkTableConstants {

    public static final String BASE_ENTITY_ID = "id";

    // Network Configuration
    public static final String T_NETWORK_CONFIGURATION = "network_conf";
    public static final String NETWORK_CONFIGURATION_ID = "network_conf_id";
    public static final String NC_VLAN_ID = "vlan_id";
    public static final String NC_NAME = "name";
    public static final String NC_DESCRIPTION = "description";
    public static final String NC_IS_STATIC = "is_static";
    public static final String NC_TYPE = "network_type";
    public static final String NC_GATE_WAY = "gateway";
    public static final String NC_SUBNET = "subnet";
    public static final String NC_PRIMARY_DNS = "primary_dns";
    public static final String NC_SECONDARY_DNS = "secondary_dns";
    public static final String NC_DNS_SUFFIX = "dns_suffix";
    public static final String NETWORK_CONFIGURATION = "networkConfiguration";

    // IP Address Range
    public static final String T_IP_ADDRESS_RANGE = "ipaddress_range";
    public static final String IP_ADDRESS_RANGE_ID = "ipaddress_range_id";
    public static final String IP_ADDRESS_RANGE_START_IP = "start_ip";
    public static final String IP_ADDRESS_RANGE_END_IP = "end_ip";
    public static final String IP_ADDRESS_RANGE_NAME = "name";
    public static final String IP_ADDRESS_RANGE_DESCRIPTION = "description";
    public static final String IP_ADDRESS_RANGE_TEMPORARY = "temporary";
    public static final String IP_ADDRESS_RANGE = "ipAddressRange";

    // IP Address Pool Entry
    public static final String T_IP_ADDRESS_POOL_ENTRY = "ipaddress_pool_entry";
    public static final String IP_ADDRESS_POOL_ENTRY_ID = "ipaddress_pool_entry_id";
    public static final String IP_ADDRESS_POOL_ENTRY_ADDRESS = "ipaddress";
    public static final String IP_ADDRESS_POOL_ENTRY_NAME = "name";
    public static final String IP_ADDRESS_POOL_ENTRY_STATE = "ipaddress_state";
    public static final String IP_ADDRESS_POOL_ENTRY_EXPIRY_DATE = "expiry_date";
    public static final String IP_ADDRESS_POOL_ENTRY_USAGE_ID = "ipaddress_usage_guid";

}
