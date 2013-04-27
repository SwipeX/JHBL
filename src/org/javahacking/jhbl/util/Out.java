package org.javahacking.jhbl.util;

import java.util.logging.Logger;

/**
 * @author trDna
 */
public class Out {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    public static void logInfo(String msg){
        LOGGER.info(msg);
    }
}
