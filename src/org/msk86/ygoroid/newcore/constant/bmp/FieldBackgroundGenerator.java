package org.msk86.ygoroid.newcore.constant.bmp;

import android.graphics.Bitmap;
import org.msk86.ygoroid.newcore.BmpGenerator;
import org.msk86.ygoroid.newcore.constant.FieldType;
import org.msk86.ygoroid.newcore.impl.Field;
import org.msk86.ygoroid.newutils.BmpReader;
import org.msk86.ygoroid.size.Size;

import java.util.HashMap;
import java.util.Map;

public class FieldBackgroundGenerator implements BmpGenerator {
    private Field field;
    private Map<Size, Bitmap> cache = new HashMap<Size, Bitmap>();

    public FieldBackgroundGenerator(Field field) {
        this.field = field;
    }

    @Override
    public Bitmap generate(Size size) {
        if(cache.get(size) == null) {
            cache.put(size, BmpReader.readBitmap(field.getBackgroundResId(), size));
        }
        return cache.get(size);
    }
}
