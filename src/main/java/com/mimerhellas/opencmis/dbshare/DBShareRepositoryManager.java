/*
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.mimerhellas.opencmis.dbshare;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

/**
 * Manages all repositories.
 */
public class DBShareRepositoryManager {

    private final Map<String, DBShareRepository> repositories;

    public DBShareRepositoryManager() {
        repositories = new HashMap<String, DBShareRepository>();
    }

    /**
     * Adds a repository object.
     *
     * @param fsr
     */
    public void addRepository(DBShareRepository fsr) {
        if (fsr == null || fsr.getRepositoryId() == null) {
            return;
        }

        repositories.put(fsr.getRepositoryId(), fsr);
    }

    /**
     * Gets a repository object by id.
     *
     * @param repositoryId
     * @return
     */
    public DBShareRepository getRepository(String repositoryId) {
        DBShareRepository result = repositories.get(repositoryId);
        if (result == null) {
            throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
        }

        return result;
    }

    /**
     * Returns all repository objects.
     *
     * @return
     */
    public Collection<DBShareRepository> getRepositories() {
        return repositories.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);

        for (DBShareRepository repository : repositories.values()) {
            sb.append('[');
            sb.append(repository.getRepositoryId());
            sb.append(" -> ");
            sb.append(repository.getRootDirectory().getAbsolutePath());
            sb.append(']');
        }

        return sb.toString();
    }
}
