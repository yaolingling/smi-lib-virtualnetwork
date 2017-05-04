/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;
import com.dell.isg.smi.virtualnetwork.model.NetworkType;
import com.dell.isg.smi.virtualnetwork.model.StaticIpv4NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationDtoAssembler;
import com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationDtoAssemblerImpl;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
public class NetworkConfigurationDtoAssemblerImplTest {

    private NetworkConfigurationDtoAssemblerImpl networkConfigurationDtoAssemblerImpl;


    @Before
    public void setup() {
        networkConfigurationDtoAssemblerImpl = new NetworkConfigurationDtoAssemblerImpl();
    }


    @Test
    public void networkConfigurationDtoAssemblerImplShouldImplementINetworkConfigurationDtoAssembler() {
        final Class<?>[] interfaces = NetworkConfigurationDtoAssemblerImpl.class.getInterfaces();
        assertThat(interfaces, is(notNullValue()));

        boolean implementedINetworkConfigurationDtoAssembler = false;
        for (final Class<?> interfaceClass : interfaces) {
            if (interfaceClass == NetworkConfigurationDtoAssembler.class) {
                implementedINetworkConfigurationDtoAssembler = true;
                break;
            }
        }

        assertThat(implementedINetworkConfigurationDtoAssembler, is(true));
    }


    @Test
    public void transformNetworkShouldReturnNullWhenEntityIsNull() {
        final NetworkConfiguration entity = null;

        final Network actual = networkConfigurationDtoAssemblerImpl.transform(entity);

        assertNull(actual);
    }


    @Test
    public void transformNetworkShouldBePopulated() {

        final NetworkConfiguration entity = new NetworkConfiguration();
        entity.setCreatedBy("admin");
        entity.setDescription("network configuration description");
        entity.setGateway("172.162.0.1");
        entity.setSubnet("255.255.255.0");
        entity.setType(NetworkType.STORAGE_ISCSI_SAN.value());
        entity.setName("TestISSCI_1");
        entity.setVlanId(90);
        entity.setStatic(true);
        entity.setId(100L);

        IpAddressRange ipAddressRange = new IpAddressRange();
        ipAddressRange.setStartIpAddress(Inet4ConverterValidator.convertIpStringToLong("172.162.0.90"));
        ipAddressRange.setEndIpAddress(Inet4ConverterValidator.convertIpStringToLong("172.162.0.92"));
        Set<IpAddressRange> ipRangeSet = new HashSet<IpAddressRange>();
        ipAddressRange.setNetworkConfiguration(entity);

        ipRangeSet.add(ipAddressRange);
        entity.setIpAddressRanges(ipRangeSet);

        final Network actual = networkConfigurationDtoAssemblerImpl.transform(entity);

        assertThat(actual.getId(), is(equalTo(entity.getId())));
        assertThat(actual.getName(), is(equalTo(entity.getName())));
        assertThat(actual.getType().value(), is(equalTo(entity.getType())));

    }


    @Test
    public void transformNetworkConfigurationShouldReturnNullWhenModelObjectIsNull() {
        final Network modelObject = null;

        final NetworkConfiguration actual = networkConfigurationDtoAssemblerImpl.transform(modelObject);

        assertNull(actual);
    }


    @Test
    public void transformNetworkConfigurationShouldBePopulated() {
        final long currentTime = 1444080293; // 2015/10/05 04:24:53 CST
        final StaticIpv4NetworkConfiguration staticConfig = new StaticIpv4NetworkConfiguration();
        staticConfig.setDnsSuffix("dnsSuffix");
        staticConfig.setGateway("172.162.0.1");
        staticConfig.setSubnet("255.255.255.0");

        List<IpRange> ipRangeList = new ArrayList<IpRange>(5);
        for (IpRange ipRange : ipRangeList) {
            ipRange.setStartingIp("172.31.25.70");
            ipRange.setEndingIp("172.31.25.72");
            ipRangeList.add(ipRange);
        }

        staticConfig.getIpRange().addAll(ipRangeList);

        final Network modelObject = new Network();
        modelObject.setCreatedBy("admin");
        modelObject.setCreatedTime(String.valueOf(currentTime));
        modelObject.setDescription("network configuration description");
        modelObject.setStaticIpv4NetworkConfiguration(staticConfig);
        modelObject.setName("TestISSCI_1");
        modelObject.setVlanId(90);
        modelObject.setStatic(true);
        modelObject.setId(100L);
        modelObject.setType(NetworkType.PRIVATE_LAN);

        final NetworkConfiguration actual = networkConfigurationDtoAssemblerImpl.transform(modelObject);

        assertThat(actual.getId(), is(equalTo(modelObject.getId())));
        assertThat(actual.getName(), is(equalTo(modelObject.getName())));
        assertThat(actual.getType(), is(equalTo(modelObject.getType().value())));
        assertThat(actual.getDescription(), is(equalTo(modelObject.getDescription())));
        assertThat(actual.getVlanId(), is(equalTo(modelObject.getVlanId())));
        assertThat(actual.isStatic(), is(equalTo(modelObject.isStatic())));
        assertThat(actual.getGateway(), is(equalTo(modelObject.getStaticIpv4NetworkConfiguration().getGateway())));
        assertThat(actual.getSubnet(), is(equalTo(modelObject.getStaticIpv4NetworkConfiguration().getSubnet())));
        assertThat(actual.getDnsSuffix(), is(equalTo(modelObject.getStaticIpv4NetworkConfiguration().getDnsSuffix())));

    }


    @Test
    public void transformIpAddressRangeShouldReturnNullWhenModelObjectIsNull() {
        final IpRange ipRange = null;
        final NetworkConfiguration networkConfiguration = null;

        final IpAddressRange actual = networkConfigurationDtoAssemblerImpl.transform(ipRange, networkConfiguration);

        assertNull(actual);
    }


    @Test
    public void transformIpAddressRangeConfigurationShouldBePopulated() {
        final IpRange ipRange = new IpRange();
        ipRange.setId(100L);
        ipRange.setStartingIp(Inet4ConverterValidator.convertIpValueToString(2896298074L));
        ipRange.setEndingIp(Inet4ConverterValidator.convertIpValueToString(2896298076L));

        final NetworkConfiguration networkConfiguration = new NetworkConfiguration();
        networkConfiguration.setName("network config");

        final IpAddressRange actual = networkConfigurationDtoAssemblerImpl.transform(ipRange, networkConfiguration);

        assertThat(actual.getNetworkConfiguration().getName(), is(equalTo(networkConfiguration.getName())));
        assertThat(Inet4ConverterValidator.convertIpValueToString(actual.getStartIpAddress()), is(equalTo("172.162.0.90")));
        assertThat(Inet4ConverterValidator.convertIpValueToString(actual.getEndIpAddress()), is(equalTo("172.162.0.92")));
        assertThat(actual.getId(), is(equalTo(ipRange.getId())));
    }

}
