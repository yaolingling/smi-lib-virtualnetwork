/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;

/**
 * The Class ValidatedInet4Address.
 */
public class ValidatedInet4Address implements Comparable<ValidatedInet4Address> {
    private static final Logger logger = LoggerFactory.getLogger(ValidatedInet4Address.class);
    private long address;


    /**
     * Instantiates a new validated inet 4 address.
     *
     * @param addressString the address string
     * @throws BusinessValidationException the business validation exception
     */
    public ValidatedInet4Address(String addressString) {
        address = Inet4ConverterValidator.convertIpStringToLong(addressString);
    }


    /**
     * Instantiates a new validated inet 4 address.
     *
     * @param value the value
     * @throws BusinessValidationException the business validation exception
     */
    public ValidatedInet4Address(long value) {
        if (Inet4ConverterValidator.isValidIpAddress(value)) {
            address = value;
        } else {
            logger.debug("Invalid IP Address; entered IPAddress format is not correct");
            BusinessValidationException businessValidationException = new BusinessValidationException(ErrorCodeEnum.INVALID_IPADDRESS_CODE);
            businessValidationException.addAttribute(String.valueOf(value));
            throw businessValidationException;
        }
    }


    /**
     * Gets the address.
     *
     * @return the address
     */
    public long getAddress() {
        return address;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Inet4ConverterValidator.convertIpValueToString(address);
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ValidatedInet4Address o) {
        if (this.getAddress() < o.getAddress()) {
            return -1;
        } else if (this.getAddress() == o.getAddress()) {
            return 0;
        }
        return 1;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ValidatedInet4Address) {
            ValidatedInet4Address other = (ValidatedInet4Address) o;
            return other.getAddress() == this.address;
        }
        return false;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        Long hasher = Long.valueOf(address);
        return hasher.hashCode();
    }


    /**
     * Gets the network prefix.
     *
     * @param mask the mask
     * @return the network prefix
     */
    public ValidatedInet4Address getNetworkPrefix(ValidatedInet4SubnetMask mask) {
        long prefix = address & mask.getValue();
        return new ValidatedInet4Address(prefix);
    }


    /**
     * Gets the host part.
     *
     * @param mask the mask
     * @return the host part
     */
    public ValidatedInet4Address getHostPart(ValidatedInet4SubnetMask mask) {
        long host = address & (~mask.getValue());
        return new ValidatedInet4Address(host);
    }
}
