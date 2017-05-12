/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;

/**
 * The Interface NetworkConfigurationDtoAssembler.
 */
public interface NetworkConfigurationDtoAssembler {

    /**
     * Transform.
     *
     * @param networkConfiguration the network configuration
     * @return the network
     */
    Network transform(NetworkConfiguration networkConfiguration);

    /**
     * Transform.
     *
     * @param network the network
     * @return the network configuration
     */
    NetworkConfiguration transform(Network network);

    /**
     * Transform.
     *
     * @param ipRange the ip range
     * @param networkConfiguration the network configuration
     * @return the ip address range
     */
    IpAddressRange transform(IpRange ipRange, NetworkConfiguration networkConfiguration);
}
