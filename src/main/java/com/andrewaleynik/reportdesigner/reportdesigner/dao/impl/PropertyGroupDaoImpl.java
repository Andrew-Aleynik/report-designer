package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyGroupDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;

public class PropertyGroupDaoImpl extends BaseDaoImpl<PropertyGroup> implements PropertyGroupDao {

    public PropertyGroupDaoImpl() {
        super(PropertyGroup.class);
    }
}
