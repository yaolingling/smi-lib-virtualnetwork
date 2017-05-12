/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.validation;

import java.util.Comparator;
import java.util.Scanner;

/**
 * The Class IPv4StringComparator.
 */
public class IPv4StringComparator implements Comparator<String> {

    /**
     * used to compare two IPv4 strings.
     *
     * @param inetAddress1 the inet address 1
     * @param inetAddress2 the inet address 2
     * @return the int
     */
    @Override
    public int compare(String inetAddress1, String inetAddress2) {
        return toNumeric(inetAddress1).compareTo(toNumeric(inetAddress2));
    }


    /**
     * To numeric.
     *
     * @param ipAddress the ip address
     * @return the long
     */
    private static Long toNumeric(String ipAddress) {
        Long returnValue;
        try (Scanner scanner = new Scanner(ipAddress);) {
            scanner.useDelimiter("\\.");
            returnValue = (scanner.nextLong() << 24) + (scanner.nextLong() << 16) + (scanner.nextLong() << 8) + (scanner.nextLong());
            scanner.close();
        }
        return returnValue;
    }
}
