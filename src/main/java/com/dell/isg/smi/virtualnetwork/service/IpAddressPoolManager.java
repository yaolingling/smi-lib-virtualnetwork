/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.List;
import java.util.Set;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.commons.elm.model.PagedResult;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.AssignIpPoolAddresses;
import com.dell.isg.smi.virtualnetwork.model.ExportIpPoolData;
import com.dell.isg.smi.virtualnetwork.model.ReserveIpPoolAddressesRequest;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
public interface IpAddressPoolManager {

    abstract PagedResult getIpv4AddressPoolEntries(long networkId, String state, String usageId, int offset, int limit);


    abstract Set<String> reserveIpv4AddressPoolAddresses(long networkId, ReserveIpPoolAddressesRequest ReserveIpPoolAddressesRequest, int reservationCalendarUnit, int reservationNumberOfUnits) throws BusinessValidationException;


    abstract void assignIpv4AddressPoolAddresses(long networkId, AssignIpPoolAddresses assignIpPoolAddresses) throws BusinessValidationException;


    abstract void releaseSpecificIpv4Address(long networkId, String ipAddress) throws BusinessValidationException;


    abstract void releaseAllIpv4Addresses(long networkId, String usageId) throws BusinessValidationException;


    abstract boolean isIpAddressStateValid(String ipAddressState);


    abstract boolean doesNetworkRecordExist(long networkId);


    abstract boolean isUsageIdValid(String usageId);


    abstract boolean isIpAddressValid(String ipAddress);


    abstract boolean isReserveQuantityRequestedIsValid(ReserveIpPoolAddressesRequest reserveIpPoolAddressesRequest);


    abstract List<ExportIpPoolData> exportIpPoolsInUse(long networkId);


    /**
     * This call will release the the IPAddress based on provided usageGUID
     *
     * @param usageId
     */
    abstract void releaseIPAddressesByUsageId(String usageId);


    /**
     *
     * This call will return the IPAddress assigned to the provided usageGUID, from provided networkConfigurationID
     *
     * @param usageGUID -GUID of the object to which the addresses are to be assigned.
     * @param networkConfigurationId -GUID of network configuration from which to reserve available IP addresses
     * @return List of IPAddresses assigned to the provided usageId
     */
    abstract Set<String> getIPAddressesAssignedToGUID(String usageId, long networkConfigurationId);


    /**
     *
     * This call will return the IPAddress reserved to the provided usageGUID, from provided networkConfigurationID
     *
     * @param usageGUID -GUID of the object to which the addresses are to be assigned.
     * @param networkConfigurationId -GUID of network configuration from which to reserve available IP addresses
     * @return List of IPAddresses assigned to the provided GUID
     */
    abstract Set<String> getIPAddressesReservedToGUID(String usageID, long networkConfigurationId);


    /**
     * This call will list of IPAddressPoolEntry based on provided IPRange, state.
     *
     * @param ipRangeId
     * @param state
     * @return List of IPAddressPoolEntry, based on provided IPRange, state.
     */
    abstract List<IpAddressPoolEntry> findByStateAndRangeId(long ipRangeId, String state);


    /**
     * This call will list of IPAddressPoolEntry assigned for provided networkConfigurationId.
     *
     * @param networkConfigurationId
     * @return List of IPAddressPoolEntry, assigned for the provided networkConfigurationId
     */
    abstract List<IpAddressPoolEntry> findByNetworkConfigurationId(long networkConfigurationId);


    /**
     * This call will return NetworkConfiguration for the provided IP Address.
     *
     * @param ipAddress
     * @return NetworkConfiguration, assigned for the provided ipAddress
     */
    abstract NetworkConfiguration findAssignedNetworkConfigurationForIpAddress(String ipAddress);


    /**
     * Convert Reserved entries to Assigned (set expiration date to null)
     *
     * @param usageId
     */
    abstract void convertReservedToAssigned(String usageId, String ipAddress);

}
