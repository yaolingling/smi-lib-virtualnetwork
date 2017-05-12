/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class ReserveIpPoolAddressesRequest.
 */
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "usageId", "quantityRequested" })
@XmlRootElement(name = "ReserveIpPoolAddressesRequest")
public class ReserveIpPoolAddressesRequest {

    @ApiModelProperty(position = 1, example = "A1B2C3D")
    protected String usageId;
    @ApiModelProperty(position = 2, example = "3")
    protected long quantityRequested;


    /**
     * Gets the value of the usageId property.
     *
     * @return the usage id
     */
    public String getUsageId() {
        return usageId;
    }


    /**
     * Sets the value of the usageId property.
     *
     * @param value the new usage id
     */
    public void setUsageId(String value) {
        this.usageId = value;
    }


    /**
     * Gets the value of the quantityRequested property.
     *
     * @return the quantity requested
     */
    public long getQuantityRequested() {
        return quantityRequested;
    }


    /**
     * Sets the value of the quantityRequested property.
     *
     * @param value the new quantity requested
     */
    public void setQuantityRequested(long value) {
        this.quantityRequested = value;
    }

}
