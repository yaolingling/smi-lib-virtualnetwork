/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolDtoAssembler;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolDtoAssemblerImpl;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
public class IpAddressPoolDtoAssemblerImplTest {

    private IpAddressPoolDtoAssemblerImpl ipAddressPoolDtoAssemblerImpl;


    @Before
    public void setup() {
        ipAddressPoolDtoAssemblerImpl = new IpAddressPoolDtoAssemblerImpl();
    }


    @Test
    public void ipAddressPoolDtoAssemblerImplShouldImplementIIpAddressPoolDtoAssembler() {
        final Class<?>[] interfaces = IpAddressPoolDtoAssemblerImpl.class.getInterfaces();
        assertThat(interfaces, is(notNullValue()));

        boolean implementedIIpAddressPoolDtoAssembler = false;
        for (final Class<?> interfaceClass : interfaces) {
            if (interfaceClass == IpAddressPoolDtoAssembler.class) {
                implementedIIpAddressPoolDtoAssembler = true;
                break;
            }
        }

        assertThat(implementedIIpAddressPoolDtoAssembler, is(true));
    }


    @Test
    public void transformIpAddressPoolEntryShouldReturnNullWhenEntityIsNull() {
        final IpAddressPoolEntry entity = null;

        final com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry actual = ipAddressPoolDtoAssemblerImpl.transform(entity);

        assertNull(actual);
    }


    @Test
    public void transformIpAddressPoolEntryShouldBePopulated() {
        final IpAddressPoolEntry entity = new IpAddressPoolEntry();
        entity.setIpAddress(Inet4ConverterValidator.convertIpStringToLong("172.31.59.156"));
        entity.setId(100L);
        entity.setIPAddressState(IpAddressState.AVAILABLE);
        entity.setName("Ip Address Pool");
        final com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry actual = ipAddressPoolDtoAssemblerImpl.transform(entity);

        assertThat(actual.getId(), is(equalTo(entity.getId())));
        assertThat(actual.getIpAddressState().toString(), is(equalTo(entity.getIpAddressState().toString())));
        assertThat(actual.getIpAddress(), is(equalTo("172.31.59.156")));
    }


    @Test
    public void transformIpAddressPoolEntryShouldReturnNullWhenModelObjectIsNull() {
        final com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject = null;

        final IpAddressPoolEntry actual = ipAddressPoolDtoAssemblerImpl.transform(modelObject);

        assertNull(actual);
    }


    @Test
    public void transformIpAddressPoolEntryEntityShouldBePopulated() {
        final com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry modelObject = new com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry();
        modelObject.setIpAddress("172.31.59.156");
        modelObject.setIpAddressState(IpAddressState.RESERVED);
        final IpAddressPoolEntry actual = ipAddressPoolDtoAssemblerImpl.transform(modelObject);

        assertThat(actual.getIpAddressState().toString(), is(equalTo(modelObject.getIpAddressState().toString())));
        assertThat(actual.getIpAddress(), is(equalTo(Inet4ConverterValidator.convertIpStringToLong("172.31.59.156"))));
    }
}
