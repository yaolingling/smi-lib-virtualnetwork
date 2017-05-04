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

@Repository
public interface IpAddressPoolEntryRepository extends CrudRepository<IpAddressPoolEntry, Long> {

    int countByIpAddressStateAndIpAddressRangeNetworkConfigurationId(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    Page<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdAndIpAddressUsageIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId, String ipAddressUsageId, Pageable pageable);


    Page<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId, Pageable pageable);


    List<IpAddressPoolEntry> findByIpAddressStateNotAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(String ipAddressState, long ipAddressRangeNetworkConfigurationId);


    List<IpAddressPoolEntry> findByIpAddressRangeId(long ipAddressRangeId);


    List<IpAddressPoolEntry> findByIpAddressRangeNetworkConfigurationIdAndIpAddressIn(long networkConfigurationId, HashSet<Long> addressList);


    IpAddressPoolEntry findByIpAddress(long ipAddress);


    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressUsageId(String ipAddressState, String ipAddressUsageId);


    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressUsageIdAndIpAddressRangeNetworkConfigurationId(String ipAddressState, String ipAddressUsageId, long ipAddressRangeNetworkConfigurationId);


    List<IpAddressPoolEntry> findByIpAddressStateAndIpAddressRangeIdOrderByIpAddress(String ipAddressState, long ipAddressRangeid);


    List<IpAddressPoolEntry> findByIpAddressRangeNetworkConfigurationIdOrderByIpAddress(long networkConfigurationId);


    @Query("update IpAddressPoolEntry e set e.expiryDate = null, e.ipAddressState = 'AVAILABLE' where e.expiryDate < current_timestamp() and e.ipAddressState = 'RESERVED'")
    int clearAllExpiredReservations();


    @Query("UPDATE IpAddressPoolEntry SET ipAddressState = 'AVAILABLE' WHERE ipAddressUsageId = :usageId")
    void updateStateToAvailabeForUsageId(String usageId);
}
