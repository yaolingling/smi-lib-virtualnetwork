/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;
import com.dell.isg.smi.virtualnetwork.model.NetworkType;
import com.dell.isg.smi.virtualnetwork.model.StaticIpv4NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
@Component
public class NetworkConfigurationDtoAssemblerImpl implements NetworkConfigurationDtoAssembler {

    @Override
    public Network transform(NetworkConfiguration networkConfiguration) {
        if (networkConfiguration == null) {
            return null;
        }
        final Network network = new Network();
        network.setId(networkConfiguration.getId());
        network.setName(networkConfiguration.getName());
        network.setDescription(networkConfiguration.getDescription());
        network.setVlanId(networkConfiguration.getVlanId());
        network.setStatic(networkConfiguration.isStatic());
        network.setType(NetworkType.fromValue(networkConfiguration.getType()));
        network.setCreatedBy(networkConfiguration.getCreatedBy());
        network.setUpdatedBy(networkConfiguration.getUpdatedBy());
        if (networkConfiguration.getCreatedTime() != null) {
            network.setCreatedTime(networkConfiguration.getCreatedTime().toString());
        }
        if (networkConfiguration.getUpdatedTime() != null) {
            network.setUpdatedTime(networkConfiguration.getUpdatedTime().toString());
        }

        // Set static fields
        if (networkConfiguration.isStatic()) {
            setStaticFieldsForWSObject(networkConfiguration, network);
        }
        return network;
    }


    private void setStaticFieldsForWSObject(NetworkConfiguration networkConfiguration, Network network) {
        final StaticIpv4NetworkConfiguration staticIpv4NetworkConfiguration = new StaticIpv4NetworkConfiguration();

        staticIpv4NetworkConfiguration.setGateway(networkConfiguration.getGateway());
        staticIpv4NetworkConfiguration.setSubnet(networkConfiguration.getSubnet());
        staticIpv4NetworkConfiguration.setPrimaryDns(networkConfiguration.getPrimaryDns());
        staticIpv4NetworkConfiguration.setSecondaryDns(networkConfiguration.getSecondaryDns());
        staticIpv4NetworkConfiguration.setDnsSuffix(networkConfiguration.getDnsSuffix());

        setIpAddressRangeForWSObject(networkConfiguration, staticIpv4NetworkConfiguration);
        network.setStaticIpv4NetworkConfiguration(staticIpv4NetworkConfiguration);
    }


    private void setIpAddressRangeForWSObject(NetworkConfiguration networkConfiguration, StaticIpv4NetworkConfiguration staticProperties) {
        List<IpRange> ipRangeList = new ArrayList<IpRange>();
        for (IpAddressRange ipAddressRange : networkConfiguration.getIpAddressRanges()) {
            // Hide temporary IP ranges for now. At some point we may want to expose them. They are used
            // for one-off assigned IPs that did not come from a pool and will be deleted when released.
            if (!ipAddressRange.isTemporary()) {
                IpRange ipRange = new IpRange();
                ipRange.setId(ipAddressRange.getId());
                ipRange.setStartingIp(Inet4ConverterValidator.convertIpValueToString(ipAddressRange.getStartIpAddress()));
                ipRange.setEndingIp(Inet4ConverterValidator.convertIpValueToString(ipAddressRange.getEndIpAddress()));
                ipRangeList.add(ipRange);
            }
        }
        staticProperties.getIpRange().addAll(ipRangeList);
    }


    @Override
    public NetworkConfiguration transform(Network network) {
        NetworkConfiguration networkConfiguration = null;
        if (network == null) {
            return null;
        }
        networkConfiguration = new NetworkConfiguration();
        networkConfiguration.setId(network.getId());
        networkConfiguration.setName(network.getName());
        networkConfiguration.setDescription(network.getDescription());
        networkConfiguration.setVlanId(network.getVlanId());
        if (network.getType() != null) {
            networkConfiguration.setType(network.getType().value());
        }
        networkConfiguration.setStatic(network.isStatic());
        // Set static fields
        if (network.isStatic() && network.getStaticIpv4NetworkConfiguration() != null) {
            setStaticFields(networkConfiguration, network.getStaticIpv4NetworkConfiguration());
        }

        return networkConfiguration;
    }


    private void setStaticFields(NetworkConfiguration networkConfiguration, StaticIpv4NetworkConfiguration staticProperties) {
        networkConfiguration.setGateway(staticProperties.getGateway());
        networkConfiguration.setSubnet(staticProperties.getSubnet());
        networkConfiguration.setPrimaryDns(staticProperties.getPrimaryDns());
        networkConfiguration.setSecondaryDns(staticProperties.getSecondaryDns());
        networkConfiguration.setDnsSuffix(staticProperties.getDnsSuffix());

        Set<IpAddressRange> ipAddressRangeList = new HashSet<IpAddressRange>();
        if (staticProperties.getIpRange() != null) {
            for (IpRange ipRange : staticProperties.getIpRange()) {
                IpAddressRange ipAddressRange = transform(ipRange, networkConfiguration);
                ipAddressRangeList.add(ipAddressRange);
            }
            networkConfiguration.setIpAddressRanges(ipAddressRangeList);
        }
    }


    @Override
    public IpAddressRange transform(IpRange ipRange, NetworkConfiguration networkConfiguration) {
        IpAddressRange ipAddressRange = new IpAddressRange();
        if (null == ipRange) {
            return null;
        }
        // only setting the ID for update operation. For create operation, id should be null
        if (null != ipRange.getId() && !(0 == ipRange.getId())) {
            ipAddressRange.setId(ipRange.getId());
        }
        ipAddressRange.setStartIpAddress(Inet4ConverterValidator.convertIpStringToLong(ipRange.getStartingIp()));
        ipAddressRange.setEndIpAddress(Inet4ConverterValidator.convertIpStringToLong(ipRange.getEndingIp()));

        // creating IPAddresspool based on IPAddressRange ranges and so setting it to IPAddressRange object is not required here, moved that code to Mgr layer
        ipAddressRange.setNetworkConfiguration(networkConfiguration);

        return ipAddressRange;
    }

}
