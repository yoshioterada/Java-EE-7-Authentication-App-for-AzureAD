/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoshio3.azuread.cdis.extensionOfPF;

import com.yoshio3.azuread.entities.ADGroup;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author yoterada
 */
public class GroupSelectionModel extends ListDataModel<ADGroup> implements SelectableDataModel<ADGroup> {

    public GroupSelectionModel() {
    }

    public GroupSelectionModel(List<ADGroup> data) {
        super(data);
    }

    @Override
    public Object getRowKey(ADGroup group) {
        System.out.println("getRowKey() : " + group.getObjectId());
        return group.getObjectId();
    }

    @Override
    public ADGroup getRowData(String rowKey) {
        List<ADGroup> groups = (List<ADGroup>) getWrappedData();
        ADGroup group = null;
        for (ADGroup grp : groups) {
            if (grp.getObjectId().equals(rowKey)) {
                group = grp;
            }
        }
        return group;
    }

}
