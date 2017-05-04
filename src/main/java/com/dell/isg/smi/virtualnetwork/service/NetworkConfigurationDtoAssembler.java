/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
public interface NetworkConfigurationDtoAssembler {

    Network transform(NetworkConfiguration networkConfiguration);


    NetworkConfiguration transform(Network network);


    IpAddressRange transform(IpRange ipRange, NetworkConfiguration networkConfiguration);

}
