/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.memory.binarystorage;

/**
 * Mutable object wrapper. Typical use case in Jet-Memory: broadcast a state change to
 * several collaborator objects. All collaborators get the same holder instance so that
 * changing the wrapped object is observed by all of them at once.
 *
 * @param <T> the wrapped object type
 */
public class ObjectHolder<T> {
    private T object;

    public T get(T defaultObject) {
        return object == null ? defaultObject : object;
    }

    public void set(T newObject) {
        object = newObject;
    }
}
