/*
 * Copyright 2016 Pinterest, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.deployservice.rodimus;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.pinterest.deployservice.common.HTTPClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RodimusManagerImpl implements RodimusManager {
    private static final Logger LOG = LoggerFactory.getLogger(RodimusManagerImpl.class);
    private static  final int RETRIES = 3;
    private String rodimusUrl;
    private HTTPClient httpClient;
    private Map<String, String> headers;
    private Gson gson;

    public RodimusManagerImpl(String rodimusUrl, String token) {
        this.rodimusUrl = rodimusUrl;
        this.httpClient = new HTTPClient();
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
        if (token != null) {
            this.headers.put("Accept", "*/*");
            this.headers.put("Authorization", String.format("token %s", token));
        }
        this.gson = new GsonBuilder().addSerializationExclusionStrategy(new CustomExclusionStrategy()).create();
    }

    private class CustomExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals("__isset_bit_vector");
        }

        @Override
        public boolean shouldSkipClass(Class<?> c) {
            return false;
        }
    }

    @Override
    public void terminateHostsByClusterName(String clusterName, Collection<String> hostIds) throws Exception {
        String url = String.format("%s/v1/clusters/%s/hosts", rodimusUrl, clusterName);
        httpClient.delete(url, gson.toJson(hostIds), headers, RETRIES);
    }

    @Override
    public Collection<String> getTerminatedHosts(Collection<String> hostIds) throws Exception {
        String url = String.format("%s/v1/hosts/state?actionType=%s", rodimusUrl, "TERMINATED");
        String res = httpClient.get(url, gson.toJson(hostIds), null, headers, RETRIES);
        return gson.fromJson(res, new TypeToken<ArrayList<String>>() {}.getType());
    }
}
