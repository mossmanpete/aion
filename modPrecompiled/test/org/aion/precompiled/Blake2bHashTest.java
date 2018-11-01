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
package org.aion.precompiled;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.aion.base.db.IContractDetails;
import org.aion.base.db.IPruneConfig;
import org.aion.base.db.IRepositoryConfig;
import org.aion.base.util.ByteUtil;
import org.aion.db.impl.DBVendor;
import org.aion.db.impl.DatabaseFactory;
import org.aion.mcf.config.CfgPrune;
import org.aion.precompiled.contracts.Blake2bHashContract;
import org.aion.vm.AbstractExecutionResult.ResultCode;
import org.aion.vm.ExecutionResult;
import org.aion.zero.impl.db.ContractDetailsAion;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Blake2bHashTest {

    private static final long INPUT_NRG = 1000;

    private byte[] byteArray1 = "a0010101010101010101010101".getBytes();
    private byte[] byteArray2 = "1".getBytes();
    private byte[] shortByteArray = "".getBytes();
    private Blake2bHashContract blake2bHasher;

    @Before
    public void setUp() {
        blake2bHasher = new Blake2bHashContract();
    }

    @Test
    public void testBlake256() {
        ExecutionResult res = blake2bHasher.execute(byteArray1, INPUT_NRG);
        byte[] output = res.getOutput();

        assertEquals(ResultCode.SUCCESS, res.getResultCode());
        assertEquals(32, output.length);
        String blake2bStr1 = "aa6648de0988479263cf3730a48ef744d238b96a5954aa77d647ae965d3f7715";
        assertTrue(blake2bStr1.equals(ByteUtil.toHexString(output)));
    }

    @Test
    public void testBlake256_2() {
        ExecutionResult res = blake2bHasher.execute(byteArray2, INPUT_NRG);
        byte[] output = res.getOutput();

        assertEquals(ResultCode.SUCCESS, res.getResultCode());
        assertEquals(32, output.length);

        String blake2bStr2 = "92cdf578c47085a5992256f0dcf97d0b19f1f1c9de4d5fe30c3ace6191b6e5db";
        assertTrue(blake2bStr2.equals(ByteUtil.toHexString(output)));
    }

    @Test
    @Ignore
    public void testBlake128() {
        byte[] input = Blake2bHashContract.setupInput(1, byteArray1);
        ExecutionResult res = blake2bHasher.execute(input, INPUT_NRG);
        byte[] output = res.getOutput();

        assertEquals(ResultCode.SUCCESS, res.getResultCode());
        assertEquals(16, output.length);

        System.out.println("The blake128 hash for '" + new String(byteArray1,
            StandardCharsets.UTF_8) + "' is:");
        System.out.print("      ");
        for (byte b : output) {
            System.out.print(b + " ");
        }
        System.out.println();
    }


    @Test
    public void invalidInputLength() {
        ExecutionResult res = blake2bHasher.execute(shortByteArray, INPUT_NRG);
        assertEquals(ResultCode.INTERNAL_ERROR, res.getResultCode());
    }

    @Test
    public void invalidInputLength2() {
        byte[] BigByteArray = new byte[1024*1024 + 1];
        ExecutionResult res = blake2bHasher.execute(BigByteArray, INPUT_NRG);
        assertEquals(ResultCode.INTERNAL_ERROR, res.getResultCode());
    }

    @Test
    public void insufficientNRG() {
        byte[] input = Blake2bHashContract.setupInput(0, byteArray1);
        ExecutionResult res = blake2bHasher.execute(input, 30);
        assertEquals(ResultCode.OUT_OF_NRG, res.getResultCode());
    }

    @Test
    public void insufficientNRG2() {
        byte[] BigByteArray = new byte[1024*1024];
        long nrg = (long) (Math.ceil(1024*1024/4)*6 + 30);
        ExecutionResult res = blake2bHasher.execute(BigByteArray, nrg);
        assertEquals(ResultCode.SUCCESS, res.getResultCode());

        res = blake2bHasher.execute(BigByteArray, nrg - 1);
        assertEquals(ResultCode.OUT_OF_NRG, res.getResultCode());
    }

    @Test
    @Ignore
    public void testInvalidOperation() {
        byte[] input = Blake2bHashContract.setupInput(3, byteArray1);
        ExecutionResult res = blake2bHasher.execute(input, INPUT_NRG);
        assertEquals(ResultCode.INTERNAL_ERROR, res.getResultCode());
    }

    private static IRepositoryConfig repoConfig =
        new IRepositoryConfig() {
            @Override
            public String getDbPath() {
                return "";
            }

            @Override
            public IPruneConfig getPruneConfig() {
                return new CfgPrune(false);
            }

            @Override
            public IContractDetails contractDetailsImpl() {
                return ContractDetailsAion.createForTesting(0, 1000000).getDetails();
            }

            @Override
            public Properties getDatabaseConfig(String db_name) {
                Properties props = new Properties();
                props.setProperty(DatabaseFactory.Props.DB_TYPE, DBVendor.MOCKDB.toValue());
                props.setProperty(DatabaseFactory.Props.ENABLE_HEAP_CACHE, "false");
                return props;
            }
        };
}
