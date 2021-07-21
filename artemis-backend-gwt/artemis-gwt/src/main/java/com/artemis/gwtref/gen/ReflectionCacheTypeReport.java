/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.artemis.gwtref.gen;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JType;

import java.util.*;

import static com.google.gwt.core.ext.TreeLogger.*;

/** Summarizes types included in the reflection cache. */
public class ReflectionCacheTypeReport {

    private final Map<String, Collection<TypeSummary>> packagesAll = new TreeMap<>();
    private final Map<String, Collection<TypeSummary>> packagesCollapsed = new TreeMap<>();

    private final TreeLogger logger;
    private final String simpleName;

    private static final class TypeSummary implements Comparable<TypeSummary> {
        private final String simpleSourceName;

        public TypeSummary(JType type) {
            simpleSourceName = type.getSimpleSourceName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeSummary that = (TypeSummary) o;
            return Objects.equals(simpleSourceName, that.simpleSourceName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(simpleSourceName);
        }

        @Override
        public String toString() {
            return "TypeSummary{" +
                    "simpleSourceName='" + simpleSourceName + '\'' +
                    '}';
        }

        @Override
        public int compareTo(TypeSummary o) {
            return simpleSourceName.compareTo(o.simpleSourceName);
        }
    }

    public ReflectionCacheTypeReport(TreeLogger logger, String simpleName) {
        this.logger = logger;
        this.simpleName = simpleName;
    }

    /** Output report to log. */
    public void report() {
        reportTypesPerPackage();
        reportSummary();
    }

    /**
     * Add type to report.
     */
    public void add(JType t) {
        final String qualified = t.getQualifiedSourceName();
        final String typePackage = qualified.contains(".") ? qualified.substring(0, qualified.lastIndexOf(".")) : "<No package>";
        final TypeSummary summary = new TypeSummary(t);

        findOrCreatePackage(packagesAll, typePackage).add(summary);
        findOrCreatePackage(packagesCollapsed, collapsePackage(typePackage)).add(summary);
    }

    private void reportTypesPerPackage() {
        logger.log(Type.INFO, "Reflected types per package");
        for (String key : packagesAll.keySet()) {
            Collection<TypeSummary> values = packagesAll.get(key);
            logger.log(Type.INFO, "  " + key + " (types: " + values.size() + ")");
            for (TypeSummary typeSummary : values) {
                logger.log(Type.TRACE, "    " + typeSummary.simpleSourceName);
            }
        }
    }

    private void reportSummary() {
        logger.log(Type.INFO, "Reflected types summary");
        for (String key : packagesCollapsed.keySet()) {
            logger.log(Type.INFO, "  " + key + " (types: " + packagesCollapsed.get(key).size() + ")");
        }
    }

    /** @return Collapsed package to a depth of 3. A.B.C.D.E becomes A.B.C.* */
    private static String collapsePackage(String key) {
        final String[] packages = key.split("\\.");
        return (packages.length > 2 ? packages[0] + "." + packages[1] + "." + packages[2] :
                packages.length > 1 ? packages[0] + "." + packages[1] :
                        packages[0]) + ".*";
    }

    private static Collection<TypeSummary> findOrCreatePackage(Map<String, Collection<TypeSummary>> registry, String typePackage) {
        if (registry.containsKey(typePackage)) {
            return registry.get(typePackage);
        } else {
            final Collection<TypeSummary> result = new TreeSet<>();
            registry.put(typePackage, result);
            return result;
        }
    }
}
