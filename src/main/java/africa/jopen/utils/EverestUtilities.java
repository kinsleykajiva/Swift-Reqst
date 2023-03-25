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

package africa.jopen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.UrlEscapers;

public class EverestUtilities {
    public static ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    /**
     * Removes leading and trailing quotation marks from strings.
     *
     * @param input String with leading and trailing quotation marks.
     * @return trimmedString - String with leading and trailing quotation marks removed.
     */
    public static String trimString(String input) {
        return input.replaceAll("\"", "");
    }

    public static String encodeURL(String url) {
        return UrlEscapers.urlFragmentEscaper().escape(url);
    }



}
