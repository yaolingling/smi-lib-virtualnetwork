/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;

/**
 * @author lakshmi.lakkireddy
 *
 */
public interface IpAddressPoolDtoAssembler {

    com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry transform(IpAddressPoolEntry entity);


    IpAddressPoolEntry transform(com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject);

}
