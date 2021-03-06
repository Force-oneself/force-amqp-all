// Copyright (c) 2019 Pivotal Software, Inc.  All rights reserved.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 1.1 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.

package com.rabbitmq.client.test;

import com.rabbitmq.client.Method;
import com.rabbitmq.client.impl.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChannelNTest {

    ConsumerWorkService consumerWorkService;
    ExecutorService executorService;

    @Before
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        consumerWorkService = new ConsumerWorkService(executorService, null, 1000, 1000);
    }

    @After
    public void tearDown() {
        consumerWorkService.shutdown();
        executorService.shutdownNow();
    }

    @Test
    public void serverBasicCancelForUnknownConsumerDoesNotThrowException() throws Exception {
        AMQConnection connection = Mockito.mock(AMQConnection.class);
        ChannelN channel = new ChannelN(connection, 1, consumerWorkService);
        Method method = new AMQImpl.Basic.Cancel.Builder().consumerTag("does-not-exist").build();
        channel.processAsync(new AMQCommand(method));
    }

    @Test(expected = IOException.class)
    public void callingBasicCancelForUnknownConsumerThrowsException() throws Exception {
        AMQConnection connection = Mockito.mock(AMQConnection.class);
        ChannelN channel = new ChannelN(connection, 1, consumerWorkService);
        channel.basicCancel("does-not-exist");
    }

}
