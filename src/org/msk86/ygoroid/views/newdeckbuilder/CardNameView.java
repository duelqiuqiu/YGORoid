package org.msk86.ygoroid.views.newdeckbuilder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.util.TypedValue;
import android.widget.TextView;
import org.msk86.ygoroid.R;
import org.msk86.ygoroid.newcore.impl.Card;
import org.msk86.ygoroid.newcore.impl.UserDefinedCard;

public class CardNameView extends TextView {
    private Card card;

    public CardNameView(Context context) {
        super(context);
        this.setSingleLine();
        this.setPadding(3, 3, 3, 3);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        XmlResourceParser xrp = getResources().getXml(R.color.card_name_view);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);
            this.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    public CardNameView(Context context, Card card) {
        this(context);
        this.card = card;
        String name = card.getName();
        if (card instanceof UserDefinedCard) {
            name = "?" + name;
        }
        this.setText(name);
    }

    public Card getCard() {
        return card;
    }
}
