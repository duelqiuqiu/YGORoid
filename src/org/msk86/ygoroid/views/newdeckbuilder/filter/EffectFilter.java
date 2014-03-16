package org.msk86.ygoroid.views.newdeckbuilder.filter;

public class EffectFilter implements CardFilter {

    String[] effects = new String[0];

    public EffectFilter(String effect) {
        if (effect.length() > 0) {
            effects = effect.split("&");
        }
    }

    @Override
    public String where() {
        String w = "";
        for (String effect : effects) {
            w += " AND t.desc like '%" + effect + "%'";
        }
        return w;
    }

    @Override
    public boolean isValid() {
        return effects.length != 0;
    }
}
