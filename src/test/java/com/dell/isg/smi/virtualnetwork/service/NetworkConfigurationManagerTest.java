/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.elm.model.PagedResult;
import com.dell.isg.smi.commons.elm.utilities.PaginationUtils;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;
import com.dell.isg.smi.virtualnetwork.model.StaticIpv4NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressPoolEntryRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressRangeRepository;
import com.dell.isg.smi.virtualnetwork.repository.NetworkConfigurationRepository;
import com.dell.isg.smi.virtualnetwork.service.IpAddressPoolManager;
import com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationDtoAssembler;
import com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager;
import com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManagerImpl;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
@RunWith(JMockit.class)
public class NetworkConfigurationManagerTest {

    public static final long NETWORK_CONFIG_ID_NOTFOUND = 100404L;
    private static final int OFFSET = 0;
    private static final int LIMIT = 10;

    @Tested
    NetworkConfigurationManager networkConfigurationManager;

    @Injectable
    IpAddressPoolManager ipAddressPoolManager;

    @Injectable
    NetworkConfigurationRepository networkConfigurationRepository;

    @Injectable
    IpAddressRangeRepository ipAddressRangeRepository;

    @Injectable
    NetworkConfigurationDtoAssembler networkConfigurationDtoAssembler;

    @Injectable
    IpAddressPoolEntryRepository ipAddressPoolEntryRepository;


    @Before
    public void setup() {
        networkConfigurationManager = new NetworkConfigurationManagerImpl();
    }


    @Test
    public void networkConfiurationManagerImplShouldImplementNetworkConfigurationManager() {
        final Class<?>[] interfaces = NetworkConfigurationManagerImpl.class.getInterfaces();
        assertThat(interfaces, is(notNullValue()));

        boolean implementedNetworkConfigurationManager = false;
        for (final Class<?> interfaceClass : interfaces) {
            if (interfaceClass == NetworkConfigurationManager.class) {
                implementedNetworkConfigurationManager = true;
                break;
            }
        }

        assertThat(implementedNetworkConfigurationManager, is(true));
    }


    @Test
    public void networkConfigurationDaoShouldHaveAutowiredAnnotation() {
        Field field = null;
        try {
            field = NetworkConfigurationManagerImpl.class.getDeclaredField("networkConfigurationRepository");
        } catch (NoSuchFieldException | SecurityException ignore) {
        }

        assertThat(field, is(notNullValue()));

        Autowired autowired = field.getAnnotation(Autowired.class);
        assertNotNull(autowired);
    }


    @Test
    public void networkConfigurationDtoAssemblerShouldHaveAutowiredAnnotation() {
        Field field = null;
        try {
            field = NetworkConfigurationManagerImpl.class.getDeclaredField("networkConfigurationDtoAssembler");
        } catch (NoSuchFieldException | SecurityException ignore) {
        }

        assertThat(field, is(notNullValue()));

        Autowired autowired = field.getAnnotation(Autowired.class);
        assertNotNull(autowired);
    }


    @Test(expected = RuntimeCoreException.class)
    public void isNetworkIdValidShouldThrowRuntimeCoreExceptionWhenNetworkIdIsZero() {
        final boolean actual = networkConfigurationManager.isNetworkIdValid(0L);
        assertThat(actual, is(false));
    }


    @Test(expected = RuntimeCoreException.class)
    public void isNetworkIdValidShouldThrowRuntimeCoreExceptionWhenNetworkIdIsLessthanZero() {
        boolean actual = networkConfigurationManager.isNetworkIdValid(-1L);
        assertThat(actual, is(false));

        actual = networkConfigurationManager.isNetworkIdValid(-50L);
        assertThat(actual, is(false));

        actual = networkConfigurationManager.isNetworkIdValid(-400L);
        assertThat(actual, is(false));
    }


    /* Get Network Tests */
    @Test
    public void isNetworkIdValidShouldReturnTrueWhenNetworkIdIsGreaterthanZero() {
        final long networkId = 999L;

        boolean actual = networkConfigurationManager.isNetworkIdValid(networkId);
        assertThat(actual, is(true));
    }


