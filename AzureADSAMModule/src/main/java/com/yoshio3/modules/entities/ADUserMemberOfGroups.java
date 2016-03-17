/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoshio3.modules.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yoterada
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
