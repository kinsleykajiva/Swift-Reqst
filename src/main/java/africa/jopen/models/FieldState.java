/*
 * Copyright 2018 Rohit Awate.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.jopen.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Convenience class to represent the state of StringKeyValueFieldController and FileKeyValueField for
 * application state maintenance logic.
 */
public class FieldState {
    public String key;
    public String value;
    public boolean checked;

    public FieldState() {
        this.key = null;
        this.value = null;
        this.checked = false;
    }

    public FieldState(String key, String value, boolean checked) {
        this.key = key;
        this.value = value;
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldState that = (FieldState) o;
        return checked == that.checked &&
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return key.isEmpty() && value.isEmpty();
    }
}
