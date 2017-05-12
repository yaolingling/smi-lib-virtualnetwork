/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;

/**
 * The Class ValidatedInet4Range.
 */
public class ValidatedInet4Range implements Comparable<ValidatedInet4Range> {
    private static final Logger logger = LoggerFactory.getLogger(ValidatedInet4Range.class);
    private ValidatedInet4Address startAddress;
    private ValidatedInet4Address endAddress;
    
    
    /**
     * Instantiates a new validated inet 4 range.
     *
     * @param startingIp the starting ip
     * @param endingIp the ending ip
     */
    public ValidatedInet4Range(String startingIp, String endingIp) {
        this(new ValidatedInet4Address(startingIp), new ValidatedInet4Address(endingIp));
    }

    
    /**
     * Instantiates a new validated inet 4 range.
     *
     * @param address1 the address 1
     * @param address2 the address 2
     */
    public ValidatedInet4Range(ValidatedInet4Address address1, ValidatedInet4Address address2) {
        startAddress = address1;
        endAddress = address2;
        validateRangeEndpointOrder();
    }


    /**
     * Instantiates a new validated inet 4 range.
     *
     * @param validatedInet4Address the validated inet 4 address
     * @param sizeOfRange the size of range
     */
    public ValidatedInet4Range(ValidatedInet4Address validatedInet4Address, int sizeOfRange) {
        if (0 >= sizeOfRange) {
            logger.warn("Validation failure: range size must be positive.");
            throw new BusinessValidationException(ErrorCodeEnum.INVALID_IPRANGE_SIZE_CODE);
        }
        startAddress = validatedInet4Address;
        endAddress = new ValidatedInet4Address(validatedInet4Address.getAddress() + (sizeOfRange - 1));
        validateRangeEndpointOrder();
    }

    /**
     * Sets the start address.
     *
     * @param startAddress the new start address
     */
    public void setStartAddress(ValidatedInet4Address startAddress) {
        this.startAddress = startAddress;
    }


    /**
     * Sets the end address.
     *
     * @param endAddress the new end address
     */
    public void setEndAddress(ValidatedInet4Address endAddress) {
        this.endAddress = endAddress;
    }
    
    
    private void validateRangeEndpointOrder() {
        if (endAddress.compareTo(startAddress) < 0) {
            logger.warn("Validation failure: Ending IP < Starting IP");
            throw new BusinessValidationException(ErrorCodeEnum.INVALID_IPRANGE_CODE);
        }
    }


    /**
     * Overlaps.
     *
     * @param other the other
     * @return true, if successful
     */
    public boolean overlaps(ValidatedInet4Range other) {
        return this.startAddress.getAddress() <= other.endAddress.getAddress() && other.startAddress.getAddress() <= this.endAddress.getAddress();
    }


    /**
     * Checks if is entirely within subnet.
     *
     * @param mask the mask
     * @return true, if is entirely within subnet
     */
    public boolean isEntirelyWithinSubnet(ValidatedInet4SubnetMask mask) {
        return startAddress.getNetworkPrefix(mask).equals(endAddress.getNetworkPrefix(mask));
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return startAddress.hashCode() ^ endAddress.hashCode();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValidatedInet4Range) {
            ValidatedInet4Range other = (ValidatedInet4Range) obj;
            return other.getStartAddress() == this.startAddress && other.getEndAddress() == this.endAddress;
        }
        return false;
    }


    /**
     * Gets the end address.
     *
     * @return the end address
     */
    public ValidatedInet4Address getEndAddress() {
        return endAddress;
    }


    /**
     * Gets the start address.
     *
     * @return the start address
     */
    public ValidatedInet4Address getStartAddress() {
        return startAddress;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ValidatedInet4Range o2) {
        int result = startAddress.compareTo(o2.startAddress);
        if (0 == result) {
            result = endAddress.compareTo(o2.endAddress);
        }
        return result;
    }


    /**
     * Gets the address strings.
     *
     * @return the address strings
     */
    public List<String> getAddressStrings() {
        List<String> result = new ArrayList<>();
        for (long addressValue = startAddress.getAddress(); addressValue <= endAddress.getAddress(); addressValue++) {
            result.add(Inet4ConverterValidator.convertIpValueToString(addressValue));
        }
        return result;
    }
}
