package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyIndicatorDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyIndicator;

public class PropertyIndicatorDaoImpl extends BaseDaoImpl<PropertyIndicator> implements PropertyIndicatorDao {
    public PropertyIndicatorDaoImpl() {
        super(PropertyIndicator.class);
    }
}
