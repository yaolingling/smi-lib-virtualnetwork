/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;

public class IpAddressRangeRepositoryImpl implements IpAddressRangeRepositoryCustom {

    @Autowired
    private IpAddressRangeRepository IpAddressRangeRepository;


    @Override
    public List<IpAddressRange> findOverlappingIpRanges(Set<IpAddressRange> ipAddressRangeList) {
        List<IpAddressRange> result = new LinkedList<IpAddressRange>();
        if (!CollectionUtils.isEmpty(ipAddressRangeList)) {
            for (IpAddressRange ipRange : ipAddressRangeList) {
                List<IpAddressRange> iterationIpAddressRangeList = IpAddressRangeRepository.findIpAddressRangesBetween(ipRange.getStartIpAddress(), ipRange.getEndIpAddress());
                if (!CollectionUtils.isEmpty(iterationIpAddressRangeList)) {
                    result.addAll(iterationIpAddressRangeList);
                }
            }
        }
        return result;
    }

}
