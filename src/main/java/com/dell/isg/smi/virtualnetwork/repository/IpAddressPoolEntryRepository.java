/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.repository;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;

/**
 * The Interface IpAddressPoolEntryRepository.
 */
@Repository
public interface IpAddressPoolEntryRepository extends CrudRepository<IpAddressPoolEntry, Long> {

    /**
     * Count by ip address state and ip address range network configuration id.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @return the int
     */
    int countByIpAddressStateAndIpAddressRangeNetworkConfigurationId(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    /**
     * Find by ip address state and ip address range network configuration id order by ip address asc.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    /**
     * Find by ip address state and ip address range network configuration id and ip address usage id order by ip address asc.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @param ipAddressUsageId the ip address usage id
     * @param pageable the pageable
     * @return the page
     */
    Page<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdAndIpAddressUsageIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId, String ipAddressUsageId, Pageable pageable);


    /**
     * Find by ip address state and ip address range network configuration id order by ip address asc.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @param pageable the pageable
     * @return the page
     */
    Page<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId, Pageable pageable);


    /**
     * Find by ip address state not and ip address range network configuration id order by ip address asc.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressStateNotAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    /**
     * Find by ip address range id.
     *
     * @param ipAddressRangeId the ip address range id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressRangeId(long ipAddressRangeId);


    /**
     * Find by ip address range network configuration id and ip address in.
     *
     * @param networkConfigurationId the network configuration id
     * @param addressList the address list
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressRangeNetworkConfigurationIdAndIpAddressIn(long networkConfigurationId, HashSet<Long> addressList);


    /**
     * Find by ip address.
     *
     * @param ipAddress the ip address
     * @return the ip address pool entry
     */
    IpAddressPoolEntry findByIpAddress(long ipAddress);


    /**
     * Find by ip address state and ip address usage id.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressUsageId the ip address usage id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressUsageId(String ipAddressState, String ipAddressUsageId);


    /**
     * Find by ip address state and ip address usage id and ip address range network configuration id.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressUsageId the ip address usage id
     * @param ipAddressRangeNetworkConfigurationId the ip address range network configuration id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressUsageIdAndIpAddressRangeNetworkConfigurationId(String ipAddressState, String ipAddressUsageId, long ipAddressRangeNetworkConfigurationId);


    /**
     * Find by ip address state and ip address range id order by ip address.
     *
     * @param ipAddressState the ip address state
     * @param ipAddressRangeid the ip address rangeid
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeIdOrderByIpAddress(String ipAddressState, long ipAddressRangeid);


    /**
     * Find by ip address range network configuration id order by ip address.
     *
     * @param networkConfigurationId the network configuration id
     * @return the list
     */
    List<IpAddressPoolEntry> findByIpAddressRangeNetworkConfigurationIdOrderByIpAddress(long networkConfigurationId);


    /**
     * Clear all expired reservations.
     *
     * @return the int
     */
    @Query("update IpAddressPoolEntry e set e.expiryDate = null, e.ipAddressState = 'AVAILABLE' where e.expiryDate < current_timestamp() and e.ipAddressState = 'RESERVED'")
    int clearAllExpiredReservations();


    /**
     * Update state to availabe for usage id.
     *
     * @param usageId the usage id
     */
    @Query("UPDATE IpAddressPoolEntry SET ipAddressState = 'AVAILABLE' WHERE ipAddressUsageId = :usageId")
    void updateStateToAvailabeForUsageId(String usageId);
}
