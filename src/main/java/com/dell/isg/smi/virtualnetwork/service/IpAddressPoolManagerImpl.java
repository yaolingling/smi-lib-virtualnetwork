/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;
import com.dell.isg.smi.commons.utilities.PaginationUtils;
import com.dell.isg.smi.commons.utilities.datetime.DateTimeUtils;
import com.dell.isg.smi.commons.utilities.model.PagedResult;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.exception.BadRequestException;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;
import com.dell.isg.smi.virtualnetwork.exception.NotFoundException;
import com.dell.isg.smi.virtualnetwork.model.AssignIpPoolAddresses;
import com.dell.isg.smi.virtualnetwork.model.ExportIpPoolData;
import com.dell.isg.smi.virtualnetwork.model.IpAddressPoolEntry;
import com.dell.isg.smi.virtualnetwork.model.IpAddressState;
import com.dell.isg.smi.virtualnetwork.model.ReserveIpPoolAddressesRequest;
import com.dell.isg.smi.virtualnetwork.repository.BaseEntityRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressPoolEntryRepository;
import com.dell.isg.smi.virtualnetwork.repository.IpAddressRangeRepository;
import com.dell.isg.smi.virtualnetwork.repository.NetworkConfigurationRepository;
import com.dell.isg.smi.virtualnetwork.validation.IPv4StringComparator;
import com.dell.isg.smi.virtualnetwork.validation.Inet4ConverterValidator;

/**
 * The Class IpAddressPoolManagerImpl.
 */
