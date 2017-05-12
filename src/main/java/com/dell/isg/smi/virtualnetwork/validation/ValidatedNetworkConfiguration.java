/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.elm.exception.BusinessValidationException;
import com.dell.isg.smi.virtualnetwork.entity.NetworkConfiguration;
import com.dell.isg.smi.virtualnetwork.exception.ErrorCodeEnum;
import com.dell.isg.smi.virtualnetwork.model.NetworkType;

/**
 * The Class ValidatedNetworkConfiguration.
 */
public class ValidatedNetworkConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ValidatedNetworkConfiguration.class);
    private static final int MIN_VLAN_ID = 1;
    private static final int MAX_VLAN_ID = 4094;

    private boolean isStatic;
    private String name;
    private String networkType;
    private Integer vlanId;


    /**
     * Instantiates a new validated network configuration.
     *
     * @param content the content
     */
    public ValidatedNetworkConfiguration(NetworkConfiguration content) {
        name = content.getName();

        Validator.validateName(name);
        Validator.validateDescription(content.getDescription());
        validateNetworkType(content.getType());

        isStatic = content.isStatic();
        if (isStatic) {
            if (!content.getType().equals(NetworkType.OOB_OR_INFRASTRUCTURE_MANAGEMENT.value()) && !content.getType().equals(NetworkType.PRIVATE_LAN.value()) && !content.getType().equals(NetworkType.PUBLIC_LAN.value()) && !content.getType().equals(NetworkType.STORAGE_ISCSI_SAN.value()) && !content.getType().equals(NetworkType.HYPERVISOR_MIGRATION.value()) && !content.getType().equals(NetworkType.HYPERVISOR_CLUSTER_PRIVATE.value()) && !content.getType().equals(NetworkType.PXE.value()) && !content.getType().equals(NetworkType.FILESHARE.value()) && !content.getType().equals(NetworkType.FIP_SNOOPING.value()) && !content.getType().equals(NetworkType.HARDWARE_MANAGEMENT.value()) && !content.getType().equals(NetworkType.HYPERVISOR_MANAGEMENT.value())) {
                logger.error("Validation failure: Network " + content.getType() + " cannot be static.");

                BusinessValidationException businessValidationException = new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_CANNOT_BE_STATIC);
                businessValidationException.addAttribute(content.getType());
                throw businessValidationException;
            }
            new ValidatedStaticNetworkProperties(content, content.getType());

        } else {
            if (content.getType().equals(NetworkType.OOB_OR_INFRASTRUCTURE_MANAGEMENT.value())) {
                logger.error("Validation failure: management network must be static.");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_MUST_BE_STATIC);
            }
            if (content.getSubnet() != null || content.getDnsSuffix() != null || content.getGateway() != null || content.getPrimaryDns() != null || content.getSecondaryDns() != null) {
                logger.error("Validation failure: static properties supplied, but isStatic is false.");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_NETWORK_MISMATCH);
            }
        }
        networkType = content.getType();
        vlanId = content.getVlanId();
        validateAllFields();
        validateVlanId(content.getType(), content.getVlanId());

    }


    private void validateAllFields() {
        if (null == name || name.isEmpty()) {
            logger.error("Validation failure: Network name is Empty or null.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_EMPTY_NETWORKNAME);
        }
        if (vlanId != null && ((MIN_VLAN_ID > vlanId) || (MAX_VLAN_ID < vlanId))) {
            logger.error("Validation failure: VLAN Id is out of range.");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_VLANID_OUTOF_RANGE);
        }
    }


    private void validateVlanId(String type, Integer vlandId) {

        if (null == vlandId) {
            if (!type.isEmpty() && !type.equalsIgnoreCase(NetworkType.OOB_OR_INFRASTRUCTURE_MANAGEMENT.value()) && !type.equalsIgnoreCase(NetworkType.HARDWARE_MANAGEMENT.value())) {
                logger.error("Validation failure: VLanId cannot be null for NetworkType other than 'OOB Management Network'.");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_VLANID_CANOT_BE_NULL);
            }
        } else {
            if (type.equalsIgnoreCase(NetworkType.OOB_OR_INFRASTRUCTURE_MANAGEMENT.value())) {
                logger.error("Validation failure: VLanId must be null for NetworkType  'OOB Management Network'.");
                throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_VLANID_MUST_BE_NULL);
            }
        }
    }


    private void validateNetworkType(String type) {

        NetworkType[] networkTypes = NetworkType.values();
        boolean validType = false;
        for (NetworkType networkType : networkTypes) {
            if (networkType.toString().equals(type)) {
                validType = true;
                break;
            }
        }
        if (!validType) {
            logger.error("Validation failure: Invalid Network Type. ");
            throw new BusinessValidationException(ErrorCodeEnum.NETWORKCONF_INVALIDNETWORK_TYPE);
        }
    }


    /**
     * Checks if is static.
     *
     * @return the isStatic
     */
    public boolean isStatic() {
        return isStatic;
    }


    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Gets the network type.
     *
     * @return the networkType
     */
    public String getNetworkType() {
        return networkType;
    }


    /**
     * Gets the vlan id.
     *
     * @return the vlanId
     */
    public int getVlanId() {
        return vlanId;
    }

}
