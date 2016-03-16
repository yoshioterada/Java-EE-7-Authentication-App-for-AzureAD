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
public class ADGroups {
    @JsonProperty("odata.metadata")
    private String metadata;
    @JsonProperty("odata.type")
    private String odata_type;
    private ADGroup[] value;

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
     * @return the odata_type
     */
    public String getOdata_type() {
        return odata_type;
    }

    /**
     * @param odata_type the odata_type to set
     */
    public void setOdata_type(String odata_type) {
        this.odata_type = odata_type;
    }

    /**
     * @return the value
     */
    public ADGroup[] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(ADGroup[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ADGroups{" + "metadata=" + metadata + ", odata_type=" + odata_type + ", value=" + Arrays.toString(value) + '}';
    }
}
