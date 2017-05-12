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

/**
 * The Class IpAddressRangeRepositoryImpl.
 */
public class IpAddressRangeRepositoryImpl implements IpAddressRangeRepositoryCustom {

    @Autowired
    private IpAddressRangeRepository ipAddressRangeRepository;


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.repository.IpAddressRangeRepositoryCustom#findOverlappingIpRanges(java.util.Set)
     */
    @Override
    public List<IpAddressRange> findOverlappingIpRanges(Set<IpAddressRange> ipAddressRangeList) {
        List<IpAddressRange> result = new LinkedList<>();
        if (!CollectionUtils.isEmpty(ipAddressRangeList)) {
            for (IpAddressRange ipRange : ipAddressRangeList) {
                List<IpAddressRange> iterationIpAddressRangeList = ipAddressRangeRepository.findIpAddressRangesBetween(ipRange.getStartIpAddress(), ipRange.getEndIpAddress());
                if (!CollectionUtils.isEmpty(iterationIpAddressRangeList)) {
                    result.addAll(iterationIpAddressRangeList);
                }
            }
        }
        return result;
    }

}
