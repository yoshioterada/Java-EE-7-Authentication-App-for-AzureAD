/*
* Copyright 2016 Yoshio Terada
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
package com.yoshio3.azuread.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
public class ADUserMemberOfGroups {
    @JsonProperty("odata.metadata")
    private String metadata;

    private String[] value;

    /**
     * @return the metadata
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the value
     */
    public String[] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ADUserMemberOfGroups{" + "metadata=" + metadata + ", value=" + Arrays.toString(value) + '}';
    }    
}
