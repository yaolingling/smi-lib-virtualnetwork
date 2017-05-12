/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.List;
import java.util.Set;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.commons.utilities.model.PagedResult;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.AssignIpPoolAddresses;
import com.dell.isg.smi.virtualnetwork.model.ExportIpPoolData;
import com.dell.isg.smi.virtualnetwork.model.ReserveIpPoolAddressesRequest;

/**
 * The Interface IpAddressPoolManager.
 */
public interface IpAddressPoolManager {

    /**
     * Gets the ipv 4 address pool entries.
     *
     * @param networkId the network id
     * @param state the state
     * @param usageId the usage id
     * @param offset the offset
     * @param limit the limit
     * @return the ipv 4 address pool entries
     */
    abstract PagedResult getIpv4AddressPoolEntries(long networkId, String state, String usageId, int offset, int limit);


    /**
     * Reserve ipv 4 address pool addresses.
     *
     * @param networkId the network id
     * @param reserveIpPoolAddressesRequest the reserve ip pool addresses request
     * @param reservationCalendarUnit the reservation calendar unit
     * @param reservationNumberOfUnits the reservation number of units
     * @return the sets the
     * @throws BusinessValidationException the business validation exception
     */
    abstract Set<String> reserveIpv4AddressPoolAddresses(long networkId, ReserveIpPoolAddressesRequest reserveIpPoolAddressesRequest, int reservationCalendarUnit, int reservationNumberOfUnits);


    /**
     * Assign ipv 4 address pool addresses.
     *
     * @param networkId the network id
     * @param assignIpPoolAddresses the assign ip pool addresses
     * @throws BusinessValidationException the business validation exception
     */
    abstract void assignIpv4AddressPoolAddresses(long networkId, AssignIpPoolAddresses assignIpPoolAddresses);


    /**
     * Release specific ipv 4 address.
     *
     * @param networkId the network id
     * @param ipAddress the ip address
     * @throws BusinessValidationException the business validation exception
     */
    abstract void releaseSpecificIpv4Address(long networkId, String ipAddress);


    /**
     * Release all ipv 4 addresses.
     *
     * @param networkId the network id
     * @param usageId the usage id
     * @throws BusinessValidationException the business validation exception
     */
    abstract void releaseAllIpv4Addresses(long networkId, String usageId);


    /**
     * Checks if is ip address state valid.
     *
     * @param ipAddressState the ip address state
     * @return true, if is ip address state valid
     */
    abstract boolean isIpAddressStateValid(String ipAddressState);


    /**
     * Does network record exist.
     *
     * @param networkId the network id
     * @return true, if successful
     */
    abstract boolean doesNetworkRecordExist(long networkId);


    /**
     * Checks if is usage id valid.
     *
     * @param usageId the usage id
     * @return true, if is usage id valid
     */
    abstract boolean isUsageIdValid(String usageId);


    /**
     * Checks if is ip address valid.
     *
     * @param ipAddress the ip address
     * @return true, if is ip address valid
     */
    abstract boolean isIpAddressValid(String ipAddress);


    /**
     * Checks if is reserve quantity requested is valid.
     *
     * @param reserveIpPoolAddressesRequest the reserve ip pool addresses request
     * @return true, if is reserve quantity requested is valid
     */
    abstract boolean isReserveQuantityRequestedIsValid(ReserveIpPoolAddressesRequest reserveIpPoolAddressesRequest);


    /**
     * Export ip pools in use.
     *
     * @param networkId the network id
     * @return the list
     */
    abstract List<ExportIpPoolData> exportIpPoolsInUse(long networkId);


    /**
     * This call will release the the IPAddress based on provided usageGUID.
     *
     * @param usageId the usage id
     */
    abstract void releaseIPAddressesByUsageId(String usageId);


    /**
     * This call will return the IPAddress assigned to the provided usageGUID, from provided networkConfigurationID.
     *
     * @param usageId the usage id
     * @param networkConfigurationId -GUID of network configuration from which to reserve available IP addresses
     * @return List of IPAddresses assigned to the provided usageId
     */
    abstract Set<String> getIPAddressesAssignedToGUID(String usageId, long networkConfigurationId);


    /**
     * This call will return the IPAddress reserved to the provided usageGUID, from provided networkConfigurationID.
     *
     * @param usageID the usage ID
     * @param networkConfigurationId -GUID of network configuration from which to reserve available IP addresses
     * @return List of IPAddresses assigned to the provided GUID
     */
    abstract Set<String> getIPAddressesReservedToGUID(String usageID, long networkConfigurationId);


    /**
     * This call will list of IPAddressPoolEntry based on provided IPRange, state.
     *
     * @param ipRangeId the ip range id
     * @param state the state
     * @return List of IPAddressPoolEntry, based on provided IPRange, state.
     */
    abstract List<IpAddressPoolEntry> findByStateAndRangeId(long ipRangeId, String state);


    /**
     * This call will list of IPAddressPoolEntry assigned for provided networkConfigurationId.
     *
     * @param networkConfigurationId the network configuration id
     * @return List of IPAddressPoolEntry, assigned for the provided networkConfigurationId
     */
    abstract List<IpAddressPoolEntry> findByNetworkConfigurationId(long networkConfigurationId);


    /**
     * This call will return NetworkConfiguration for the provided IP Address.
     *
     * @param ipAddress the ip address
     * @return NetworkConfiguration, assigned for the provided ipAddress
     */
    abstract NetworkConfiguration findAssignedNetworkConfigurationForIpAddress(String ipAddress);


    /**
     * Convert Reserved entries to Assigned (set expiration date to null).
     *
     * @param usageId the usage id
     * @param ipAddress the ip address
     */
    abstract void convertReservedToAssigned(String usageId, String ipAddress);

}
