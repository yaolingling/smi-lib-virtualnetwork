/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.virtualnetwork.entity.IpAddressRange;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;

public class ValidatedStaticNetworkProperties {

    private static final Logger logger = LoggerFactory.getLogger(ValidatedStaticNetworkProperties.class);

    private ValidatedInet4Address gateway;
    private String dnsSuffix;
    private ValidatedInet4Address primaryDns;
    private ValidatedInet4Address secondaryDns;
    private ValidatedInet4SubnetMask subnetMask;
    private TreeSet<ValidatedInet4Range> ipRanges;
    private String networkType;


    /**
     * @return the gateway
     */
    public ValidatedInet4Address getGateway() {
        return gateway;
    }


    /**
     * @return the dnsSuffix
     */
    public String getDnsSuffix() {
        return dnsSuffix;
    }


    /**
     * @return the primaryDns
     */
    public ValidatedInet4Address getPrimaryDns() {
        return primaryDns;
    }


    /**
     * @return the secondaryDns
     */
    public ValidatedInet4Address getSecondaryDns() {
        return secondaryDns;
    }


    /**
     * @return the subnetMask
     */
    public ValidatedInet4SubnetMask getSubnetMask() {
        return subnetMask;
    }


    /**
     * @return the ipRanges
     */
    public SortedSet<ValidatedInet4Range> getIpRanges() {
        return Collections.unmodifiableSortedSet(ipRanges);
    }


    /**
     * @return the networkType
     */
    public String getNetworkType() {
        return networkType;
    }


    public ValidatedStaticNetworkProperties(NetworkConfiguration networkProperties, String networkType) {
        this.networkType = networkType;
        if (null == networkProperties) {
            logger.error("StaticNetworkProperties validation failure: null networkProperties passed.");
            return;
        }
        validateAndSetDnsSuffix(networkProperties.getDnsSuffix());
        validateAndSetGateway(networkProperties.getGateway());
        validateSubnet(networkProperties.getSubnet());
        createIpRanges(networkProperties.getIpAddressRanges());
        validateAndSetPrimaryDns(networkProperties.getPrimaryDns());
        validateAndSetSecondaryDns(networkProperties.getSecondaryDns());
        if (networkProperties.getSubnet() != null && networkProperties.getSubnet().length() > 0) {
            subnetMask = new ValidatedInet4SubnetMask(networkProperties.getSubnet());
            validateGatewayAndIpRangesAreWithinSameSubnet();
        }
    }


