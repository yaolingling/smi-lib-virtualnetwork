/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;

@Repository
public interface NetworkConfigurationRepository extends CrudRepository<NetworkConfiguration, Long> {

    NetworkConfiguration findByIpAddressRangesIpAddressPoolIpAddress(long ipAddress);


    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN 'true' ELSE 'false' END FROM NetworkConfiguration n WHERE n.vlanId = :vlanId ")
    boolean doesVlanIdExist(long vlanId);


    Page<NetworkConfiguration> findAll(Pageable pageable);


    List<NetworkConfiguration> findByType(String type);


    NetworkConfiguration findByName(String name);

}
