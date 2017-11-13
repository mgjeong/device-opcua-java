package org.edge.protocol.mapper.api;

import java.util.Optional;

public class EdgeMapper {
    private EdgeBaseMapperMap mapperMap;

    public EdgeMapper() {
        mapperMap = new EdgeMapperMap();
    }

    public void addMappingData(String id, String value) throws Exception {
        if (false == mapperMap.containsKey(id)) {
            mapperMap.put(id, value);
        }
    }

    public String getMappingData(String id) {
        Optional<String> ret = mapperMap.getNode(id);
        if(ret.equals(Optional.empty())) {
            return null;
        } else {
            return mapperMap.getNode(id).get();
        }
    }

    private class EdgeMapperMap extends EdgeAbstractMapperMap {
    }
}
