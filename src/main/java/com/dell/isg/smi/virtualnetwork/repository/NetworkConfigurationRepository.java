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

/**
 * The Interface NetworkConfigurationRepository.
 */
@Repository
public interface NetworkConfigurationRepository extends CrudRepository<NetworkConfiguration, Long> {

    /**
     * Find by ip address ranges ip address pool ip address.
     *
     * @param ipAddress the ip address
     * @return the network configuration
     */
    NetworkConfiguration findByIpAddressRangesIpAddressPoolIpAddress(long ipAddress);


    /**
     * Does vlan id exist.
     *
     * @param vlanId the vlan id
     * @return true, if successful
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN 'true' ELSE 'false' END FROM NetworkConfiguration n WHERE n.vlanId = :vlanId ")
    boolean doesVlanIdExist(long vlanId);


    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<NetworkConfiguration> findAll(Pageable pageable);


    /**
     * Find by type.
     *
     * @param type the type
     * @return the list
     */
    List<NetworkConfiguration> findByType(String type);


    /**
     * Find by name.
     *
     * @param name the name
     * @return the network configuration
     */
    NetworkConfiguration findByName(String name);

}
