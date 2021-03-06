package org.msk86.ygoroid.newcore.impl.builder;

import org.msk86.ygoroid.newcore.Container;
import org.msk86.ygoroid.newcore.Item;
import org.msk86.ygoroid.newcore.Layout;
import org.msk86.ygoroid.newcore.Renderer;
import org.msk86.ygoroid.newcore.deck.DeckCards;
import org.msk86.ygoroid.newcore.impl.CardList;
import org.msk86.ygoroid.newcore.impl.builder.renderer.MainDeckSectionRenderer;
import org.msk86.ygoroid.newcore.impl.layout.GridLayout;

public class MainDeckSection implements Item, Container {
    DeckCards cards;
    CardList mainDeck;
    Item holder;
    boolean highLight;

    public MainDeckSection(Item holder) {
        mainDeck = new CardList();
        this.holder = holder;
    }

    public void setCards(DeckCards cards) {
        this.cards = cards;
        mainDeck = this.cards.getMainDeckCards();
        if(layout != null) {
            layout.setItems(mainDeck.getCards());
        }
    }

    public Item getHolder() {
        return holder;
    }

    public boolean isHighLight() {
        return highLight;
    }

    public void setHighLight(boolean highLight) {
        this.highLight = highLight;
    }

    Renderer renderer;
    @Override
    public Renderer getRenderer() {
        if(renderer == null) {
            renderer = new MainDeckSectionRenderer(this);
        }
        return renderer;
    }

    GridLayout layout;
    @Override
    public Layout getLayout() {
        if(layout == null) {
            layout = new GridLayout(this, mainDeck.getCards());
        }
        return layout;
    }
}
