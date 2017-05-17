/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;

/**
 * The Class ValidatedInet4SubnetMask.
 */
public class ValidatedInet4SubnetMask {
    long maskValue;


    /**
     * Instantiates a new validated inet 4 subnet mask.
     *
     * @param mask the mask
     * @throws BusinessValidationException the business validation exception
     */
    public ValidatedInet4SubnetMask(String mask) throws BusinessValidationException {
        maskValue = Inet4ConverterValidator.convertIpStringToLong(mask);
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public long getValue() {
        return maskValue;
    }
}
