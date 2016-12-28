/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author navega
 */
public class PortManager {

    public static final int FILE_LIST_PORT = 52684;

    private static final int RANGE_UP = 61000;
    private static final int RANGE_DOWN = 32768;

    private static List<Integer> mUsedPorts = new ArrayList();

    public static int getMultiCastPort() {
        Random rn = new Random();
        int range = RANGE_UP - RANGE_DOWN + 1;
        int randomNum = RANGE_DOWN;
        do {
            randomNum = rn.nextInt(range) + RANGE_DOWN;

            if (mUsedPorts.size() == range) {
                mUsedPorts = new ArrayList<>();
            }

        } while (mUsedPorts.contains(randomNum) || randomNum == FILE_LIST_PORT);

        return randomNum;
    }
}
