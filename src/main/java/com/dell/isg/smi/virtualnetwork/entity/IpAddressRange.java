/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.entity;

import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.BASE_ENTITY_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_DESCRIPTION;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_END_IP;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_NAME;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_START_IP;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.IP_ADDRESS_RANGE_TEMPORARY;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NETWORK_CONFIGURATION_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.T_IP_ADDRESS_RANGE;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author Lakshmi.Lakkireddy
 *
 */
@Entity
@Table(name = T_IP_ADDRESS_RANGE)
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = IP_ADDRESS_RANGE_ID, referencedColumnName = BASE_ENTITY_ID)
public class IpAddressRange extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = IP_ADDRESS_RANGE_NAME)
    private String name;

    @Column(name = IP_ADDRESS_RANGE_DESCRIPTION)
    private String description;

    @Column(name = IP_ADDRESS_RANGE_START_IP, nullable = false)
    private long startIpAddress;

    @Column(name = IP_ADDRESS_RANGE_END_IP, nullable = false)
    private long endIpAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NETWORK_CONFIGURATION_ID)
    private NetworkConfiguration networkConfiguration;

    @OneToMany(mappedBy = IP_ADDRESS_RANGE, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = IpAddressPoolEntry.class)
    @Column(name = IP_ADDRESS_RANGE_ID)
    private Set<IpAddressPoolEntry> ipAddressPool;

    @Column(name = IP_ADDRESS_RANGE_TEMPORARY)
    private boolean temporary;


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
     * @return the startIpAddress
     */
    public long getStartIpAddress() {
        return startIpAddress;
    }


    /**
     * @param startIpAddress the startIpAddress to set
     */
    public void setStartIpAddress(long startIpAddress) {
        this.startIpAddress = startIpAddress;
    }


    /**
     * @return the endIpAddress
     */
    public long getEndIpAddress() {
        return endIpAddress;
    }


    /**
     * @param endIpAddress the endIpAddress to set
     */
    public void setEndIpAddress(long endIpAddress) {
        this.endIpAddress = endIpAddress;
    }


    /**
     * @return the networkConfiguration
     */
    public NetworkConfiguration getNetworkConfiguration() {
        return networkConfiguration;
    }


    /**
     * @param networkConfiguration the networkConfiguration to set
     */
    public void setNetworkConfiguration(NetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }


    /**
     * @return the ipAddressPool
     */
    public Set<IpAddressPoolEntry> getIpAddressPool() {
        return ipAddressPool;
    }


    /**
     * @param ipAddressPool the ipAddressPool to set
     */
    public void setIpAddressPool(Set<IpAddressPoolEntry> ipAddressPool) {
        this.ipAddressPool = ipAddressPool;
    }


    /**
     * @return the temporary
     */
    public boolean isTemporary() {
        return temporary;
    }


    /**
     * @param temporary the temporary to set
     */
    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