@Component
public class IpAddressPoolManagerImpl implements IpAddressPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(IpAddressPoolManagerImpl.class);
    private static final String defaultState = "ALL";
    private static final String GMT = "GMT";

    @Autowired
    IpAddressRangeRepository ipAddressRangeRepository;

    @Autowired
    IpAddressPoolEntryRepository ipAddressPoolEntryRepository;

    @Autowired
    BaseEntityRepository baseEntityRepository;

    @Autowired
    NetworkConfigurationRepository networkConfigurationRepository;

    @Autowired
    private IpAddressPoolDtoAssembler ipAddressPoolDtoAssembler;

    // Singleton instance of this class.
    private static IpAddressPoolManager instance;


    /**
     * Get the singleton instance of this class, creating it if necessary.
     *
     * @return single instance of IpAddressPoolManagerImpl
     */
    public static synchronized IpAddressPoolManager getInstance() {
        if (instance == null)
            instance = new IpAddressPoolManagerImpl();
        return instance;
    }


    @Override
    public PagedResult getIpv4AddressPoolEntries(long networkId, String state, String usageId, int offset, int limit) {
        logger.trace("Entering getIpv4AddressPoolEntries() method with networkId: {} and state: {}", networkId, state);
        if (limit == 0) {
            // TODO: throw an exception
            return null;
        }
        int page = offset / limit;
        PageRequest pageRequest = new PageRequest(page, limit);

        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryList = new ArrayList<>();

        if (!StringUtils.isEmpty(usageId)) {
            if (!defaultState.equalsIgnoreCase(state)) {
                Page<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryPage = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdAndIpAddressUsageIdOrderByIpAddressAsc(state, networkId, usageId, pageRequest);
                ipAddressPoolEntryList = ipAddressPoolEntryPage.getContent();
            } else {
                for (IpAddressState ipAddressState : IpAddressState.values()) {
                    Page<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryPage = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdAndIpAddressUsageIdOrderByIpAddressAsc(ipAddressState.value(), networkId, usageId, pageRequest);
                    List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolList = ipAddressPoolEntryPage.getContent();
                    ipAddressPoolEntryList.addAll(ipAddressPoolList);
                }
            }
        } else {
            if (!defaultState.equalsIgnoreCase(state)) {
                Page<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryPage = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(state, networkId, pageRequest);
                ipAddressPoolEntryList = ipAddressPoolEntryPage.getContent();
            } else {
                for (IpAddressState ipAddressState : IpAddressState.values()) {
                    Page<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryPage = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(ipAddressState.value(), networkId, pageRequest);
                    List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolList = ipAddressPoolEntryPage.getContent();
                    ipAddressPoolEntryList.addAll(ipAddressPoolList);
                }
            }
        }

        if (CollectionUtils.isEmpty(ipAddressPoolEntryList)) {
            return PaginationUtils.ZERO_RESULT;
        }
        List<IpAddressPoolEntry> ipPoolList = new ArrayList<>();
        ipAddressPoolEntryList.stream().forEach(i -> ipPoolList.add(ipAddressPoolDtoAssembler.transform(i)));
        int total = 0;
        if (!defaultState.equalsIgnoreCase(state)) {
            total += ipAddressPoolEntryRepository.countByIpAddressStateAndIpAddressRangeNetworkConfigurationId(state, networkId);
        } else {
            for (IpAddressState ipAddressState : IpAddressState.values()) {
                total += ipAddressPoolEntryRepository.countByIpAddressStateAndIpAddressRangeNetworkConfigurationId(ipAddressState.value(), networkId);
            }
        }

        PagedResult pagedResult = PaginationUtils.paginate(ipPoolList, total, offset, limit);
        logger.trace("Exiting getIpv4AddressPoolEntries() method with networkId: {} and state: {}", networkId, state);
        return pagedResult;
    }


    @Override
    public synchronized Set<String> reserveIpv4AddressPoolAddresses(long networkId, ReserveIpPoolAddressesRequest reserveIpPoolAddressesRequest, int reservationCalendarUnit, int reservationNumberOfUnits) {
        logger.trace("Entering reserveIpv4AddressPoolAddresses() method with networkId: {}", networkId);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addresses = getAvailablePoolEntries(networkId);

        if ((null == addresses) || CollectionUtils.isEmpty(addresses)) {
            logger.trace("Unable to reserve requested number of addresses as there are no ip addresses in pool.");
            throw new RuntimeCoreException(ErrorCodeEnum.IPPOOL_NO_IPADDRESS_AVAILABLE);
        }

        if (addresses.size() < reserveIpPoolAddressesRequest.getQuantityRequested()) {
            logger.trace("Unable to reserve requested number of addresses as there are no enough ip addresses in pool.");
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(ErrorCodeEnum.IPPOOL_ENOUGH_IPADRESS_NOT_AVAILABLE);
            runtimeCoreException.addAttribute("networkId:" + networkId);
            runtimeCoreException.addAttribute("quantityRequested:" + reserveIpPoolAddressesRequest.getQuantityRequested());
            throw runtimeCoreException;
        }

        Date expiryDate = createFutureDate(reservationCalendarUnit, reservationNumberOfUnits);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addressesToReserve = addresses.subList(0, (int) reserveIpPoolAddressesRequest.getQuantityRequested());
        reserveIPAddressCollection(addressesToReserve, expiryDate, reserveIpPoolAddressesRequest.getUsageId());

        ipAddressPoolEntryRepository.save(addressesToReserve);

        Set<String> result = setOfStringsFromAddressPoolEntryCollection(addressesToReserve);
        logger.trace("Exiting reserveIpv4AddressPoolAddresses() method with networkId: {}", networkId);
        return result;
    }


    @Override
    public void assignIpv4AddressPoolAddresses(long networkId, AssignIpPoolAddresses assignIpPoolAddresses) {
        logger.trace("Entering assignIpv4AddressPoolAddresses() with networkId: {}", networkId);
        if (assignIpPoolAddresses != null) {
            List<String> requestedIpAddresses = assignIpPoolAddresses.getIpAddresses();
            if (CollectionUtils.isEmpty(requestedIpAddresses)) {
                BusinessValidationException bve = new BusinessValidationException();
                bve.setErrorCode(ErrorCodeEnum.ENUM_INVALID_DATA);
                throw bve;
            }

            try {
                HashSet<Long> addressList = new HashSet<>();
                for (String ipAddress : requestedIpAddresses) {
                    addressList.add(Inet4ConverterValidator.convertIpStringToLong(ipAddress));
                }

                List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> targetIpEntries = ipAddressPoolEntryRepository.findByIpAddressRangeNetworkConfigurationIdAndIpAddressIn(networkId, addressList);
                String usageId = assignIpPoolAddresses.getUsageId();
                for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address : targetIpEntries) {
                    if (!isAssignableTo(address, usageId)) {
                        logger.error("Attempt to assign IP addresses to usageId: {} failed", usageId);
                        RuntimeCoreException rce = new RuntimeCoreException();
                        rce.setErrorCode(ErrorCodeEnum.IPPOOL_IPADDRESS_ASSIGN_FAILED);
                        throw rce;
                    }
                    address.setIPAddressState(IpAddressState.ASSIGNED);
                    address.setIpAddressUsageId(usageId);
                    address.setExpiryDate(null);
                    addressList.remove(address.getIpAddress());
                }

                ipAddressPoolEntryRepository.save(targetIpEntries);
            } catch (RuntimeCoreException rce) {
                logger.error("Exception occurred in assignIPAddressesTransactionally", rce.toString());
                rce.setErrorCode(ErrorCodeEnum.IPPOOL_IPADDRESS_ASSIGN_FAILED);
                throw rce;
            } catch (Exception e) {
                logger.error("Exception occurred in assignIPAddressesTransactionally", e);
                throw e;
            }
        }
        logger.trace("Exiting assignIpv4AddressPoolAddresses() with networkId: {}", networkId);
    }


    @Override
    public void releaseSpecificIpv4Address(long networkId, String ipAddress) {
        logger.trace("Entering releaseSpecificIpv4Address() with networkId: {} and ipAddress: {}", networkId, ipAddress);
        Long ipToRelease = Inet4ConverterValidator.convertIpStringToLong(ipAddress);
        com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry poolEntry = ipAddressPoolEntryRepository.findByIpAddress(ipToRelease);
        String attributeForError = "ipAddress:" + ipAddress;
        if (poolEntry == null) {
            logger.trace("Invalid Ip Address passed");
            NotFoundException notFoundException = new NotFoundException();
            notFoundException.setErrorCode(ErrorCodeEnum.ENUM_NOT_FOUND_ERROR);
            notFoundException.addAttribute(attributeForError);
            throw notFoundException;
        }
        if (IpAddressState.AVAILABLE.toString().equals(poolEntry.getIpAddressState())) {
            logger.trace("Ip Address already released");
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorCode(ErrorCodeEnum.ENUM_INVALID_DATA);
            badRequestException.addAttribute(attributeForError);
            throw badRequestException;
        }
        releaseAddress(poolEntry);
        try {
            ipAddressPoolEntryRepository.save(poolEntry);
        } catch (Exception e) {
            RuntimeCoreException exception = new RuntimeCoreException(e);
            exception.setErrorCode(ErrorCodeEnum.IPPOOL_NO_IPADDRESS_AVAILABLE);
            exception.addAttribute(attributeForError);
            throw exception;
        }
        logger.trace("Exiting releaseSpecificIpv4Address() with networkId: {} and ipAddress: {}", networkId, ipAddress);
    }


    @Override
    public void releaseAllIpv4Addresses(long networkId, String usageId) {
        logger.trace("Entering releaseAllIpv4Addresses() with networkId: {} and usageId: {}", networkId, usageId);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> assignedIpAddressPoolEntryList = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageId(IpAddressState.ASSIGNED.value(), usageId);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> reservedIpAddressPoolEntryList = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageId(IpAddressState.RESERVED.value(), usageId);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryList = new ArrayList<>();
        assignedIpAddressPoolEntryList.stream().forEach(i -> ipAddressPoolEntryList.add(i));
        reservedIpAddressPoolEntryList.stream().forEach(i -> ipAddressPoolEntryList.add(i));
        if (CollectionUtils.isEmpty(ipAddressPoolEntryList)) {
            BusinessValidationException businessValidationException = new BusinessValidationException(ErrorCodeEnum.ENUM_INVALID_DATA);
            businessValidationException.addAttribute("usageId:" + usageId);
            throw businessValidationException;
        }
        for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry ipAddressPoolEntry : ipAddressPoolEntryList) {
            releaseAddress(ipAddressPoolEntry);
        }
        ipAddressPoolEntryRepository.save(ipAddressPoolEntryList);
        logger.trace("Exiting releaseAllIpv4Addresses() with networkId: {} and usageId: {}", networkId, usageId);
    }


    @Override
    public boolean isIpAddressStateValid(String ipAddressState) {
        try {
            IpAddressState.valueOf(ipAddressState);
            return true;
        } catch (Exception iae) {
            logger.warn("No match for IpAddressState", iae);
            return false;
        }
    }


    @Override
    public boolean isIpAddressValid(String ipAddress) {
        if (!StringUtils.hasLength(ipAddress)) {
            RuntimeCoreException.handleRuntimeCoreException(ErrorCodeEnum.ENUM_INVALID_DATA, String.valueOf(ipAddress));
        }
        return true;
    }


    @Override
    public boolean isUsageIdValid(String usageId) {
        if (usageId == null) {
            RuntimeCoreException.handleRuntimeCoreException(ErrorCodeEnum.ENUM_INVALID_DATA, "usageId");
        }
        return true;
    }


    @Override
    public boolean isReserveQuantityRequestedIsValid(ReserveIpPoolAddressesRequest reserveIpPoolAddressesRequest) {
        if ((reserveIpPoolAddressesRequest != null) && (reserveIpPoolAddressesRequest.getQuantityRequested() > 0)) {
            return true;
        }
        return false;
    }


    @Override
    public synchronized Set<String> getIPAddressesAssignedToGUID(String usageGUID, long networkConfigurationId) {
        Set<String> result = null;
        logger.debug(String.format("Getting into method : getIPAddressesAssignedToGUID(%s, %s)", usageGUID, networkConfigurationId));
        try {
            result = getIPAddressesAssignedToGUIDTransactionally(usageGUID, networkConfigurationId);
        } catch (RuntimeCoreException rce) {
            logger.error("Exception occurred in getIPAddressesAssignedToGUID", rce);
            throw rce;
        }
        return result;
    }


    @Override
    public synchronized Set<String> getIPAddressesReservedToGUID(String usageGUID, long networkConfigurationId) {
        Set<String> result = null;
        logger.debug(String.format("Getting into method : getIPAddressesReservedToGUID(%s, %s)", usageGUID, networkConfigurationId));
        try {
            result = getIPAddressesReservedToGUIDTransactionally(usageGUID, networkConfigurationId);
        } catch (RuntimeCoreException rce) {
            logger.error("Exception occurred in getIPAddressesReservedToGUID", rce);
            throw rce;
        }
        return result;
    }


    @Override
    public List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> findByStateAndRangeId(long ipRangeId, String state) {
        logger.trace("Entering IpAddressPoolManager findByStateAndRangeId() method");
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntryList = null;
        try {
            ipAddressPoolEntryList = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeIdOrderByIpAddress(state, ipRangeId);
        } catch (RuntimeCoreException rce) {
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in findByStateAndRangeId", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        }
        logger.trace("Exiting IpAddressPoolManager findByStateAndRangeId() method");
        return ipAddressPoolEntryList;
    }


    @Override
    public List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> findByNetworkConfigurationId(long networkConfigurationId) {
        logger.debug("Getting into method networkConfigurationId() in DAO Manager");
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> result = null;
        try {
            result = ipAddressPoolEntryRepository.findByIpAddressRangeNetworkConfigurationIdOrderByIpAddress(networkConfigurationId);
        } catch (RuntimeCoreException rce) {
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in findByNetworkConfigurationID", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        }
        logger.debug("Exiting from method networkConfigurationId() in DAO Manager");
        return result;
    }


    @Transactional
    @Override
    public synchronized void releaseIPAddressesByUsageId(String usageId) {
        logger.debug("Getting into releaseIPAddressesByUsageId.");

        try {
            // Delete temporary IPs attached to usageGUID
            List<IpAddressRange> ipAddressRangeList = ipAddressRangeRepository.findByTemporaryIsTrue();
            if (!CollectionUtils.isEmpty(ipAddressRangeList)) {
                for (IpAddressRange ipAddressRange : ipAddressRangeList) {
                    Set<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressPoolEntrySet = ipAddressRange.getIpAddressPool();
                    for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry ipAddressPoolEntry : ipAddressPoolEntrySet) {
                        if (usageId != ipAddressPoolEntry.getIpAddressUsageId()) {
                            ipAddressPoolEntryRepository.delete(ipAddressPoolEntry);
                        }
                    }
                }
            }

            // Delete temporary ranges with no IPs attached (i.e. from previous delete)
            ipAddressRangeRepository.deleteWhereNoPoolEntriesExist();

            // Mark non-temporary IPs as available
            ipAddressPoolEntryRepository.updateStateToAvailabeForUsageId(usageId);
        } catch (RuntimeCoreException rce) {
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in  releaseIPAddressesByUsageId", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        }
        logger.debug("Exiting from method releaseIPAddressesByUsageId.");
    }


    @Override
    public NetworkConfiguration findAssignedNetworkConfigurationForIpAddress(String ipAddress) {
        NetworkConfiguration networkConfiguration = null;
        logger.debug("Getting into findAssignedNetworkConfigurationForIpAddress.");
        try {
            networkConfiguration = networkConfigurationRepository.findByIpAddressRangesIpAddressPoolIpAddress(Inet4ConverterValidator.convertIpStringToLong(ipAddress));
        } catch (RuntimeCoreException rce) {
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in  findAssignedNetworkConfigurationForIpAddress", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.NETWORKCONF_SYSTEM_ERROR);
            throw runtimeCoreException;
        }
        return networkConfiguration;
    }


    private Date createFutureDate(int futureUnitType, int numberOfUnitsInFuture) {
        TimeZone gmt = TimeZone.getTimeZone(GMT);
        Calendar gmtCalendar = Calendar.getInstance(gmt);
        gmtCalendar.add(futureUnitType, numberOfUnitsInFuture);
        return gmtCalendar.getTime();
    }


    private List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> getAvailablePoolEntries(long networkConfigurationId) {
        logger.trace("Entering getAvailablePoolEntries() method with networkId: {}", networkConfigurationId);
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addresses = null;
        try {
            addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(IpAddressState.AVAILABLE.value(), networkConfigurationId);
        } catch (RuntimeCoreException rce) {
            logger.error("RuntimeCoreException occurred in getAvailablePoolEntries", rce);
            throw rce;
        } catch (Exception e) {
            logger.debug("Exception occurred in getAvailablePoolEntries", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.IPPOOL_IPADDRESS_ASSIGN_FAILED);
            throw runtimeCoreException;
        } finally {
            long numberOfAddresses = 0;
            if (null != addresses) {
                numberOfAddresses = addresses.size();
            }
            logger.trace("Exiting method  getAvailablePoolEntries() with {} addresses", numberOfAddresses);
        }
        return addresses;
    }


    private Set<String> getIPAddressesAssignedToGUIDTransactionally(String usageGUID, long networkConfigurationId) {
        Set<String> result = null;
        logger.debug(String.format("Getting into method : getIPAddressesAssignedToGUIDTransactionally(%s, %s)", usageGUID, networkConfigurationId));
        try {
            List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addresses;
            if (0 != networkConfigurationId) {
                addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageIdAndIpAddressRangeNetworkConfigurationId(IpAddressState.ASSIGNED.value(), usageGUID, networkConfigurationId);
            } else {
                addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageId(IpAddressState.ASSIGNED.value(), usageGUID);
            }
            result = this.setOfStringsFromAddressPoolEntryCollection(addresses);
        } catch (RuntimeCoreException rce) {
            logger.error("AsmException occurred in getIPAddressesAssignedToGUIDTransactionally", rce);
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in getIPAddressesAssignedToGUIDTransactionally", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.IPPOOL_IPADDRESS_ASSIGN_FAILED);
            throw runtimeCoreException;
        }
        return result;
    }


    private Set<String> getIPAddressesReservedToGUIDTransactionally(String usageGUID, long networkConfigurationId) {
        logger.debug(String.format("Getting into method : getIPAddressesReservedToGUIDTransactionally(%s, %s)", usageGUID, networkConfigurationId));
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addresses;
        if (0 != networkConfigurationId) {
            addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageIdAndIpAddressRangeNetworkConfigurationId(IpAddressState.RESERVED.value(), usageGUID, networkConfigurationId);
        } else {
            addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageId(IpAddressState.RESERVED.value(), usageGUID);
        }
        return this.setOfStringsFromAddressPoolEntryCollection(addresses);
    }


    private boolean isAssignableTo(com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address, String usageId) {
        IpAddressState addressState = IpAddressState.valueOf(address.getIpAddressState());
        return IpAddressState.AVAILABLE == addressState || IpAddressState.RESERVED == addressState || (IpAddressState.ASSIGNED == addressState && address.getIpAddressUsageId() == usageId);
    }


    private void reserveIpAddress(com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address, Date expiryDate, String usageId) {
        logger.trace("Entering reserveIpAddress method with IpAddress: {} and ExpiryDate: {}", address.getIpAddress(), expiryDate.toString());
        address.setIPAddressState(IpAddressState.RESERVED);
        address.setExpiryDate(expiryDate);
        address.setIpAddressUsageId(usageId);
        logger.trace("Exiting reserveIpAddress method");
    }


    private void reserveIPAddressCollection(List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addressesToReserve, Date expiryDate, String usageId) {
        for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry entry : addressesToReserve) {
            if (entry == null) {
                logger.trace("Method:reserveIPAddressCollection-IPAddressPoolEntry is null");
                return;
            } else {
                reserveIpAddress(entry, expiryDate, usageId);
            }
        }
    }


    private Set<String> setOfStringsFromAddressPoolEntryCollection(List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addressesToReserve) {
        SortedSet<String> result = new TreeSet<>(new IPv4StringComparator());
        if (addressesToReserve != null) {
            for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address : addressesToReserve) {
                result.add(Inet4ConverterValidator.convertIpValueToString(address.getIpAddress()));
            }
        }
        return result;
    }


    @Override
    public synchronized void convertReservedToAssigned(String usageId, String ipAddress) {

        logger.debug(String.format("Getting into method : convertReservedToAssigned(%s)", usageId));
        try {
            List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> addresses;
            addresses = ipAddressPoolEntryRepository.findByIpAddressStateAndIpAddressUsageId(IpAddressState.RESERVED.value(), usageId);
            if (addresses == null) {
                logger.error("Unable to reserve requested number of addresses");
                throw new RuntimeCoreException(ErrorCodeEnum.IPPOOL_NO_IPADDRESS_AVAILABLE);
            }

            for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address : addresses) {
                String sip = Inet4ConverterValidator.convertIpValueToString(address.getIpAddress());
                if (sip.equals(ipAddress)) {
                    address.setIPAddressState(IpAddressState.ASSIGNED);
                    address.setExpiryDate(null);
                    break;
                }
            }

            ipAddressPoolEntryRepository.save(addresses);
        } catch (RuntimeCoreException rce) {
            logger.error("Exception occurred in convertReservedToAssigned", rce);
            throw rce;
        } catch (Exception e) {
            logger.error("Exception occurred in convertReservedToAssigned", e);
            RuntimeCoreException runtimeCoreException = new RuntimeCoreException(e);
            runtimeCoreException.setErrorCode(ErrorCodeEnum.IPPOOL_IPADDRESS_ASSIGN_FAILED);
            throw runtimeCoreException;
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean doesNetworkRecordExist(long networkId) {
        NetworkConfiguration networkConfig = networkConfigurationRepository.findOne(networkId);
        if (networkConfig == null) {
            return false;
        }
        return true;
    }


    private void releaseAddress(com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry address) {
        address.setIpAddressState(IpAddressState.AVAILABLE.toString());
        address.setIpAddressUsageId(null);
        address.setExpiryDate(null);
    }


    @Override
    public List<ExportIpPoolData> exportIpPoolsInUse(long networkId) {
        logger.trace("Entering exportIpPoolsInUse() with networkId: {}", networkId);
        List<ExportIpPoolData> exportIpAddressesList = new ArrayList<>();
        // uses AddressStateNot
        List<com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry> ipAddressesInUse = ipAddressPoolEntryRepository.findByIpAddressStateNotAndIpAddressRangeNetworkConfigurationIdOrderByIpAddressAsc(IpAddressState.AVAILABLE.value(), networkId);
        for (com.dell.isg.smi.virtualnetwork.entity.IpAddressPoolEntry ipPoolEntry : ipAddressesInUse) {
            ExportIpPoolData ipAddressData = new ExportIpPoolData();
            if (ipPoolEntry.getExpiryDate() != null) {
                ipAddressData.setExpiryDate(DateTimeUtils.getIsoDateString(ipPoolEntry.getExpiryDate()));
            }
            ipAddressData.setId(ipPoolEntry.getId());
            ipAddressData.setIpAddress(Inet4ConverterValidator.convertIpValueToString(ipPoolEntry.getIpAddress()));
            ipAddressData.setIpAddressState(IpAddressState.valueOf(ipPoolEntry.getIpAddressState()));
            ipAddressData.setLastModifiedBy(!StringUtils.hasLength(ipPoolEntry.getUpdatedBy()) ? ipPoolEntry.getCreatedBy() : ipPoolEntry.getUpdatedBy());
            ipAddressData.setLastModifiedDate(ipPoolEntry.getUpdatedTime().toString());
            ipAddressData.setUsageId(ipPoolEntry.getIpAddressUsageId());
            exportIpAddressesList.add(ipAddressData);
        }
        logger.trace("Exiting exportIpPoolsInUse()");
        return exportIpAddressesList;
    }
}
