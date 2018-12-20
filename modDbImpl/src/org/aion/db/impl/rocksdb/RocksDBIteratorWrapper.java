package org.aion.db.impl.rocksdb;

import java.util.Iterator;
import org.rocksdb.RocksIterator;

/**
 * A wrapper for the {@link RocksIterator} conforming to the {@link Iterator<byte[]>} interface.
 *
 * @author Alexandra Roatis
 */
public class RocksDBIteratorWrapper implements Iterator<byte[]> {
    RocksIterator itr;

    public RocksDBIteratorWrapper(RocksIterator itr) {
        this.itr = itr;
        itr.seekToFirst();
    }

    @Override
    public boolean hasNext() {
        return itr.isValid();
    }

    @Override
    public byte[] next() {
        byte[] key = itr.key();
        itr.next();
        return key;
    }
}
