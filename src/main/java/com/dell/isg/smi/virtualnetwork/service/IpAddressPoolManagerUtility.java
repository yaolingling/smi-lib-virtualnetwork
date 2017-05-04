/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;
import com.dell.isg.smi.virtualnetwork.validation.ValidatedInet4Range;

public final class IpAddressPoolManagerUtility {

    private IpAddressPoolManagerUtility() {
    }


    public static Set<IpAddressPoolEntry> expandIpAddresses(IpAddressRange ipRange) {
        ValidatedInet4Range validatedRange = new ValidatedInet4Range(Inet4ConverterValidator.convertIpValueToString(ipRange.getStartIpAddress()), Inet4ConverterValidator.convertIpValueToString(ipRange.getEndIpAddress()));
        Set<IpAddressPoolEntry> addressPools = new HashSet<IpAddressPoolEntry>();
        List<String> addressStrings = validatedRange.getAddressStrings();
        for (String address : addressStrings) {
            IpAddressPoolEntry entry = new IpAddressPoolEntry();
            entry.setName(address);
            entry.setIpAddress(Inet4ConverterValidator.convertIpStringToLong(address));
            entry.setIPAddressRange(ipRange);
            entry.setIpAddressState(IpAddressState.AVAILABLE.toString());
            addressPools.add(entry);
        }
        return addressPools;
    }

}
