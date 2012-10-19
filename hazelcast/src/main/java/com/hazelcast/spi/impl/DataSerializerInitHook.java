/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
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

package com.hazelcast.spi.impl;

import com.hazelcast.nio.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DataSerializerHook;

import java.util.HashMap;
import java.util.Map;

/**
 * @mdogan 8/24/12
 */
public final class DataSerializerInitHook implements DataSerializerHook {

    public Map<String, DataSerializableFactory> createFactoryMap() {
        final Map<String, DataSerializableFactory> factories = new HashMap<String, DataSerializableFactory>();

        factories.put(Response.class.getName(), new DataSerializableFactory() {
            public DataSerializable create() {
                return new Response();
            }
        });

        factories.put(PartitionIterator.class.getName(), new DataSerializableFactory() {
            public DataSerializable create() {
                return new PartitionIterator();
            }
        });

        factories.put(OperationWrapper.class.getName(), new DataSerializableFactory() {
            public DataSerializable create() {
                return new OperationWrapper();
            }
        });

        return factories;
    }
}