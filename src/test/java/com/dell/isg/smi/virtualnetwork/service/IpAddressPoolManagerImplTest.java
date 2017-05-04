/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.dell.isg.smi.virtualnetwork.repository.BaseEntityRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressPoolEntryRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressRangeRepository;
import com.dell.isg.smi.virtualnetwork.repository.NetworkConfigurationRepository;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolDtoAssembler;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolManager;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolManagerImpl;

import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
@RunWith(JMockit.class)
public class IpAddressPoolManagerImplTest {

    @Tested
    IpAddressPoolManagerImpl ipAddressPoolManager;

    @Injectable
    IpAddressRangeRepository ipAddressRangeRepository;

    @Injectable
    IpAddressPoolEntryRepository ipAddressPoolEntryRepository;

    @Injectable
    BaseEntityRepository baseEntityRepository;

    @Injectable
    NetworkConfigurationRepository networkConfigurationRepository;

    @Injectable
    IpAddressPoolDtoAssembler ipAddressPoolDtoAssembler;


    @Before
    public void setup() {
        ipAddressPoolManager = new IpAddressPoolManagerImpl();
    }


    @Test
    public void ipAddressPoolManagerImplShouldImplementIIpAddressPoolManager() {
        final Class<?>[] interfaces = IpAddressPoolManagerImpl.class.getInterfaces();
        assertThat(interfaces, is(notNullValue()));

        boolean implementedIIpAddressPoolManager = false;
        for (final Class<?> interfaceClass : interfaces) {
            if (interfaceClass == IpAddressPoolManager.class) {
                implementedIIpAddressPoolManager = true;
                break;
            }
        }

        assertThat(implementedIIpAddressPoolManager, is(true));
    }


    @Test
    public void ipAddressPoolEntryRepositoryShouldHaveAutowiredAnnotation() {
        Field field = null;
        try {
            field = IpAddressPoolManagerImpl.class.getDeclaredField("ipAddressPoolEntryRepository");
        } catch (NoSuchFieldException | SecurityException ignore) {
        }

        assertThat(field, is(notNullValue()));

        Autowired autowired = field.getAnnotation(Autowired.class);
        assertNotNull(autowired);
    }


    @Test
    public void ipAddressPoolDtoAssemblerShouldHaveAutowiredAnnotation() {
        Field field = null;
        try {
            field = IpAddressPoolManagerImpl.class.getDeclaredField("ipAddressPoolDtoAssembler");
        } catch (NoSuchFieldException | SecurityException ignore) {
        }

        assertThat(field, is(notNullValue()));

        Autowired autowired = field.getAnnotation(Autowired.class);
        assertNotNull(autowired);
    }


    @Test
    public void isIpAddressValidShouldReturnTrueWhenIpAddressIsNotBlank() {
        final String ipAddress = "172.31.59.156";
        boolean actual = ipAddressPoolManager.isIpAddressValid(ipAddress);
        assertThat(actual, is(true));

    }
}
