/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.List;

import com.dell.isg.smi.commons.elm.model.PagedResult;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;

public interface NetworkConfigurationManager {

    long createNetwork(Network network);


    Network getNetwork(String name);


    Network getNetwork(long networkId);


    void updateNetwork(Network network, long networkId);


    void deleteNetwork(NetworkConfiguration networkConfigurtion, long networkId);


    PagedResult getAllNetworks(int offset, int limit);


    boolean isNetworkIdValid(long networkId);


    boolean isIpRangeValid(IpRange ipRange);


    long addIpv4Range(long networkId, IpRange ipRange);


    boolean isIpRangeIdValid(long rangeId);


    void updateIpv4Range(long networkId, long rangeId, IpRange ipRange);


    void deleteIpv4Range(long networkId, long rangeId);


    List<Network> getAllNetworksByType(String networkType);


    Network getNetworkByName(String networkName);


    PagedResult getNetworkByNamePaged(String name);


    boolean isVlandIdGreaterThan4000(int vlanId);


    NetworkConfiguration getNetworkConfiguration(long networkId);
}