/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.List;

import com.dell.isg.smi.commons.utilities.model.PagedResult;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;

/**
 * The Interface NetworkConfigurationManager.
 */
public interface NetworkConfigurationManager {

    /**
     * Creates the network.
     *
     * @param network the network
     * @return the long
     */
    long createNetwork(Network network);


    /**
     * Gets the network.
     *
     * @param name the name
     * @return the network
     */
    Network getNetwork(String name);


    /**
     * Gets the network.
     *
     * @param networkId the network id
     * @return the network
     */
    Network getNetwork(long networkId);


    /**
     * Update network.
     *
     * @param network the network
     * @param networkId the network id
     */
    void updateNetwork(Network network, long networkId);


    /**
     * Delete network.
     *
     * @param networkConfigurtion the network configurtion
     * @param networkId the network id
     */
    void deleteNetwork(NetworkConfiguration networkConfigurtion, long networkId);


    /**
     * Gets the all networks.
     *
     * @param offset the offset
     * @param limit the limit
     * @return the all networks
     */
    PagedResult getAllNetworks(int offset, int limit);


    /**
     * Checks if is network id valid.
     *
     * @param networkId the network id
     * @return true, if is network id valid
     */
    boolean isNetworkIdValid(long networkId);


    /**
     * Checks if is ip range valid.
     *
     * @param ipRange the ip range
     * @return true, if is ip range valid
     */
    boolean isIpRangeValid(IpRange ipRange);


    /**
     * Adds the ipv 4 range.
     *
     * @param networkId the network id
     * @param ipRange the ip range
     * @return the long
     */
    long addIpv4Range(long networkId, IpRange ipRange);


    /**
     * Checks if is ip range id valid.
     *
     * @param rangeId the range id
     * @return true, if is ip range id valid
     */
    boolean isIpRangeIdValid(long rangeId);


    /**
     * Update ipv 4 range.
     *
     * @param networkId the network id
     * @param rangeId the range id
     * @param ipRange the ip range
     */
    void updateIpv4Range(long networkId, long rangeId, IpRange ipRange);


    /**
     * Delete ipv 4 range.
     *
     * @param networkId the network id
     * @param rangeId the range id
     */
    void deleteIpv4Range(long networkId, long rangeId);


    /**
     * Gets the all networks by type.
     *
     * @param networkType the network type
     * @return the all networks by type
     */
    List<Network> getAllNetworksByType(String networkType);


    /**
     * Gets the network by name.
     *
     * @param networkName the network name
     * @return the network by name
     */
    Network getNetworkByName(String networkName);


    /**
     * Gets the network by name paged.
     *
     * @param name the name
     * @return the network by name paged
     */
    PagedResult getNetworkByNamePaged(String name);


    /**
     * Checks if is vland id greater than 4000.
     *
     * @param vlanId the vlan id
     * @return true, if is vland id greater than 4000
     */
    boolean isVlandIdGreaterThan4000(int vlanId);


    /**
     * Gets the network configuration.
     *
     * @param networkId the network id
     * @return the network configuration
     */
    NetworkConfiguration getNetworkConfiguration(long networkId);
}