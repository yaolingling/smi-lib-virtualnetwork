/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import org.springframework.stereotype.Component;

import com.dell.isg.smi.commons.elm.utilities.DateTimeUtils;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * The Class IpAddressPoolDtoAssemblerImpl.
 */
@Component
public class IpAddressPoolDtoAssemblerImpl implements IpAddressPoolDtoAssembler {

    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.IpAddressPoolDtoAssembler#transform(com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry)
     */
    @Override
    public com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry transform(IpAddressPoolEntry entity) {
        if (entity == null) {
            return null;
        }
        com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject = new com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry();
        if (entity.getExpiryDate() != null) {
            modelObject.setExpiryDate(DateTimeUtils.getIsoDateString(entity.getExpiryDate()));
        }
        modelObject.setId(entity.getId());
        modelObject.setIpAddress(Inet4ConverterValidator.convertIpValueToString(entity.getIpAddress()));
        modelObject.setIpAddressState(IpAddressState.valueOf(entity.getIpAddressState()));
        modelObject.setUsageId(entity.getIpAddressUsageId());

        return modelObject;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.IpAddressPoolDtoAssembler#transform(com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry)
     */
    @Override
    public IpAddressPoolEntry transform(com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject) {
        if (modelObject == null) {
            return null;
        }
        IpAddressPoolEntry entity = new IpAddressPoolEntry();
        entity.setIpAddress(Inet4ConverterValidator.convertIpStringToLong(modelObject.getIpAddress()));
        entity.setIpAddressState(modelObject.getIpAddressState().toString());
        return entity;
    }

}
