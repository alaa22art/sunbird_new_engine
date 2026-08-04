package com.certacure.lis.interfaces.middleware.core;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MacAddressChecker {

    /**
     * Retrieves the first non-loopback MAC address found on the local machine
     * @return MAC address as a string, or null if not found
     */
    public static String getLocalMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    // Convert MAC address bytes to string representation
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compares the local MAC address with a given MAC address
     * @param macAddressToCompare The MAC address to compare against
     * @return true if the MAC addresses match (case insensitive), false otherwise
     */
    public static boolean compareMacAddress(String macAddressToCompare) {
        if (macAddressToCompare == null || macAddressToCompare.trim().isEmpty()) {
            return false;
        }
        
        String localMac = getLocalMacAddress();
        if (localMac == null) {
            return false;
        }
        
        // Normalize both MAC addresses by removing all non-alphanumeric characters and converting to uppercase
        String normalizedLocalMac = localMac.replaceAll("[^A-Fa-f0-9]", "").toUpperCase();
        String normalizedCompareMac = macAddressToCompare.replaceAll("[^A-Fa-f0-9]", "").toUpperCase();
        
        return normalizedLocalMac.equals(normalizedCompareMac);
    }

    public static void main(String[] args) {
        // Example usage
        String macToCheck = "00-1A-2B-3C-4D-5E"; // Replace with the MAC you want to check
        
        System.out.println("Local MAC Address: " + getLocalMacAddress());
        System.out.println("Comparing with: " + macToCheck);
        
        boolean isMatch = compareMacAddress(macToCheck);
        System.out.println("MAC addresses match: " + isMatch);
    }
}