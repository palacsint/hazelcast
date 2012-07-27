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

package com.hazelcast.impl.spi;

import com.hazelcast.impl.partition.PartitionInfo;
import com.hazelcast.nio.Address;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

class SinglePartitionInvocation extends FutureTask implements Invocation, Callback {
    protected final NodeService nodeService;
    protected final String serviceName;
    protected final Operation op;
    protected final PartitionInfo partitionInfo;
    protected int replicaIndex = 0;
    protected int tryCount = 100;
    protected long tryPauseMillis = 500;
    protected volatile int invokeCount = 0;

    SinglePartitionInvocation(NodeService nodeService, String serviceName, Operation op, PartitionInfo partitionInfo, int replicaIndex, int tryCount, long tryPauseMillis) {
        super(op, null);
        this.nodeService = nodeService;
        this.serviceName = serviceName;
        this.op = op;
        this.partitionInfo = partitionInfo;
        this.replicaIndex = replicaIndex;
        this.tryCount = tryCount;
        this.tryPauseMillis = tryPauseMillis;
    }

    public void notify(Object result) {
        if (result instanceof Response) {
            Response response = (Response) result;
            if (response.isException()) {
                setResult(response.getResult());
            } else {
                setResult(response.getResultData());
            }
        } else {
            setResult(result);
        }
    }

    Address getTarget() {
        return partitionInfo.getReplicaAddress(replicaIndex);
    }

    public Future invoke() {
        try {
            invokeCount++;
            nodeService.invokeOnSinglePartition(SinglePartitionInvocation.this);
        } catch (Exception e) {
            setResult(e);
        }
        return this;
    }

    void setResult(Object obj) {
        if (obj instanceof RetryableException) {
            if (invokeCount < tryCount) {
                try {
                    Thread.sleep(tryPauseMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                invoke();
            } else {
                setException((Throwable) obj);
            }
        } else {
            if (obj instanceof Exception) {
                setException((Throwable) obj);
            } else {
                set(obj);
            }
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public Operation getOperation() {
        return op;
    }

    public PartitionInfo getPartitionInfo() {
        return partitionInfo;
    }

    public int getReplicaIndex() {
        return replicaIndex;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public boolean isCancelled() {
        return false;
    }

    public int getPartitionId() {
        return partitionInfo.getPartitionId();
    }
}