    @Test(expected = RuntimeCoreException.class)
    public void getNetworkShouldReturnNullWhenNetworkConfigurationDaoReturnNull() {
        new Expectations() {
            {
                networkConfigurationRepository.findOne(anyLong);
                result = null;
            }
        };

        final Object actual = networkConfigurationManager.getNetwork(NETWORK_CONFIG_ID_NOTFOUND);
        assertThat(actual, is(nullValue()));
    }


    @Test(expected = RuntimeCoreException.class)
    public void getNetworkShouldDelegateToNetworkConfigurationDaoFindById() {
        final long networkId = 999L;

        new Expectations() {
            {
                networkConfigurationRepository.findOne(anyLong);
                result = null;
            }
        };

        networkConfigurationManager.getNetwork(networkId);

        new Verifications() {
            {
                networkConfigurationRepository.findOne(anyLong);
                times = 1;
            }
        };
    }


    @Test
    public void getNetworkShouldDelegateToNetworkConfigurationDtoAssemblerTransformNetworkWhenNetworkConfigurationDaoReturnNotNull(@Mocked NetworkConfiguration networkConfiguration) {
        final long networkId = 999L;

        new Expectations() {
            {
                networkConfigurationRepository.findOne(anyLong);
                result = networkConfiguration;
            }
        };

        networkConfigurationManager.getNetwork(networkId);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform((NetworkConfiguration) any);
                times = 1;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void getNetworkShouldNotDelegateToNetworkConfigurationDtoAssemblerTransformNetworkWhenNetworkConfigurationDaoReturnNull() {
        final long networkId = 999L;

        new Expectations() {
            {
                networkConfigurationRepository.findOne(anyLong);
                result = null;
            }
        };

        networkConfigurationManager.getNetwork(networkId);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform((NetworkConfiguration) any);
                times = 0;
            }
        };
    }


    /* Get All Networks Tests */
    @Test
    public void getAllNetworksShouldDelegateToNetworkConfigurationDaoFindPagedNetworksWhenNetworkConfigurationDoesExist() throws IllegalArgumentException, Exception {

        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(new ArrayList<NetworkConfiguration>());
            }
        };

        networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);

        new Verifications() {
            {
                // int page = OFFSET / LIMIT;
                // PageRequest pageRequest = new PageRequest(page,LIMIT);
                // Page<NetworkConfiguration> networkConfigurationPage = networkConfigurationRepository.findAll((Pageable) any);
                // networkConfigurationRepository.findAll(pageRequest);
                // times = 1;
            }
        };
    }


    @Test
    public void getAllNetworksShouldReturnZeroPageResultWhenNetworkConfigurationDoesNotExistAndNetworkConfigurationDaoReturnNull() throws IllegalArgumentException, Exception {
        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(new ArrayList<NetworkConfiguration>());
            }
        };

        final Object actual = networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);
        assertThat(actual, is(equalTo(PaginationUtils.ZERO_RESULT)));
    }


    @Test
    public void getAllNetworksShouldReturnZeroPageResultWhenNetworkConfigurationDoesExistAndNetworkConfigurationDaoReturnEmptyList() throws IllegalArgumentException, Exception {
        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(new ArrayList<NetworkConfiguration>());
            }
        };

        final Object actual = networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);
        assertThat(actual, is(equalTo(PaginationUtils.ZERO_RESULT)));
    }


    @Test
    public void getAllNetworksShouldDelegateToNetworkConfigurationDtoAssemblerTransformNetworkConfigurationWhenNetworkConfigurationDoesExistAndNetworkConfigurationDaoReturnNotNull() throws IllegalArgumentException, Exception {

        final List<NetworkConfiguration> networkConfigurationList = new ArrayList<NetworkConfiguration>();
        final NetworkConfiguration networkConfiguration = new NetworkConfiguration();
        networkConfiguration.setStatic(false);
        networkConfigurationList.add(networkConfiguration);

        final NetworkConfiguration networkConfiguration2 = new NetworkConfiguration();
        networkConfiguration2.setStatic(false);
        networkConfigurationList.add(networkConfiguration2);
        Page<NetworkConfiguration> pageResult = new PageImpl<NetworkConfiguration>(networkConfigurationList);

        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = pageResult;

                networkConfigurationRepository.count();
                result = 2;
            }
        };

        networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform((NetworkConfiguration) any);
                times = 2;
            }
        };
    }


    @Test
    public void getAllNetworksShouldNotDelegateToNetworkConfigurationDtoAssemblerTransformNetworkConfigurationWhenNetworkConfigurationDoesExistAndNetworkConfigurationDaoReturnNull() throws IllegalArgumentException, Exception {

        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(new ArrayList<NetworkConfiguration>());
            }
        };

        networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform((NetworkConfiguration) any);
                times = 0;
            }
        };
    }


    @Test
    public void getAllNetworksShouldDelegateToNetworkConfigurationDaoCountRecordsWhenNetworkConfigurationDoesExistAndNetworkConfigurationDaoReturnNotNull() throws IllegalArgumentException, Exception {

        final List<NetworkConfiguration> networkConfigurationList = new ArrayList<NetworkConfiguration>();
        final NetworkConfiguration networkConfiguration1 = new NetworkConfiguration();
        networkConfigurationList.add(networkConfiguration1);

        final NetworkConfiguration networkConfiguration2 = new NetworkConfiguration();
        networkConfigurationList.add(networkConfiguration2);

        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(networkConfigurationList);

                networkConfigurationRepository.count();
                result = 2;
            }
        };

        PagedResult pagedResult = networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);
        assertNotNull(pagedResult.getData());
        assertEquals(2, pagedResult.getData().size());

        // new Verifications() {
        // {
        // networkConfigurationRepository.count();
        // times = 1;
        // }
        // };
    }


    @Test
    public void getAllNetworksShouldDelegateToPaginateWhenNetworkConfigurationDoesExistAndNetworkConfigurationDaoReturnNotNull(@Mocked Network network) throws IllegalArgumentException, Exception {

        final List<NetworkConfiguration> networkConfigurationList = new ArrayList<NetworkConfiguration>();
        final NetworkConfiguration networkConfiguration1 = new NetworkConfiguration();
        networkConfigurationList.add(networkConfiguration1);

        final NetworkConfiguration networkConfiguration2 = new NetworkConfiguration();
        networkConfigurationList.add(networkConfiguration2);

        final int total = 30;

        new Expectations() {
            {
                int page = OFFSET / LIMIT;
                PageRequest pageRequest = new PageRequest(page, LIMIT);
                networkConfigurationRepository.findAll(pageRequest);
                result = new PageImpl<NetworkConfiguration>(networkConfigurationList);
                networkConfigurationRepository.count();
                result = total;
                networkConfigurationDtoAssembler.transform((NetworkConfiguration) any);
                result = network;
            }
        };

        final PagedResult pagedResult = networkConfigurationManager.getAllNetworks(OFFSET, LIMIT);
        assertThat(pagedResult, is(notNullValue()));

        assertThat(pagedResult.getData(), is(notNullValue()));
        assertThat(pagedResult.getData().size(), is(2));
        assertThat(pagedResult.getPagination().getLimit(), is(10L));
        assertThat(pagedResult.getPagination().getOffset(), is(0L));
        assertThat(pagedResult.getPagination().getTotal(), is(30L));
    }


    /* Delete Network Configuration Tests */
    // @Test(expected = RuntimeCoreException.class)
    @Test
    public void deleteNetworkShouldDelegateToNetworkConfigurationDaoFindById(@Mocked NetworkConfiguration networkConfig) {
        final long networkId = 999L;
        new Expectations() {
            {

            }
        };

        networkConfigurationManager.deleteNetwork(networkConfig, networkId);

        new Verifications() {
            {
                // int page = OFFSET / LIMIT;
                // PageRequest pageRequest = new PageRequest(page,LIMIT);
                // networkConfigurationRepository.findAll(pageRequest);
                networkConfigurationRepository.delete(networkId);
                times = 1;
            }
        };
    }


    // @Test(expected = RuntimeCoreException.class)
    @Test
    public void deleteNetworkShouldDelegateToNetworkConfigurationDaoDeleteNetworkConfiguration(@Mocked NetworkConfiguration networkConfig) {
        final long networkId = 999L;

        networkConfigurationManager.deleteNetwork(networkConfig, networkId);

        new Verifications() {
            {
                // networkConfigurationRepository.findOne(networkId);
                // times = 1;
                networkConfigurationRepository.delete(networkId);
                times = 1;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void deleteNetworkShouldNotDelegateToNetworkConfigurationDaoDeleteNetworkConfigurationWhenIpAddressesAreAssignedOrReserved(@Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressPoolEntry entry, @Mocked IpAddressRange ipRange) {
        final long networkId = 999L;

        List<IpAddressPoolEntry> list = new ArrayList<IpAddressPoolEntry>();
        list.add(entry);

        Set<IpAddressRange> ipRanges = new HashSet<IpAddressRange>();
        ipRanges.add(ipRange);

        new Expectations() {
            {
                networkConfiguration.getIpAddressRanges();
                result = ipRanges;
                ipAddressPoolManager.findByStateAndRangeId(anyLong, anyString);
                result = list;
            }
        };

        networkConfigurationManager.deleteNetwork(networkConfiguration, networkId);

        new Verifications() {
            {
                networkConfigurationRepository.delete(networkId);
                times = 0;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void createNetworkShouldDelegateToNetworkConfigurationDtoAssembler(@Mocked Network network, @Mocked NetworkConfiguration networkConfiguration) {
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);

            }
        };

        networkConfigurationManager.createNetwork(network);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform(network);
                times = 1;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void createNetworkShouldDelegateToNetworkConfigurationDaoFindOverlappingIPRanges(@Mocked Network network, @Mocked NetworkConfiguration networkConfiguration) {
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);
            }
        };

        networkConfigurationManager.createNetwork(network);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform(network);
                times = 1;

                Set<IpAddressRange> ipAddressRangeSet = new HashSet<IpAddressRange>(networkConfiguration.getIpAddressRanges());
                ipAddressRangeRepository.findOverlappingIpRanges(ipAddressRangeSet);
                times = 1;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void createNetworkShouldThrowRuntimeCoreExceptionWhenIpAddressRangeListIsNotNull(@Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        // final List<IpAddressRange> ipAddressRangeList = new ArrayList<IpAddressRange>();
        // ipAddressRangeList.add(ipAddressRange);

        IpRange ipRange = new IpRange();
        ipRange.setStartingIp("72.162.0.10");
        ipRange.setEndingIp("72.162.0.19");

        StaticIpv4NetworkConfiguration StaticIpv4NetworkConfiguration = new StaticIpv4NetworkConfiguration();
        StaticIpv4NetworkConfiguration.setPrimaryDns("72.162.0.1");
        StaticIpv4NetworkConfiguration.setSecondaryDns("72.162.0.2");
        StaticIpv4NetworkConfiguration.getIpRange().add(ipRange);

        Network network = new Network();
        network.setName("network name");
        network.setStatic(true);
        network.setVlanId(90);
        network.setId(100l);
        network.setStaticIpv4NetworkConfiguration(StaticIpv4NetworkConfiguration);

        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);

                // Set<IpAddressRange> ipAddressRangeSet = new HashSet<IpAddressRange>(networkConfiguration.getIpAddressRanges());
                // ipAddressRangeRepository.findOverlappingIpRanges(networkConfiguration.getIpAddressRanges());
                // result = ipAddressRangeList;
            }
        };

        networkConfigurationManager.createNetwork(network);

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform(network);
                times = 1;

                Set<IpAddressRange> ipAddressRangeSet = new HashSet<IpAddressRange>(networkConfiguration.getIpAddressRanges());
                ipAddressRangeRepository.findOverlappingIpRanges(ipAddressRangeSet);
                times = 1;
            }
        };
    }


    @Test
    public void createNetworkShouldHaveTransactionalAnnotation(@Mocked Network network) throws NoSuchMethodException, SecurityException {

        final Method method = NetworkConfigurationManagerImpl.class.getDeclaredMethod("createNetwork", Network.class);

        assertThat(method.getAnnotation(Transactional.class), is(notNullValue()));
    }


    @Test
    public void updateNetworkShouldHaveTransactionalAnnotation(@Mocked Network network) throws NoSuchMethodException, SecurityException {

        final Method method = NetworkConfigurationManagerImpl.class.getDeclaredMethod("updateNetwork", Network.class, long.class);

        assertThat(method.getAnnotation(Transactional.class), is(notNullValue()));
    }


    @Test
    public void deleteNetworkShouldHaveTransactionalAnnotation(@Mocked Network network) throws NoSuchMethodException, SecurityException {

        final Method method = NetworkConfigurationManagerImpl.class.getDeclaredMethod("deleteNetwork", NetworkConfiguration.class, long.class);

        assertThat(method.getAnnotation(Transactional.class), is(notNullValue()));
    }


    /*
     * @Test public void mapNetworkTypeToAdpaterNetworkTypeShouldReturnTypeAsExpected() { String type = "FIP_SNOOPING"; NetworkType networkType =
     * Deencapsulation.invoke(networkConfigurationManager, "mapNetworkTypeToAdapterNetworkType", type); assertThat(networkType.toString(), is(equalTo("PROMISCUOUS_NETWORK")));
     * 
     * String type1 = "STORAGE_ISCSI_SAN"; NetworkType networkType1 = Deencapsulation.invoke(networkConfigurationManager, "mapNetworkTypeToAdapterNetworkType", type1);
     * assertThat(networkType1.toString(), is(equalTo("STORAGE_NETWORK")));
     * 
     * String type2 = "HYPERVISOR_MANAGEMENT"; NetworkType networkType2 = Deencapsulation.invoke(networkConfigurationManager, "mapNetworkTypeToAdapterNetworkType", type2);
     * assertThat(networkType2.toString(), is(equalTo("DATA_NETWORK")));
     * 
     * String type3 = "FILESHARE"; NetworkType networkType3 = Deencapsulation.invoke(networkConfigurationManager, "mapNetworkTypeToAdapterNetworkType", type3);
     * assertThat(networkType3.toString(), is(equalTo("DATA_NETWORK"))); }
     */

    @Test(expected = RuntimeCoreException.class)
    public void updateNetworkShouldDelegateToNetworkConfigurationDtoAssembler(@Mocked Network network, @Mocked NetworkConfiguration networkConfiguration) {
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);
            }
        };

        networkConfigurationManager.updateNetwork(network, networkConfiguration.getId());

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform(network);
                times = 1;
            }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void updateNetworkShouldDelegateToNetworkConfigurationDaoFindById(@Mocked Network network, @Mocked NetworkConfiguration networkConfiguration) {
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);
            }
        };

        networkConfigurationManager.updateNetwork(network, networkConfiguration.getId());

        new Verifications() {
            {
                networkConfigurationDtoAssembler.transform(network);
                times = 1;
                networkConfigurationRepository.findOne(networkConfiguration.getId());
                times = 1;
            }
        };
    }


    /*
     * @Test(expected = BusinessValidationException.class) public void updateNetworkShouldThrowBusinessValidationException(@Mocked Network network, @Mocked NetworkConfiguration
     * networkConfiguration, @Mocked NetworkConfiguration oldNetworkConfiguration) { new Expectations(networkConfigurationManager) { {
     * networkConfigurationDtoAssembler.transform(network); result = networkConfiguration; // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration",
     * networkConfiguration); //Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);
     * networkConfigurationRepository.findOne(networkConfiguration.getId()); result = oldNetworkConfiguration; networkConfiguration.isStatic(); result = true;
     * oldNetworkConfiguration.isStatic(); result = false; } };
     * 
     * networkConfigurationManager.updateNetwork(network, networkConfiguration.getId());
     * 
     * new Verifications() { { networkConfigurationDtoAssembler.transform(network); times = 1; networkConfigurationRepository.findOne(networkConfiguration.getId()); times = 1; } };
     * }
     */

    @Test(expected = RuntimeCoreException.class)
    public void updateNetworkShouldShouldDelegateToNetworkConfigurationDaoFindOverlappingRanges(@Mocked Network network, @Mocked NetworkConfiguration networkConfiguration, @Mocked NetworkConfiguration oldNetworkConfiguration) {
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationDtoAssembler.transform(network);
                result = networkConfiguration;
                networkConfigurationRepository.findOne(networkConfiguration.getId());
                result = oldNetworkConfiguration;
                // Deencapsulation.invoke(networkConfigurationManager, "validateNetworkConfiguration", networkConfiguration);
                // Deencapsulation.invoke(networkConfigurationManager, "validateIpCountForStaticNetwork", networkConfiguration);
                // networkConfigurationRepository.findOne(networkConfiguration.getId());
                // result = oldNetworkConfiguration;
                // networkConfiguration.isStatic();
                // result = true;
                // oldNetworkConfiguration.isStatic();
                // result = true;
            }
        };

        networkConfigurationManager.updateNetwork(network, networkConfiguration.getId());

        new Verifications() {
            {
                // networkConfigurationDtoAssembler.transform(network);
                // times = 1;
                // networkConfigurationRepository.findOne(networkConfiguration.getId());
                // times = 1;
            }
        };
    }


    @Test
    public void addIpv4RangeShouldDelegateToNetworkConfigurationDtoAssemblerTransformIpRange(@Mocked IpRange ipRange) {
        final long networkId = 100L;
        NetworkConfiguration networkConfiguration = new NetworkConfiguration();
        networkConfiguration.setName("network name");
        networkConfiguration.setGateway("72.162.0.1");
        networkConfiguration.setSubnet("255.255.255.0");
        networkConfiguration.setStatic(true);
        networkConfiguration.setType("PUBLIC_LAN");
        networkConfiguration.setVlanId(90);
        networkConfiguration.setPrimaryDns("72.162.0.1");
        networkConfiguration.setSecondaryDns("72.162.0.2");
        IpAddressRange ipAddressRange = new IpAddressRange();
        Set<IpAddressRange> ipAddressRanges = new HashSet<IpAddressRange>();
        ipAddressRange.setStartIpAddress(Inet4ConverterValidator.convertIpStringToLong("72.162.0.10"));
        ipAddressRange.setEndIpAddress(Inet4ConverterValidator.convertIpStringToLong("72.162.0.19"));
        ipAddressRanges.add(ipAddressRange);
        networkConfiguration.setIpAddressRanges(ipAddressRanges);
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationRepository.findOne(networkId);
                result = networkConfiguration;
                networkConfigurationDtoAssembler.transform(ipRange, networkConfiguration);
                result = ipAddressRange;
            }
        };

        networkConfigurationManager.addIpv4Range(networkId, ipRange);

        /*
         * new Verifications() { { networkConfigurationRepository.findOne(anyLong); times = 1; networkConfigurationDtoAssembler.transform(ipRange, networkConfiguration); times = 1;
         * } };
         */
    }


    @Test
    public void addIpv4RangeShouldDelegateToNetworkConfigurationDaoSaveOrUpdate(@Mocked IpRange ipRange) {
        final long networkId = 100L;
        NetworkConfiguration networkConfiguration = new NetworkConfiguration();
        networkConfiguration.setName("network name");
        networkConfiguration.setGateway("72.162.0.1");
        networkConfiguration.setSubnet("255.255.255.0");
        networkConfiguration.setStatic(true);
        networkConfiguration.setType("PUBLIC_LAN");
        networkConfiguration.setVlanId(90);
        networkConfiguration.setPrimaryDns("72.162.0.1");
        networkConfiguration.setSecondaryDns("72.162.0.2");
        IpAddressRange ipAddressRange = new IpAddressRange();
        Set<IpAddressRange> ipAddressRanges = new HashSet<IpAddressRange>();
        ipAddressRange.setStartIpAddress(Inet4ConverterValidator.convertIpStringToLong("72.162.0.10"));
        ipAddressRange.setEndIpAddress(Inet4ConverterValidator.convertIpStringToLong("72.162.0.19"));
        ipAddressRanges.add(ipAddressRange);
        networkConfiguration.setIpAddressRanges(ipAddressRanges);
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationRepository.findOne(networkId);
                result = networkConfiguration;
                networkConfigurationDtoAssembler.transform(ipRange, networkConfiguration);
                result = ipAddressRange;
            }
        };

        networkConfigurationManager.addIpv4Range(networkId, ipRange);

        new Verifications() {
            {
                // networkConfigurationRepository.findOne(networkId);
                // times = 1;
                // networkConfigurationDtoAssembler.transform(ipRange, networkConfiguration);
                // times = 1;
                ipAddressRangeRepository.save(ipAddressRange);
                times = 1;
            }
        };
    }


    @Test
    public void deleteIpv4RangeShouldDelegateToNetworkConfigurationDaoFindById(@Mocked IpRange ipRange, @Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        final long networkId = 100L;
        final long rangeId = 200L;
        new Expectations(networkConfigurationManager) {
            {
                ipAddressRangeRepository.findOne(rangeId);
                result = ipAddressRange;
            }
        };

        networkConfigurationManager.deleteIpv4Range(networkId, rangeId);
    }


    @Test
    public void deleteIpv4RangeShouldDelegateToIpAddressPoolManagerFindByAssignedStateAndRangeId(@Mocked IpRange ipRange, @Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        final long networkId = 100L;
        final long rangeId = 200L;
        List<IpAddressPoolEntry> entry = null;
        new Expectations(networkConfigurationManager) {
            {
                ipAddressRangeRepository.findOne(rangeId);
                result = ipAddressRange;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                result = entry;
            }
        };

        networkConfigurationManager.deleteIpv4Range(networkId, rangeId);

        new Verifications() {
            // {
            // ipAddressRangeRepository.findOne(rangeId);
            // times = 1;
            // ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
            // times = 1;
            // }
        };
    }


    @Test
    public void deleteIpv4RangeShouldDelegateToIpAddressPoolManagerFindByReservedStateAndRangeId(@Mocked IpRange ipRange, @Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        final long networkId = 100L;
        final long rangeId = 200L;
        List<IpAddressPoolEntry> entry = null;
        List<IpAddressPoolEntry> entry2 = null;
        new Expectations(networkConfigurationManager) {
            {
                ipAddressRangeRepository.findOne(rangeId);
                result = ipAddressRange;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                result = entry;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
                result = entry2;
            }
        };

        networkConfigurationManager.deleteIpv4Range(networkId, rangeId);

        new Verifications() {
            // {
            // ipAddressRangeRepository.findOne(rangeId);
            // times = 1;
            // ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
            // times = 1;
            // ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
            // times = 1;
            // }
        };
    }


    @Test(expected = RuntimeCoreException.class)
    public void deleteIpv4RangeShouldThrowRuntimeCoreExceptionWhenPoolListIsNotEmpty(@Mocked IpRange ipRange, @Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        final long networkId = 100L;
        final long rangeId = 200L;
        List<IpAddressPoolEntry> entry = new ArrayList<IpAddressPoolEntry>();
        entry.add(new IpAddressPoolEntry());
        List<IpAddressPoolEntry> entry2 = null;
        new Expectations(networkConfigurationManager) {
            {
                ipAddressRangeRepository.findOne(rangeId);
                result = ipAddressRange;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                result = entry;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
                result = entry2;
                CollectionUtils.isEmpty(entry);
                result = false;
            }
        };

        networkConfigurationManager.deleteIpv4Range(networkId, rangeId);

        new Verifications() {
            {
                ipAddressRangeRepository.findOne(rangeId);
                times = 1;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                times = 1;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
                times = 1;
            }
        };
    }


    @Test
    public void deleteIpv4RangeShouldDelegateToNetworkConfigurationDaoDeleteById(@Mocked IpRange ipRange, @Mocked NetworkConfiguration networkConfiguration, @Mocked IpAddressRange ipAddressRange) {
        final long networkId = 100L;
        final long rangeId = 200L;
        List<IpAddressPoolEntry> entry = null;
        List<IpAddressPoolEntry> entry2 = null;
        new Expectations(networkConfigurationManager) {
            {
                ipAddressRangeRepository.findOne(rangeId);
                result = ipAddressRange;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                result = entry;
                ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
                result = entry2;
                ipAddressRangeRepository.delete(rangeId);
            }
        };

        networkConfigurationManager.deleteIpv4Range(networkId, rangeId);

        new Verifications() {
            // {
            // ipAddressRangeRepository.findOne(rangeId);
            // times = 1;
            // ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.ASSIGNED.toString());
            // times = 1;
            // ipAddressPoolManager.findByStateAndRangeId(ipAddressRange.getId(), IpAddressState.RESERVED.toString());
            // times = 1;
            // ipAddressRangeRepository.delete(rangeId);
            // times = 1;
            // }
        };
    }


    @Test
    public void getAllNetworksByTypeShouldDelegateToNetworkConfigurationDaoFindAllNetworkConfigurations() {
        final String networkType = "STORAGE_ISCSI_SAN";
        List<NetworkConfiguration> entry = new ArrayList<NetworkConfiguration>();
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationRepository.findByType(networkType);
                result = entry;
            }
        };

        List<Network> networkList = networkConfigurationManager.getAllNetworksByType(networkType);
        assertNotNull(networkList);

        // new Verifications() {
        // {
        // networkConfigurationRepository.findByType(networkType);
        // times = 1;
        // }
        // };
    }


    @Test
    public void getAllNetworksByTypeShouldNotDelegateToNetworkConfigurationDaoFindAllNetworkConfigurationsWhenNetworkTypeIsNullOrEmpty() {
        final String networkType = "";
        new Expectations(networkConfigurationManager) {
            {
            }
        };

        networkConfigurationManager.getAllNetworksByType(networkType);

        new Verifications() {
            {
                networkConfigurationRepository.findByType(networkType);
                times = 0;
            }
        };
    }


    @Test
    public void getAllNetworksByTypeShouldDelegateToNetworkConfigurationDtoAssemblerTransformNetworkConfiguration() {
        final String networkType = "STORAGE_ISCSI_SAN";
        final List<NetworkConfiguration> entry = new ArrayList<NetworkConfiguration>();
        final NetworkConfiguration networkConfiguration = new NetworkConfiguration();
        entry.add(networkConfiguration);
        final Network network = new Network();

        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationRepository.findByType(networkType);
                result = entry;
                networkConfigurationDtoAssembler.transform(networkConfiguration);
                result = network;
            }
        };

        networkConfigurationManager.getAllNetworksByType(networkType);
        /*
         * new Verifications() { { networkConfigurationRepository.findByType(networkType); times = 1; networkConfigurationDtoAssembler.transform(networkConfiguration); times = 1; }
         * };
         */
    }


    @Test
    public void getAllNetworksByTypeShouldDelegateToNetworkConfigurationDtoAssemblerTransformNetworkConfigurationMultipleEntries() {
        final String networkType = "STORAGE_ISCSI_SAN";
        final List<NetworkConfiguration> entries = new ArrayList<NetworkConfiguration>();
        final NetworkConfiguration networkConfiguration1 = new NetworkConfiguration();
        networkConfiguration1.setName("netConfig1");
        entries.add(networkConfiguration1);
        final NetworkConfiguration networkConfiguration2 = new NetworkConfiguration();
        networkConfiguration2.setName("netConfig2");
        entries.add(networkConfiguration2);
        final Network network1 = new Network();
        network1.setName("net1");
        final Network network2 = new Network();
        network1.setName("net2");
        new Expectations(networkConfigurationManager) {
            {
                networkConfigurationRepository.findByType(networkType);
                result = entries;
                networkConfigurationDtoAssembler.transform(networkConfiguration1);
                returns(network1, network2);
            }
        };

        final List<Network> result = networkConfigurationManager.getAllNetworksByType(networkType);
        assertEquals(2, result.size());
        assertTrue(network1.equals(result.get(0)));
        assertTrue(network2.equals(result.get(1)));

        new Verifications() {
            {
                // networkConfigurationRepository.findByType(networkType);
                // times = 1;
                // networkConfigurationDtoAssembler.transform(networkConfiguration1);
                // times = 2;
            }
        };
    }


    @Test
    public void getNetworksByNameShouldDelegateToNetworkConfigurationDaoFindByName() {

        final String networkName = "Management Network";

        networkConfigurationManager.getNetworkByName(networkName);

        new Verifications() {
            {
                networkConfigurationRepository.findByName(networkName);
                times = 1;
            }
        };
    }


    @Test
    public void getNetworksByNameShouldReturnNullWhenDaoReturnNull() {

        final String networkName = "Management Network";

        new Expectations() {
            {
                networkConfigurationRepository.findByName(networkName);
                result = null;
            }
        };

        final Network actual = networkConfigurationManager.getNetworkByName(networkName);
        assertThat(actual, is(nullValue()));
    }


    @Test
    public void getNetworksByNameShouldDelegateToDtoWhenDaoReturnNotNull(@Mocked NetworkConfiguration networkConfiguration, @Mocked Network network) {

        final String networkName = "Management Network";

        new Expectations() {
            {
                networkConfigurationRepository.findByName(networkName);
                result = networkConfiguration;
                networkConfigurationDtoAssembler.transform(networkConfiguration);
                result = network;
            }
        };

        final Network actual = networkConfigurationManager.getNetworkByName(networkName);
        assertThat(actual, is(network));
    }
}
