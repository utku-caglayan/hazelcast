/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.memory;

import com.hazelcast.jet.io.impl.serialization.JetSerializationServiceImpl;
import com.hazelcast.jet.io.serialization.JetDataInput;
import com.hazelcast.jet.io.serialization.JetDataOutput;
import com.hazelcast.jet.io.serialization.JetSerializationService;
import com.hazelcast.jet.memory.memoryblock.MemoryBlock;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


@RunWith(HazelcastSerialClassRunner.class)
@Category({QuickTest.class})
public class JetSerializationTest extends BaseMemoryTest {
    private final JetSerializationService serializationService = new JetSerializationServiceImpl();

    @Before
    public void setUp() throws Exception {
        init();
    }

    @Test
    public void testHeap() throws IOException {
        test(heapMemoryBlock);
    }

    @Test
    public void testNative() throws IOException {
        test(nativeMemoryBlock);
    }

    public void test(MemoryBlock memoryBlock) throws IOException {
        JetDataOutput output = serializationService.createObjectDataOutput(memoryBlock, useBigEndian());
        writeBlock(output);

        long address = output.baseAddress();
        long allocatedSize = output.allocatedSize();

        assertEquals(memoryBlock.getUsedBytes(), allocatedSize + MemoryBlock.TOP_OFFSET);

        long writeCount = memoryBlock.getAvailableBytes() / allocatedSize;

        for (int i = 0; i < writeCount - 1; i++) {
            writeBlock(output);
        }

        JetDataInput input = serializationService.createObjectDataInput(memoryBlock, useBigEndian());
        input.reset(address, writeCount * allocatedSize);
        for (int i = 0; i < writeCount; i++) {
            assertEquals(input.readObject(), "1");
            assertEquals(input.readInt(), 1);
            assertEquals(input.readLong(), 1L);
            assertEquals(input.read(), 1);
            assertEquals(input.readChar(), 1);
            assertEquals(input.readBoolean(), true);
            assertEquals(input.readDouble(), 1d, 0d);
            assertEquals(input.readFloat(), 1f, 0f);
            assertEquals(input.readShort(), 1);
            assertArrayEquals(input.readByteArray(), new byte[]{1});
            assertArrayEquals(input.readCharArray(), new char[]{(char) 1});
            assertEquals(input.readBooleanArray()[0], true);
            assertArrayEquals(input.readIntArray(), new int[]{1});
        }
    }

    private void writeBlock(JetDataOutput output) throws IOException {
        output.writeObject("1");
        output.writeInt(1);
        output.writeLong(1L);
        output.write((byte) 1);
        output.writeChar((char) 1);
        output.writeBoolean(true);
        output.writeDouble(1d);
        output.writeFloat(1f);
        output.writeShort(1);
        output.writeByteArray(new byte[]{1});
        output.writeCharArray(new char[]{(char) 1});
        output.writeBooleanArray(new boolean[]{true});
        output.writeIntArray(new int[]{1});
    }
}
