/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.clustering.infinispan.subsystem;

import org.infinispan.commons.executors.BlockingThreadPoolExecutorFactory;
import org.infinispan.commons.executors.ThreadPoolExecutorFactory;
import org.infinispan.configuration.global.ThreadPoolConfiguration;
import org.infinispan.configuration.global.ThreadPoolConfigurationBuilder;
import org.infinispan.server.commons.controller.ResourceServiceBuilder;
import org.infinispan.server.commons.service.Builder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

/**
 * @author Radoslav Husar
 * @version August 2015
 */
public class ThreadPoolBuilder extends ComponentConfigurationBuilder<ThreadPoolConfiguration> implements ResourceServiceBuilder<ThreadPoolConfiguration> {

    private final ThreadPoolConfigurationBuilder builder = new ThreadPoolConfigurationBuilder(null);
    private final ThreadPoolDefinition definition;
    private final boolean nonBlocking;

    ThreadPoolBuilder(ThreadPoolDefinition definition, String containerName, boolean nonBlocking) {
        super(definition.getServiceName(containerName));
        this.definition = definition;
        this.nonBlocking = nonBlocking;
    }

    @Override
    public Builder<ThreadPoolConfiguration> configure(OperationContext context, ModelNode model) throws OperationFailedException {
        ThreadPoolExecutorFactory<?> factory = new BlockingThreadPoolExecutorFactory(
                this.definition.getMaxThreads().resolveModelAttribute(context, model).asInt(),
                this.definition.getMinThreads().resolveModelAttribute(context, model).asInt(),
                this.definition.getQueueLength().resolveModelAttribute(context, model).asInt(),
                this.definition.getKeepAliveTime().resolveModelAttribute(context, model).asLong(),
                this.nonBlocking
        );
        this.builder.threadPoolFactory(factory);

        return this;
    }

    @Override
    public ThreadPoolConfiguration getValue() throws IllegalStateException, IllegalArgumentException {
        return this.builder.create();
    }

}
