/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.utilities.model.PagedResult;
import com.dell.isg.smi.commons.utilities.PaginationUtils;
import com.dell.isg.smi.virtualnetwork.common.utilities.UserHelper;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.exception.BadRequestException;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;
import com.dell.isg.smi.virtualnetwork.exception.NotFoundException;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.model.IpRange;
import com.dell.isg.smi.virtualnetwork.model.Network;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressPoolEntryRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressRangeRepository;
import com.dell.isg.smi.virtualnetwork.repository.NetworkConfigurationRepository;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;
import com.dell.isg.smi.virtualnetwork.validation.ValidatedInet4Range;
import com.dell.isg.smi.virtualnetwork.validation.ValidatedNetworkConfiguration;

/**
 * The Class NetworkConfigurationManagerImpl.
 */
@Component
public class NetworkConfigurationManagerImpl implements NetworkConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(NetworkConfigurationManagerImpl.class);
    public static final int MAX_IP_COUNT = 1000;

    @Autowired
    NetworkConfigurationRepository networkConfigurationRepository;

    @Autowired
    IpAddressPoolEntryRepository ipAddressPoolEntryRepository;

    @Autowired
    IpAddressRangeRepository ipAddressRangeRepository;

    @Autowired
    private IpAddressPoolManager ipAddressPoolManager;

    @Autowired
    private NetworkConfigurationDtoAssembler networkConfigurationDtoAssembler;

    public static final String IP_ADDRESS_STATE_AVAILABLE = "AVAILABLE";


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getAllNetworks(int, int)
     */
    @Override
    public PagedResult getAllNetworks(int offset, int limit) {
        logger.trace("Entering NetworkConfigurationManager getAllNetworks");
        List<NetworkConfiguration> networkConfigurationList;

        if (limit > 0) {
            int page = offset / limit;
            PageRequest pageRequest = new PageRequest(page, limit);
            Page<NetworkConfiguration> networkConfigurationPage = networkConfigurationRepository.findAll(pageRequest);
            networkConfigurationList = networkConfigurationPage.getContent();
        } else {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(ErrorCodeEnum.ENUM_BAD_REQUEST_ERROR);
            badRequestException.addAttribute("limit");
            throw badRequestException;
        }

        if (CollectionUtils.isEmpty(networkConfigurationList)) {
            return PaginationUtils.ZERO_RESULT;
        }

        List<Network> networksList = new ArrayList<>();
        for (NetworkConfiguration networkConfiguration : networkConfigurationList) {
            Network network = transformNetworkConfigAndSetIpRangeProperties(networkConfiguration);
            networksList.add(network);
        }
        long total = networkConfigurationRepository.count();

        final PagedResult pagedResult = PaginationUtils.paginate(networksList, total, offset, limit);
        logger.trace("Exiting NetworkConfigurationManager getAllNetworks");
        return pagedResult;
    }

    
    private Network transformNetworkConfigAndSetIpRangeProperties(NetworkConfiguration networkConfiguration) {
        logger.trace("Entering transformNetworkConfigAndSetIpRangeParameters()");
        Network network = networkConfigurationDtoAssembler.transform(networkConfiguration);
        if (null != network && null != network.getStaticIpv4NetworkConfiguration()) {
            List<IpRange> ipRangeList = network.getStaticIpv4NetworkConfiguration().getIpRange();
            for (IpRange ipRange : ipRangeList) {
                if (null == ipRange) {
                    continue;
                }
                long rangeId = ipRange.getId();
                List<IpAddressPoolEntry> ipAddresses = ipAddressPoolEntryRepository.findByIpAddressRangeId(rangeId);
                int ipAddessesInUse = 0;
                int ipAddressesFree = 0;
                for (IpAddressPoolEntry ipAddress : ipAddresses) {
                    if (null != ipAddress) {
                        if (IP_ADDRESS_STATE_AVAILABLE.equalsIgnoreCase(ipAddress.getIpAddressState())) {
                            ipAddressesFree++;
                        } else {
                            ipAddessesInUse++;
                        }
                    }
                }
                ipRange.setIpAddressesInUse(ipAddessesInUse);
                ipRange.setIpAddressesFree(ipAddressesFree);
            }
        }
        logger.trace("Exiting transformNetworkConfigAndSetIpRangeParameters()");
        return network;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getNetwork(java.lang.String)
     */
    @Override
    public Network getNetwork(String name) {
        logger.trace("Entering NetworkConfigurationManager getNetwork(String name)");
        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findByName(name);
        if (networkConfiguration == null) {
            NotFoundException notFoundException = new NotFoundException(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(name);
            throw notFoundException;
        }
        Network network = transformNetworkConfigAndSetIpRangeProperties(networkConfiguration);
        logger.trace("Exiting NetworkConfigurationManager getNetwork(String name)");
        return network;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getNetworkByNamePaged(java.lang.String)
     */
    @Override
    public PagedResult getNetworkByNamePaged(String name) {
        logger.trace("Entering NetworkConfigurationManager getNetworkByNamePaged(String name)");
        int total = 0;
        int limit = 0;

        NetworkConfiguration networkConfiguration = null;
        try {
            networkConfiguration = networkConfigurationRepository.findByName(name);
        } catch (Exception e) {
            logger.trace("network not found.", e);
        }

        List<Network> networksList = new ArrayList<>();
        if (null != networkConfiguration) {
            Network network = transformNetworkConfigAndSetIpRangeProperties(networkConfiguration);
            networksList.add(network);
            total = 1;
            limit = 1;
        }
        final PagedResult pagedResult = PaginationUtils.paginate(networksList, total, 0, limit);
        logger.trace("Exiting NetworkConfigurationManager getNetworkByNamePaged(String name)");
        return pagedResult;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getNetwork(long)
     */
    @Override
    public Network getNetwork(long networkId) {
        logger.trace("Entering NetworkConfigurationManager getNetwork");
        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findOne(networkId);
        if (networkConfiguration == null) {
            NotFoundException notFoundException = new NotFoundException(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(String.valueOf(networkId));
            throw notFoundException;
        }
        Network network = transformNetworkConfigAndSetIpRangeProperties(networkConfiguration);
        logger.trace("Exiting NetworkConfigurationManager getNetwork");
        return network;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#createNetwork(com.dell.isg.smi.virtualnetwork.model.Network)
     */
    @Override
    @Transactional
    public long createNetwork(Network network) {
        logger.trace("Entered createNetwork {}", network.toString());
        long networkConfigurationId = 0L;

        try {
            NetworkConfiguration networkConfiguration = networkConfigurationDtoAssembler.transform(network);
            validateNetworkConfiguration(networkConfiguration);
            validateIpCountForStaticNetwork(networkConfiguration);

            // Ignore ID for IP Range in case create request has a ID (Guid)
            if (networkConfiguration.isStatic() && networkConfiguration.getIpAddressRanges() != null) {
                for (IpAddressRange ipRange : networkConfiguration.getIpAddressRanges()) {
                    if (null != ipRange) {
                        ipRange.setId(0);
                        // creating IPAddresspool and setting it to IPAddressRange
                        Set<IpAddressPoolEntry> ipPool = IpAddressPoolManagerUtility.expandIpAddresses(ipRange);
                        ipRange.setIpAddressPool(ipPool);
                    }
                }
            }

            List<IpAddressRange> ipAddressRangesList = null;
            try {
                ipAddressRangesList = ipAddressRangeRepository.findOverlappingIpRanges(networkConfiguration.getIpAddressRanges());
            } catch (Exception e) {
                logger.error("Exception occured during findOverlappingIPRanges", e);
                RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
                runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
                throw runtimeCoreException;
            }

            if ( !CollectionUtils.isEmpty(ipAddressRangesList)) {
                logger.error("IP ranges overlap with IP ranges of other Network Configurations");
                BadRequestException badRequestException = new BadRequestException();
                badRequestException.setErrorCode(ErrorCodeEnum.NETWORKCONF_NETWORK_OVERLAP);
                throw badRequestException;
            }

            if (networkConfiguration.isStatic() && networkConfiguration.getIpAddressRanges() != null) {
                Set<IpAddressRange> ipRanges = networkConfiguration.getIpAddressRanges();
                if (ipRanges != null) {
                    for (IpAddressRange IPRange : ipRanges) {
                        Set<IpAddressPoolEntry> newIpPoolSet = IpAddressPoolManagerUtility.expandIpAddresses(IPRange);
                        IPRange.setIpAddressPool(newIpPoolSet);
                    }
                }
            }

            String userName = UserHelper.getUserName();
            networkConfiguration.setCreatedBy(userName);

            networkConfigurationRepository.save(networkConfiguration);
            networkConfigurationId = networkConfiguration.getId();

            logger.info("Newly created Network Configuration Id is {}", networkConfigurationId);
        } catch (RuntimeCoreException rce) {
            logger.error("RuntimeCoreException occured during createNetworkConfiguration", rce);
            throw rce;
        } catch (Exception e) {
            logger.error("Exception encountered while performing create opertaion. " + e.getMessage());
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        } finally {
            logger.trace("Exiting NetworkConfigurationManager createNetwork ");
        }
        return networkConfigurationId;
    }


    // TODO: refactor this method!
    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#updateNetwork(com.dell.isg.smi.virtualnetwork.model.Network, long)
     * 
     * This method needs to be rewritten to pull the existing network, and modify it with the transform based on partial data
     */
    // present in the network model parameter. In its current implementation it doesn't presently support partial updates.
    @Override
    @Transactional
    public void updateNetwork(Network network, long networkId) {
        logger.trace("Entered updateNetwork() {}", networkId);

        try {
            if ((null != network) && (null != network.getStaticIpv4NetworkConfiguration()) && !CollectionUtils.isEmpty(network.getStaticIpv4NetworkConfiguration().getIpRange())) {
                // we are not allowing the ranges to be edited by this method
                BadRequestException badRequestException = new BadRequestException();
                badRequestException.setErrorCode(ErrorCodeEnum.NETWORKCONF_UPDATE_IPRANGE_INCLUDED);
                badRequestException.addAttribute("network");
                throw badRequestException;
            }

            NetworkConfiguration networkConfiguration = networkConfigurationDtoAssembler.transform(network);
            networkConfiguration.setId(networkId);
            NetworkConfiguration oldNetworkConfiguration = networkConfigurationRepository.findOne(networkId);
            if (oldNetworkConfiguration == null) {
                NotFoundException notFoundException = new NotFoundException();
                notFoundException.setErrorCode(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
                notFoundException.addAttribute(String.valueOf(networkId));
                throw notFoundException;
            }

            networkConfiguration.setIpAddressRanges(oldNetworkConfiguration.getIpAddressRanges());
            validateNetworkConfiguration(networkConfiguration);
            validateIpCountForStaticNetwork(networkConfiguration);

            // check if IPAddress type (DHCP <-> Static) is changed. If yes, throw validation exception
            if ( (networkConfiguration.isStatic() && !oldNetworkConfiguration.isStatic()) || (!networkConfiguration.isStatic() && oldNetworkConfiguration.isStatic()) ) {
                logger.error("Network Configuration's IP-Address type is changed");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_IPADDRESS_TYPE_CHANGED);
            }

            networkConfiguration.setCreatedBy(oldNetworkConfiguration.getCreatedBy());
            networkConfiguration.setCreatedTime(oldNetworkConfiguration.getCreatedTime());
            String userName = UserHelper.getUserName();
            networkConfiguration.setUpdatedBy(userName);

            networkConfigurationRepository.save(networkConfiguration);
            logger.info("Updated Network Configuration for networkId {}", networkId);
        } catch (RuntimeCoreException rce) {
            logger.error("RuntimeCoreException occured during updateNetworkConfiguration", rce);
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occured during updateNetworkConfiguration", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        } finally {
            logger.trace("Exiting updateNetwork()");
        }
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#deleteNetwork(com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration, long)
     */
    @Override
    @Transactional
    public void deleteNetwork(NetworkConfiguration networkConfiguration, long networkId) {
        logger.trace("Entered deleteNetwork() {}", networkId);
        try {
            // Check whether the deleted IP Ranges are IN-USE
            Set<IpAddressRange> setDeletedIpRanges = networkConfiguration.getIpAddressRanges();
            for (IpAddressRange deletedIPAddressRange : setDeletedIpRanges) {
                List<IpAddressPoolEntry> assignedIPAddressPoolEntryList = ipAddressPoolManager.findByStateAndRangeId(deletedIPAddressRange.getId(), IpAddressState.ASSIGNED.toString());
                List<IpAddressPoolEntry> reservedIPAddressPoolEntryList = ipAddressPoolManager.findByStateAndRangeId(deletedIPAddressRange.getId(), IpAddressState.RESERVED.toString());

                if (!CollectionUtils.isEmpty(assignedIPAddressPoolEntryList) || !CollectionUtils.isEmpty(reservedIPAddressPoolEntryList)) {
                    // Throw IP Range in use exception
                    logger.error("IP Range is in-use. Undo any modifications to in-use IP Ranges");
                    throw new RuntimeCoreException(ErrorCodeEnum.NETWORKCONF_IP_IN_USE);
                }
            }

            networkConfigurationRepository.delete(networkId);
            logger.info("Deleted Network Configuration with networkId {}", networkId);
        } catch (RuntimeCoreException rse) {
            logger.error("RuntimeCoreException occured during deleteNetworkConfiguration", rse);
            throw rse;
        } catch (Exception e) {
            logger.error("Exception occured during deleteNetworkConfiguration", e);
            String strErrorCause = e.getMessage();
            logger.error("Exception occured while performing deleteNetworkConfiguration operation: {}", strErrorCause);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        } finally {
            logger.trace("Exiting deleteNetworkConfiguration()");
        }
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#isNetworkIdValid(long)
     */
    @Override
    public boolean isNetworkIdValid(long networkId) {
        if (networkId <= 0) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(ErrorCodeEnum.ENUM_INVALID_DATA);
            badRequestException.addAttribute("networkId");
            throw badRequestException;
        }
        return true;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#isIpRangeValid(com.dell.isg.smi.virtualnetwork.model.IpRange)
     */
    @Override
    public boolean isIpRangeValid(IpRange ipRange) {
        if (ipRange != null) {
            return true;
        }
        return false;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#addIpv4Range(long, com.dell.isg.smi.virtualnetwork.model.IpRange)
     */
    @Override
    public long addIpv4Range(long networkId, IpRange ipRange) {
        logger.trace("entered addIpv4Range() {}", networkId);
        long ipRangeId = 0;
        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findOne(networkId);
        if (networkConfiguration == null) {
            NotFoundException notFoundException = new NotFoundException();
            notFoundException.setErrorCode(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(String.valueOf(networkId));
            throw notFoundException;
        }
        IpAddressRange ipAddressRange = networkConfigurationDtoAssembler.transform(ipRange, networkConfiguration);
        Set<IpAddressRange> existingIpAddressRange = networkConfiguration.getIpAddressRanges();
        existingIpAddressRange.add(ipAddressRange);
        validateNetworkConfiguration(networkConfiguration);
        validateIpCountForStaticNetwork(networkConfiguration);

        Set<IpAddressPoolEntry> newIpPoolSet = IpAddressPoolManagerUtility.expandIpAddresses(ipAddressRange);
        ipAddressRange.setIpAddressPool(newIpPoolSet);

        try {
            ipAddressRangeRepository.save(ipAddressRange);
            ipRangeId = ipAddressRange.getId();
        } catch (Exception e) {
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IPRANGE_SAVE_OR_UPDATE_FAILED);
            throw runtimeCoreException;
        } finally {
            logger.trace("exiting addIpv4Range() with ipRangeId{}", ipRangeId);
        }

        return ipRangeId;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#isIpRangeIdValid(long)
     */
    @Override
    public boolean isIpRangeIdValid(long rangeId) {
        if (rangeId <= 0) {
            RuntimeCoreException.handleRuntimeCoreException(ErrorCodeEnum.ENUM_INVALID_DATA, String.valueOf(rangeId));
        }
        return true;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#updateIpv4Range(long, long, com.dell.isg.smi.virtualnetwork.model.IpRange)
     */
    @Override
    public void updateIpv4Range(long networkId, long rangeId, IpRange ipRange) {
        logger.trace("entered updateIpv4Range() rangeId:{}", rangeId);

        // get the old range from the database
        IpAddressRange ipAddressRange = null;
        try {
            ipAddressRange = ipAddressRangeRepository.findOne(rangeId);
        } catch (Exception e) {
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IPRANGE_RETRIEVE_FAILED);
            runtimeCoreException.addAttribute(String.valueOf(rangeId));
            throw runtimeCoreException;
        }

        if (ipAddressRange == null) {
            logger.error("No record found for range ID {}", rangeId);
            NotFoundException notFoundException = new NotFoundException(ErrorCodeEnum.NETWORKCONF_IPRANGE_NOT_FOUND);
            notFoundException.addAttribute(String.valueOf(rangeId));
            throw notFoundException;
        }

        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findOne(networkId);
        if (networkConfiguration == null) {
            NotFoundException notFoundException = new NotFoundException(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(String.valueOf(networkId));
            throw notFoundException;
        }

        Set<IpAddressPoolEntry> ipAddressPoolEntrySet = ipAddressRange.getIpAddressPool();

        // validate the new range
        ValidatedInet4Range validatedRange = new ValidatedInet4Range(ipRange.getStartingIp(), ipRange.getEndingIp());

        // use get a string list of the new range
        List<String> addressStrings = validatedRange.getAddressStrings();

        try {
            ipAddressRange.setStartIpAddress(Inet4ConverterValidator.convertIpStringToLong(ipRange.getStartingIp()));
            ipAddressRange.setEndIpAddress(Inet4ConverterValidator.convertIpStringToLong(ipRange.getEndingIp()));

            // IMPORTANT: expandIpAddresses creates new entities for all of the ip addresses in the range
            Set<IpAddressPoolEntry> newIpPoolSet = IpAddressPoolManagerUtility.expandIpAddresses(ipAddressRange);

            // loop through the existing entries to remove or add them to the new list
            for (IpAddressPoolEntry poolEntry : ipAddressPoolEntrySet) {
                if (null == poolEntry) {
                    continue;
                }

                // get the entry's ip address as a string for comparison with our list.
                String poolEntryIpAddress = Inet4ConverterValidator.convertIpValueToString(poolEntry.getIpAddress());
                logger.debug("processing existing IP address {}", poolEntryIpAddress);

                // if the ip from the old range is in the new range we leave it alone by removing it's newed up version, and adding back the original entity
                if (addressStrings.contains(poolEntryIpAddress)) {
                    logger.debug("the existing ip address is in the new range");

                    // match existing entity with "newed up" entity by the ip address
                    Set<IpAddressPoolEntry> newEntryMatches = newIpPoolSet.stream().filter(newIpEntry -> newIpEntry.getIpAddress() == poolEntry.getIpAddress()).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(newEntryMatches)) {
                        logger.debug("there are entries matched. match count {}", newEntryMatches.size());
                        if (newEntryMatches.size() == 1) {
                            // get the matched entry
                            Iterator<IpAddressPoolEntry> iterator = newEntryMatches.iterator();
                            IpAddressPoolEntry matchedIpAddressPoolEntry = iterator.next();

                            // verify the ip addresses match
                            if (matchedIpAddressPoolEntry.getIpAddress() == poolEntry.getIpAddress()) {
                                logger.debug("ip address matches a newed up entry. Removing the newed up entry from the list and adding the existing one");
                                // remove the newed up version of the entity from the list
                                newIpPoolSet.remove(matchedIpAddressPoolEntry);
                                // add in the pre-existing entitiy to the list
                                newIpPoolSet.add(poolEntry);
                            } else {
                                logger.warn("filtered entry does not match the exptected ip address {}", poolEntryIpAddress);
                            }

                        } else {
                            logger.warn("Expected existing IP address {} to match only one entry during the lamda filter but more than one match was found.", poolEntryIpAddress);
                        }
                    } else {
                        logger.warn("Expected existing IP address {} to match a new entry during the lamda filter but no match was found.", poolEntryIpAddress);
                    }
                } else if (!"AVAILABLE".equals(poolEntry.getIpAddressState())) {
                    logger.error("IP address {} cannot be removed because it is already in use.", poolEntryIpAddress);
                    // we cannot delete ip addresses from the range that are being used
                    RuntimeCoreException runtimeCoreException = new RuntimeCoreException(ErrorCodeEnum.NETWORKCONF_IPRANGE_UPDATE_FAILED_IP_IN_USE);
                    runtimeCoreException.addAttribute(poolEntryIpAddress);
                    throw runtimeCoreException;
                } else {
                    logger.debug("IP Address {} will be deleted", poolEntryIpAddress);
                }
            }

            // clear out the old list and add the transformed data into it
            ipAddressPoolEntrySet.clear();
            ipAddressPoolEntrySet.addAll(newIpPoolSet);

            // set address entry set on the range object we originally retrieved
            ipAddressRange.setIpAddressPool(ipAddressPoolEntrySet);

            // validate everything
            validateNetworkConfiguration(networkConfiguration);
            validateIpCountForStaticNetwork(networkConfiguration);

            // save
            ipAddressRangeRepository.save(ipAddressRange);
            logger.info("updated ipAddressRange successfully");
        } catch (RuntimeCoreException e) {
            logger.error("RuntimeCoreException occured in updateIpv4Range ", e);
            throw e;
        } catch (Exception e) {
            logger.error("failed to update IPV4 range.", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IPRANGE_SAVE_OR_UPDATE_FAILED);
            throw runtimeCoreException;
        } finally {
            logger.trace("exiting updateIpv4Range()");
        }
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#deleteIpv4Range(long, long)
     */
    @Override
    public void deleteIpv4Range(long networkId, long rangeId) {
        logger.trace("entered deleteIpv4Range() rangeId:{}", rangeId);
        IpAddressRange ipAddressRangeToBeDeleted = ipAddressRangeRepository.findOne(rangeId);
        if (ipAddressRangeToBeDeleted == null) {
            NotFoundException notFoundException = new NotFoundException();
            notFoundException.setErrorCode(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(String.valueOf(rangeId));
            throw notFoundException;
        }
        List<IpAddressPoolEntry> assignedIPAddressPoolEntryList = ipAddressPoolManager.findByStateAndRangeId(ipAddressRangeToBeDeleted.getId(), IpAddressState.ASSIGNED.toString());
        List<IpAddressPoolEntry> reservedIPAddressPoolEntryList = ipAddressPoolManager.findByStateAndRangeId(ipAddressRangeToBeDeleted.getId(), IpAddressState.RESERVED.toString());

        if (!CollectionUtils.isEmpty(assignedIPAddressPoolEntryList) || !CollectionUtils.isEmpty(reservedIPAddressPoolEntryList)) {
            logger.error("IP Range is in-use. Undo any modifications to in-use IP Ranges");
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IP_IN_USE);
            throw badRequestException;
        }

        try {
            ipAddressRangeRepository.delete(rangeId);
        } catch (Exception e) {
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IPRANGE_DELETE_FAILED);
            throw runtimeCoreException;
        } finally {
            logger.trace("exiting deleteIpv4Range()");
        }
    }


    private void validateNetworkConfiguration(NetworkConfiguration networkConfiguration) {
        logger.trace("Entering NetworkConfigManager -> validateNetworkConfiguration");
        try {
            new ValidatedNetworkConfiguration(networkConfiguration);
        } catch (BusinessValidationException bve) {
            logger.error("Validation Exception : Invalid Network Configuration specified: ", bve);
            throw bve;
        } catch (Exception e) {
            logger.error("Exception: Invalid Network Configuration specified: ", e);
            BusinessValidationException businessValidationException = new BusinessValidationException(e);
            businessValidationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_NETWORK_CONF);
            throw businessValidationException;
        }
        logger.trace("Exiting NetworkConfigManager -> validateNetworkConfiguration");
    }


    private void validateIpCountForStaticNetwork(NetworkConfiguration networkConfiguration) {
        int totalIPCount = 0;
        if (!networkConfiguration.isStatic())
            return;

        for (IpAddressRange ipRange : networkConfiguration.getIpAddressRanges()) {
            long ipDiffCount = (ipRange.getEndIpAddress() - ipRange.getStartIpAddress()) + 1;

            if (ipDiffCount > 0)
                totalIPCount = totalIPCount + (int) ipDiffCount;

            if (totalIPCount > MAX_IP_COUNT) {
                BadRequestException badRequestException = new BadRequestException();
                badRequestException.setErrorCode(ErrorCodeEnum.NETWORKCONF_IP_RANGE_LIMIT_EXCEEDED);
                badRequestException.addAttribute(String.valueOf(MAX_IP_COUNT));
                throw badRequestException;
            }
        }
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getAllNetworksByType(java.lang.String)
     */
    @Override
    public List<Network> getAllNetworksByType(String networkType) {
        final List<Network> networks = new ArrayList<>();

        if (StringUtils.hasLength(networkType)) {
            List<NetworkConfiguration> networkConfigurations = networkConfigurationRepository.findByType(networkType);
            if (!CollectionUtils.isEmpty(networkConfigurations)) {
                networkConfigurations.stream().forEach(i -> networks.add(networkConfigurationDtoAssembler.transform(i)));
            }
        }
        return networks;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getNetworkByName(java.lang.String)
     */
    @Override
    public Network getNetworkByName(String networkName) {
        logger.trace("Entering NetworkConfigurationManager getNetworksByName({})", networkName);
        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findByName(networkName);
        if (networkConfiguration == null) {
            return null;
        }
        Network network = networkConfigurationDtoAssembler.transform(networkConfiguration);
        logger.trace("Exiting NetworkConfigurationManager getNetworksByName({})", networkName);
        return network;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#isVlandIdGreaterThan4000(int)
     */
    @Override
    public boolean isVlandIdGreaterThan4000(int vlanId) {
        logger.trace("Entering isVlandIdGreaterThan4000({})", vlanId);
        if (vlanId > 4000) {
            return true;
        }
        logger.trace("Exiting isVlandIdGreaterThan4000({})", vlanId);
        return false;
    }


    /* (non-Javadoc)
     * @see com.dell.isg.smi.virtualnetwork.service.NetworkConfigurationManager#getNetworkConfiguration(long)
     */
    @Override
    public NetworkConfiguration getNetworkConfiguration(long networkId) {
        logger.trace("Entering getNetwork() method with networkId: {}", networkId);
        NetworkConfiguration networkConfiguration = networkConfigurationRepository.findOne(networkId);
        if (null == networkConfiguration) {
            NotFoundException notFoundException = new NotFoundException();
            notFoundException.setErrorCode(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(String.valueOf(networkId));
            throw notFoundException;
        }
        logger.trace("Exiting getNetwork()");
        return networkConfiguration;
    }
}