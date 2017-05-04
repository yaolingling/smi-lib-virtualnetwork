/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.entity;

import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.BASE_ENTITY_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_ADDRESS;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_EXPIRY_DATE;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_NAME;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_STATE;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_POOL_ENTRY_USAGE_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.T_IP_ADDRESS_POOL_ENTRY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.dell.isg.smi.virtualnetwork.model.IpAddressState;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
@Entity
@Table(name = T_IP_ADDRESS_POOL_ENTRY)
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = IP_ADDRESS_POOL_ENTRY_ID, referencedColumnName = BASE_ENTITY_ID)
public class IpAddressPoolEntry extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = IP_ADDRESS_POOL_ENTRY_ADDRESS)
    private long ipAddress;

    @Column(name = IP_ADDRESS_POOL_ENTRY_NAME)
    private String name;

    @Column(name = IP_ADDRESS_POOL_ENTRY_STATE)
    private String ipAddressState;

    @ManyToOne
    @JoinColumn(name = IP_ADDRESS_RANGE_ID)
    private IpAddressRange ipAddressRange;

    @Column(name = IP_ADDRESS_POOL_ENTRY_USAGE_ID)
    private String ipAddressUsageId;

    @Column(name = IP_ADDRESS_POOL_ENTRY_EXPIRY_DATE)
    private Date expiryDate;


    /**
     * @return the ipAddress
     */
    public long getIpAddress() {
        return ipAddress;
    }


    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(long ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the ipAddressState
     */
    public String getIpAddressState() {
        return ipAddressState;
    }


    /**
     * @param ipAddressState the ipAddressState to set
     */
    public void setIpAddressState(String ipAddressState) {
        this.ipAddressState = ipAddressState;
    }


    /**
     * @param ipAddressState the ipAddressState to set
     */
    public void setIPAddressState(IpAddressState state) {
        this.ipAddressState = state.toString();
    }


    /**
     * @return the ipAddressRange
     */
    public IpAddressRange getIPAddressRange() {
        return ipAddressRange;
    }


    /**
     * @param ipAddressRange the ipAddressRange to set
     */
    public void setIPAddressRange(IpAddressRange ipAddressRange) {
        this.ipAddressRange = ipAddressRange;
    }


    /**
     * @return the expiryDate
     */
    public Date getExpiryDate() {
        return expiryDate;
    }


    /**
     * @param expiryDate the expiryDate to set
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }


    /**
     * @return the ipAddressUsageId
     */
    public String getIpAddressUsageId() {
        return ipAddressUsageId;
    }


    /**
     * @param ipAddressUsageId the ipAddressUsageId to set
     */
    public void setIpAddressUsageId(String ipAddressUsageId) {
        this.ipAddressUsageId = ipAddressUsageId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof IpAddressPoolEntry)) {
            return false;
        }
        IpAddressPoolEntry newPoolEntry = (IpAddressPoolEntry) obj;
        return ((newPoolEntry.getIpAddress() - this.getIpAddress()) == 0);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == 0) ? 0 : (int) ipAddress);
        return result;
    }

}
