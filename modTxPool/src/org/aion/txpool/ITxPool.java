/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */
package org.aion.txpool;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.aion.base.type.AionAddress;
import org.aion.base.type.ITransaction;

/**
 * Aion pending state should be the only user of transaction pool.
 *
 * @param <TX>
 */
public interface ITxPool<TX extends ITransaction> {

    String PROP_TX_TIMEOUT = "tx-timeout";
    String PROP_BLOCK_SIZE_LIMIT = "blk-size-limit";
    String PROP_BLOCK_NRG_LIMIT = "blk-nrg-limit";
    String PROP_TX_SEQ_MAX = "tx-seq-max";

    List<TX> add(List<TX> tx);

    // return TX if the TX add success, if the pool already has the same nonce tx. return the old
    // tx.
    TX add(TX tx);

    List<TX> remove(List<TX> tx);

    List<TX> remove(Map<AionAddress, BigInteger> accNonce);

    int size();

    List<TX> snapshot();

    List<TX> getOutdatedList();

    long getOutDateTime();

    BigInteger bestPoolNonce(AionAddress addr);

    void updateBlkNrgLimit(long nrg);

    @SuppressWarnings("SameReturnValue")
    String getVersion();

    List<TX> snapshotAll();

    TX getPoolTx(AionAddress from, BigInteger txNonce);
}
