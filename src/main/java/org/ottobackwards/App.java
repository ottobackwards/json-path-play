/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ottobackwards;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;

/* note some of this is from the JsonMapParser in Apache Metron */
public class App {

  private final MapStrategy mapStrategy = MapStrategy.UNFOLD;
  private TypeRef<List<Map<String, Object>>> typeRef = new TypeRef<List<Map<String, Object>>>() {
  };

  private static interface Handler {

    JSONObject handle(String key, Map value, JSONObject obj);
  }

  public static enum MapStrategy implements Handler {
    DROP((key, value, obj) -> obj), UNFOLD(MapStrategy::recursiveUnfold),
    ALLOW((key, value, obj) -> {
      obj.put(key, value);
      return obj;
    }), ERROR((key, value, obj) -> {
      throw new IllegalStateException(
          "Unable to process " + key + " => " + value + " because value is a map.");
    });
    Handler handler;

    MapStrategy(Handler handler) {
      this.handler = handler;
    }

    private static JSONObject recursiveUnfold(String key, Map value, JSONObject obj) {
      Set<Map.Entry<Object, Object>> entrySet = value.entrySet();
      for (Map.Entry<Object, Object> kv : entrySet) {
        String newKey = Joiner.on(".").join(key, kv.getKey().toString());
        if (kv.getValue() instanceof Map) {
          recursiveUnfold(newKey, (Map) kv.getValue(), obj);
        } else {
          obj.put(newKey, kv.getValue());
        }
      }
      return obj;
    }

    @Override
    public JSONObject handle(String key, Map value, JSONObject obj) {
      return handler.handle(key, value, obj);
    }

  }

  private final ObjectMapper objectMapper = new ObjectMapper();
  public App() {
    Configuration.setDefaults(new Configuration.Defaults() {

      private final JsonProvider jsonProvider = new JacksonJsonProvider();
      private final MappingProvider mappingProvider = new JacksonMappingProvider();

      @Override
      public JsonProvider jsonProvider() {
        return jsonProvider;
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }
    });
    CacheProvider.setCache(new LRUCache(100));
  }

  public List<JSONObject> parserMany(byte[] rawMessage, String jsonPath) {
    List<Map<String, Object>> messages = JsonPath.parse(new String(rawMessage))
        .read(jsonPath, typeRef);
    final List<JSONObject> ret = new ArrayList<>();
    messages.forEach((m) -> {
      JSONObject jsonObject = normalizeJSON(m);
      String originalString = jsonObject.toJSONString();
      jsonObject.put("original_string", originalString);
      if (!jsonObject.containsKey("timestamp")) {
        jsonObject.put("timestamp", System.currentTimeMillis());
      }
      ret.add(jsonObject);
    });
    return ret;
  }

  public List<JSONObject> parse(byte[] rawMessage) {
    try {
      String originalString = new String(rawMessage);
      //convert the JSON_LIST blob into a String -> Object map
      Map<String, Object> rawMap = objectMapper
          .readValue(originalString, new TypeReference<Map<String, Object>>() {
          });
      JSONObject ret = normalizeJSON(rawMap);
      ret.put("original_string", originalString);
      if (!ret.containsKey("timestamp")) {
        //we have to ensure that we have a timestamp.  This is one of the pre-requisites for the parser.
        ret.put("timestamp", System.currentTimeMillis());
      }
      return ImmutableList.of(ret);
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Process all sub-maps via the MapHandler.  We have standardized on one-dimensional maps as our data model..
   *
   * @param map
   * @return
   */
  private JSONObject normalizeJSON(Map<String, Object> map) {
    JSONObject ret = new JSONObject();
    for (Map.Entry<String, Object> kv : map.entrySet()) {
      if (kv.getValue() instanceof Map) {
        mapStrategy.handle(kv.getKey(), (Map) kv.getValue(), ret);
      } else {
        ret.put(kv.getKey(), kv.getValue());
      }
    }
    return ret;
  }
}
