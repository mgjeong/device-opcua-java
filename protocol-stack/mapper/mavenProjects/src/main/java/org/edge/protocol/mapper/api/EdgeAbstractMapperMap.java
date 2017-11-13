package org.edge.protocol.mapper.api;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.MapMaker;

public class EdgeAbstractMapperMap extends ForwardingConcurrentMap<String, String>
        implements EdgeBaseMapperMap {
    private final ConcurrentMap<String, String> metadataMapperMap;

    public EdgeAbstractMapperMap() {
        MapMaker mapMaker = new MapMaker();

        metadataMapperMap = makeNodeMap(mapMaker);
    }

    protected ConcurrentMap<String, String> makeNodeMap(MapMaker mapMaker) {
        return mapMaker.makeMap();
    }

    @Override
    protected final ConcurrentMap<String, String> delegate() {
        return metadataMapperMap;
    }
}