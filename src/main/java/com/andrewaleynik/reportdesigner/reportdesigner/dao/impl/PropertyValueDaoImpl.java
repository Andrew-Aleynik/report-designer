package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyValueDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;

public class PropertyValueDaoImpl extends BaseDaoImpl<PropertyValue> implements PropertyValueDao {
    public PropertyValueDaoImpl() {
        super(PropertyValue.class);
    }
}