    private void validateAndSetSecondaryDns(String secondaryDnsString) {
        if (!StringUtils.hasLength(secondaryDnsString)) {
            return;
        }
        try {
            secondaryDns = new ValidatedInet4Address(secondaryDnsString);
        } catch (BusinessValidationException validationException) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for Secondary DNS.");
            BusinessValidationException businessValidationException = new BusinessValidationException(validationException);
            businessValidationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_IP_FOR_SECONDARY_DNS);
            throw businessValidationException;
        }
        if (secondaryDns.equals(primaryDns)) {
            logger.debug("StaticNetworkProperties validation failure: primary and secondary DNS are the same.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_SAME_PRIMARY_SECONDARY_DNS);
        }
    }


    private void validateAndSetPrimaryDns(String primaryDnsString) {
        if (!StringUtils.hasLength(primaryDnsString)) {
            return;
        }
        try {
            primaryDns = new ValidatedInet4Address(primaryDnsString);
        } catch (BusinessValidationException validationException) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for Primary DNS.");
            BusinessValidationException businessValidationException = new BusinessValidationException(validationException);
            businessValidationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_IP_FOR_PRIMARY_DNS);
            throw businessValidationException;
        }
    }


    private void validateAndSetGateway(String gatewayString) {
        if (!StringUtils.hasLength(gatewayString)) {
            logger.debug("StaticNetworkProperties validation failure: Null or empty string passed for gateway.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_EMPTY_GATEWAY);
        }
        try {
            gateway = new ValidatedInet4Address(gatewayString);
        } catch (BusinessValidationException exception) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for gateway.");
            BusinessValidationException validationException = new BusinessValidationException(exception);
            validationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_IP_FOR_GATEWAY);
            throw validationException;
        }
    }


    private void validateSubnet(String subnetString) {
        if (!StringUtils.hasLength(subnetString)) {
            logger.error("StaticNetworkProperties validation failure: Invalid IP passed for subnet.");
            BusinessValidationException businessValidationException = new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALID_SUBNET_IP);
            businessValidationException.addAttribute(subnetString);
            throw businessValidationException;
        }
        try {
            new ValidatedInet4Address(subnetString);
        } catch (BusinessValidationException exception) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for subnet.");
            BusinessValidationException validationException = new BusinessValidationException(exception);
            validationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_SUBNET_IP);
            throw validationException;
        }

    }


    private void validateAndSetDnsSuffix(String dnsSuffixString) {
        if (!StringUtils.hasLength(dnsSuffixString)) {
            return;
        }
        if (Validator.isDnsNameValid(dnsSuffixString)) {
            dnsSuffix = dnsSuffixString;
        } else {
            logger.debug("StaticNetworkProperties validation failure: invalid DNS Suffix.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALID_DNS_SUFFIX);
        }
    }


    private void validateGatewayAndIpRangesAreWithinSameSubnet() {
        List<ValidatedInet4Address> addressesToCheck = new ArrayList<>();
        if (null != gateway) {
            addressesToCheck.add(gateway);
        }
        for (ValidatedInet4Range range : ipRanges) {
            addressesToCheck.add(range.getStartAddress());
            addressesToCheck.add(range.getEndAddress());
        }
        if (!ipAddressesOnSameSubnet(addressesToCheck, subnetMask)) {
            logger.debug("StaticNetworkProperties validation failure: Gateway and IP ranges not in same subnet");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_GATEWAY_SUBNET_IPRANGE_NOT_SAME);
        }
    }


    private boolean ipAddressesOnSameSubnet(List<ValidatedInet4Address> addressesToCheck, ValidatedInet4SubnetMask mask) {
        boolean result = true;
        ValidatedInet4Address comparisonBase = null;
        for (ValidatedInet4Address address : addressesToCheck) {
            if (null == comparisonBase) {
                comparisonBase = address.getNetworkPrefix(mask);
                continue;
            }
            if (!comparisonBase.equals(address.getNetworkPrefix(mask))) {
                result = false;
                break;
            }
        }
        return result;
    }


    private void createIpRanges(Set<IpAddressRange> ipRanges) {
        if (null == ipRanges || ipRanges.isEmpty()) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP Ranges");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALID_IPRANGE);
        }
        Comparator<ValidatedInet4Range> comparator = new Inet4RangeComparator();
        TreeSet<ValidatedInet4Range> sortedRanges = new TreeSet<>(comparator);
        for (IpAddressRange range : ipRanges) {
            validateIpRange(range);
            sortedRanges.add(new ValidatedInet4Range(Inet4ConverterValidator.convertIpValueToString(range.getStartIpAddress()), Inet4ConverterValidator.convertIpValueToString(range.getEndIpAddress())));
        }
        this.ipRanges = sortedRanges;
        validateRangesDoNotOverlap();
    }


    private void validateIpRange(IpAddressRange range) {
        try {
            new ValidatedInet4Address(range.getStartIpAddress());
        } catch (BusinessValidationException e) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for IPRange start IPAddress.");
            BusinessValidationException businessValidationException = new BusinessValidationException(e);
            businessValidationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_IPRANGE_START_IP);
            throw businessValidationException;
        }
        try {
            new ValidatedInet4Address(range.getEndIpAddress());
        } catch (BusinessValidationException e) {
            logger.debug("StaticNetworkProperties validation failure: Invalid IP passed for IPRange end IPAddress.");
            BusinessValidationException businessValidationException = new BusinessValidationException(e);
            businessValidationException.setErrorCode(ErrorCodeEnum.NETWORKCONF_INVALID_IPRANGE_END_IP);
            throw businessValidationException;
        }

        if (range.getStartIpAddress() == 0 || range.getEndIpAddress() == 0) {
            logger.debug("StaticNetworkProperties validation failure: IP range is invalid.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALID_IPRANGE);
        }

        if (range.getStartIpAddress() > range.getEndIpAddress()) {
            logger.debug("StaticNetworkProperties validation failure: IP range is invalid. Start address greater than end address.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALID_IPRANGE);
        }

    }


    private void validateRangesDoNotOverlap() {
        ValidatedInet4Range previous = null;
        for (ValidatedInet4Range current : ipRanges) {
            if (null != previous && previous.overlaps(current)) {
                logger.debug("StaticNetworkProperties validation failure: IP ranges overlap.");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_OVERLAP_IPRANGE);
            }
            previous = current;
        }
    }

}
