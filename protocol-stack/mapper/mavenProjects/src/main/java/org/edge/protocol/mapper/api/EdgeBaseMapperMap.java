package org.edge.protocol.mapper.api;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public interface EdgeBaseMapperMap extends ConcurrentMap<String, String> {

    default void addNode(String id, String value) {
        put(id, value);
    }

    default boolean containsSession(String id) {
        return containsEndpoint(id);
    }

    default boolean containsEndpoint(String id) {
        return containsKey(id);
    }

    default Optional<String> getNode(String id) {
        return Optional.ofNullable(get(id));
    }

    default Optional<String> removeNode(String id) {
        return Optional.ofNullable(remove(id));
    }

}
