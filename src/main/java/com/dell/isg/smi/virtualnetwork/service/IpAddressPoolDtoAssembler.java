/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;

/**
 * The Interface IpAddressPoolDtoAssembler.
 */
public interface IpAddressPoolDtoAssembler {

    /**
     * Transform.
     *
     * @param entity the entity
     * @return the com.dell.isg.smi.virtualnetwork.model. ip address pool entry
     */
    com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry transform(IpAddressPoolEntry entity);

    /**
     * Transform.
     *
     * @param modelObject the model object
     * @return the ip address pool entry
     */
    IpAddressPoolEntry transform(com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject);

}
