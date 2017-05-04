/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;

@Repository
public interface IpAddressRangeRepository extends CrudRepository<IpAddressRange, Long>, IpAddressRangeRepositoryCustom {
    @Modifying
    @Transactional
    @Query("delete from IpAddressRange r where not exists (select e from IpAddressPoolEntry e where e.ipAddressRange = r)")
    void deleteWhereNoPoolEntriesExist();


    @Query("select i from IpAddressRange i where i.startIpAddress between :startAddress and :endAddress and i.endIpAddress between :startAddress and :endAddress")
    List<IpAddressRange> findIpAddressRangesBetween(@Param("startAddress") long startAddress, @Param("endAddress") long endAddress);


    List<IpAddressRange> findByTemporaryIsTrue();
}
