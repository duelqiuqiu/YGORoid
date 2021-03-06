package org.msk86.ygoroid.newcore.impl.layout;

import android.graphics.Point;
import org.msk86.ygoroid.newcore.Container;
import org.msk86.ygoroid.newcore.Item;
import org.msk86.ygoroid.newcore.Layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AbsoluteLayout implements Layout {
    Container container;
    List<Item> items;
    Map<Item, Point> positionMap;
    Map<Item, Integer> zIndexMap;
    private int offsetX;

    public AbsoluteLayout(Container container) {
        this.container = container;
        items = new CopyOnWriteArrayList<Item>();
        positionMap = new HashMap<Item, Point>();
        zIndexMap = new HashMap<Item, Integer>();
    }

    public void addItem(Item item, int x, int y) {
        addItem(item, x, y, 0);
    }

    public void addItem(Item item, int x, int y, int zIndex) {
        if(items.contains(item)) {
            return;
        }
        items.add(item);
        Point pos = new Point(offsetX + x, y);
        positionMap.put(item, pos);
        zIndexMap.put(item, zIndex);
    }

    public void removeItem(Item item) {
        items.remove(item);
        positionMap.remove(item);
        zIndexMap.remove(item);
    }

    public void removeItems(Class clazz) {
        for(Item item : items) {
            if(clazz.isInstance(item)) {
                removeItem(item);
            }
        }
    }

    @Override
    public List<? extends Item> items() {
        List<Item> zIndexList = new CopyOnWriteArrayList<Item>();
        for(int i=getMinZIndex();i<=getMaxZIndex();i++) {
            for (Map.Entry<Item, Integer> entry : zIndexMap.entrySet()) {
                if(entry.getValue() == i) {
                    zIndexList.add(entry.getKey());
                }
            }
        }
        return zIndexList;
    }

    private int getMaxZIndex() {
        int max = Integer.MIN_VALUE;
        for(Integer z : zIndexMap.values()) {
            if(z > max) {
                max = z;
            }
        }
        return max;
    }
    private int getMinZIndex() {
        int min = Integer.MAX_VALUE;
        for(Integer z : zIndexMap.values()) {
            if(z < min) {
                min = z;
            }
        }
        return min;
    }

    @Override
    public Item itemAt(int x, int y) {
        Item itemAtXY = null;
        for(Item item : items) {
            Point pos = positionMap.get(item);
            if(pos.x <= x && x < pos.x + item.getRenderer().size().width()
                    && pos.y <= y && y < pos.y + item.getRenderer().size().height()) {
                if(itemAtXY == null || zIndexMap.get(itemAtXY) < zIndexMap.get(item)) {
                    itemAtXY = item;
                }
            }
        }
        return itemAtXY;
    }

    @Override
    public Point itemPosition(Item item) {
        return positionMap.get(item);
    }

    public void setOffset(int offsetX, int i) {
        this.offsetX = offsetX;
    }
}
