/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.frontend.connection;

import org.apache.shardingsphere.infra.config.props.ConfigurationPropertyKey;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.junit.Test;
import org.mockito.MockedStatic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public final class ConnectionLimitContextTest {
    
    @Test
    public void assertConnectionsLimited() {
        ContextManager contextManager = mockContextManager(2);
        try (MockedStatic<ProxyContext> proxyContext = mockStatic(ProxyContext.class, RETURNS_DEEP_STUBS)) {
            proxyContext.when(() -> ProxyContext.getInstance().getContextManager()).thenReturn(contextManager);
            assertTrue(ConnectionLimitContext.getInstance().connectionAllowed());
            assertTrue(ConnectionLimitContext.getInstance().connectionAllowed());
            assertFalse(ConnectionLimitContext.getInstance().connectionAllowed());
            ConnectionLimitContext.getInstance().connectionInactive();
            ConnectionLimitContext.getInstance().connectionInactive();
            assertTrue(ConnectionLimitContext.getInstance().connectionAllowed());
            ConnectionLimitContext.getInstance().connectionInactive();
            ConnectionLimitContext.getInstance().connectionInactive();
            ConnectionLimitContext.getInstance().connectionInactive();
        }
    }
    
    @Test
    public void assertConnectionsUnlimited() {
        ContextManager contextManager = mockContextManager(0);
        try (MockedStatic<ProxyContext> proxyContext = mockStatic(ProxyContext.class, RETURNS_DEEP_STUBS)) {
            proxyContext.when(() -> ProxyContext.getInstance().getContextManager()).thenReturn(contextManager);
            assertFalse(ConnectionLimitContext.getInstance().limitsMaxConnections());
        }
    }
    
    private static ContextManager mockContextManager(final int maxConnections) {
        ContextManager result = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        when(result.getMetaDataContexts().getMetaData().getProps().getValue(ConfigurationPropertyKey.PROXY_FRONTEND_MAX_CONNECTIONS)).thenReturn(maxConnections);
        return result;
    }
}
