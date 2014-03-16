package org.msk86.ygoroid.views.newdeckbuilder.filter;

import org.msk86.ygoroid.core.Attribute;

public class AttrFilter implements CardFilter {

    Attribute attr;

    public AttrFilter(Attribute attr) {
        this.attr = attr;
    }

    @Override
    public String where() {
        if (!isValid()) {
            return "";
        }
        return " AND d.attribute & " + attr.getCode() + " = " + attr.getCode();
    }

    @Override
    public boolean isValid() {
        return attr != Attribute.NULL;
    }
}
