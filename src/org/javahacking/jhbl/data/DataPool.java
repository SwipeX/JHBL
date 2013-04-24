package org.javahacking.jhbl.data;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author trDna
 */
public abstract class DataPool {

    public abstract <T extends DataPool> T asPool(Object[] args);

}
