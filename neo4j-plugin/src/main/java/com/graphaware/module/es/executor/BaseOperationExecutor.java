/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.module.es.executor;

import com.graphaware.common.representation.NodeRepresentation;
import com.graphaware.writer.thirdparty.WriteOperation;
import io.searchbox.client.JestClient;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.*;

/**
 * Base class for {@link OperationExecutor} implementations.
 */
public abstract class BaseOperationExecutor implements OperationExecutor {

    private List<WriteOperation<?>> allFailed;

    private final JestClient client;
    private final String index;
    private final String keyProperty;

    /**
     * Construct a new executor.
     *
     * @param client      Jest client. Must not be <code>null</code>.
     * @param index       Elasticsearch index name. Must not be <code>null</code> or empty.
     * @param keyProperty name of the node property that serves as the key, under which the node will be indexed in Elasticsearch. Must not be <code>null</code> or empty.
     */
    public BaseOperationExecutor(JestClient client, String index, String keyProperty) {
        notNull(client);
        hasLength(index);
        hasLength(keyProperty);

        this.client = client;
        this.index = index;
        this.keyProperty = keyProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        allFailed = new LinkedList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WriteOperation<?>> flush() {
        return allFailed;
    }

    protected String getKey(NodeRepresentation node) {
        return String.valueOf(node.getProperties().get(keyProperty));
    }

    protected Map<String, String> nodeToProps(NodeRepresentation node) {
        Map<String, String> source = new LinkedHashMap<>();
        for (String key : node.getProperties().keySet()) {
            source.put(key, String.valueOf(node.getProperties().get(key)));
        }
        return source;
    }

    protected final void addFailed(WriteOperation<?> failed) {
        this.allFailed.add(failed);
    }

    protected final void clearFailed() {
        allFailed.clear();
    }

    protected JestClient getClient() {
        return client;
    }

    protected String getIndex() {
        return index;
    }
}
