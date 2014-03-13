package org.msk86.ygoroid.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import org.msk86.ygoroid.YGOActivity;
import org.msk86.ygoroid.core.Drawable;
import org.msk86.ygoroid.sqlite.CardsDBHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public class Utils {
    private static DisplayMetrics dm;
    private static YGOActivity context;
    private static CardsDBHelper dbHelper;

    public static void initInstance(YGOActivity activity) {
        context = activity;
        dm = new DisplayMetrics();
        dbHelper = new CardsDBHelper(activity, 1);
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        checkFolders();
    }

    private static void checkFolders() {
        checkFolder(Configuration.baseDir(), false);
        checkFolder(Configuration.deckPath(), false);
        checkFolder(Configuration.cardImgPath(), true);
        checkFolder(Configuration.userDefinedCardImgPath(), true);
        checkFolder(Configuration.texturePath(), true);
    }

    private static void checkFolder(String path, boolean noMedia) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (noMedia) {
            File noMediaDir = new File(path + ".nomedia");
            if (!noMediaDir.exists()) {
                noMediaDir.mkdirs();
            }
        }
    }

    public static YGOActivity getContext() {
        return context;
    }

    public static CardsDBHelper getDbHelper() {
        return dbHelper;
    }

    public static int screenHeight() {
        return dm.heightPixels;
    }

    public static int screenWidth() {
        return dm.widthPixels;
    }

    public static int unitLength() {
        int longer = dm.widthPixels > dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        int shorter = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        int unitLengthW = (int) (longer / 6f);
        int unitLengthH = (int) (shorter / 3.95f);
        return unitLengthW < unitLengthH ? unitLengthW : unitLengthH;
    }

    public static int totalWidth() {
        return unitLength() * 6;
    }

    public static int deckBuilderWidth() {
        return screenWidth() * 3 / 4;
    }

    public static int cardScreenHeight() {
        return screenHeight();
    }

    public static int cardHeight() {
        int padding = 2;
        return unitLength() - padding * 2;
    }

    public static int cardSnapshotHeight() {
        return cardHeight() * 3 / 4;
    }

    public static int cardPreviewHeight() {
        return (int) (cardPreviewWidth() * 1.45);
    }

    public static int cardScreenWidth() {
        return (int) (screenHeight() / 1.45);
    }

    public static int cardWidth() {
        return (int) (cardHeight() / 1.45);
    }

    public static int cardSnapshotWidth() {
        return cardWidth() * 3 / 4;
    }

    public static int cardPreviewWidth() {
        return screenWidth() / 4;
    }

    public static int bigCardHeight() {
        return screenHeight();
    }

    public static int bigCardWidth() {
        return (int) (bigCardHeight() / 1.45);
    }

    private static int calculateSampleScale(BitmapFactory.Options options, int reqHeight) {
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        return inSampleSize;
    }

    private static int calculateSampleScale(int resId, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        return calculateSampleScale(options, targetHeight);
    }

    private static int calculateSampleScale(String file, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        return calculateSampleScale(options, targetHeight);
    }

    public static Bitmap readBitmapScaleByHeight(int resId, int targetHeight) {
        return readBitmapScaleByHeight(resId, targetHeight, Bitmap.Config.ARGB_4444);
    }

    public static Bitmap readBitmapScaleByHeight(int resId, int targetHeight, Bitmap.Config colorSample) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = colorSample;
            options.inSampleSize = calculateSampleScale(resId, targetHeight);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
            return scaleByHeight(bitmap, targetHeight, colorSample);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap readBitmapScaleByHeight(String file, int targetHeight) {
        return readBitmapScaleByHeight(file, targetHeight, Bitmap.Config.ARGB_4444);
    }

    public static Bitmap readBitmapScaleByHeight(String file, int targetHeight, Bitmap.Config colorSample) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = colorSample;
            options.inSampleSize = calculateSampleScale(file, targetHeight);
            Bitmap bitmap = BitmapFactory.decodeFile(file, options);
            return scaleByHeight(bitmap, targetHeight, colorSample);
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap readBitmapScaleByHeight(String zip, String innerFile, String extractFile, int targetHeight) {
        try {
            ZipReader.extractZipFile(zip, innerFile, extractFile);
            return readBitmapScaleByHeight(extractFile, targetHeight);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap readBitmapScaleByHeight(String innerFile, String extractFile, int targetHeight) {
        try {
            String[] zips = cardPicZips();
            for (String zip : zips) {
                Bitmap bmp = readBitmapScaleByHeight(Configuration.cardImgPath() + zip, innerFile, extractFile, targetHeight);
                if (bmp != null) {
                    return bmp;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String tempifyDeck(String deck) {
        return deck + "._tmp";
    }

    public static boolean isTempDeck(String deck) {
        return deck.endsWith("._tmp");
    }

    public static String untempifyDeck(String tempDeck) {
        if (isTempDeck(tempDeck)) {
            return tempDeck.substring(0, tempDeck.length() - 5);
        }
        return tempDeck;
    }

    public static void clearAllTempDeck() {
        File deckPath = new File(Configuration.deckPath());
        File[] tempDecks = deckPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return isTempDeck(name);
            }
        });
        for (File tempDeck : tempDecks) {
            tempDeck.delete();
        }
    }

    public static String findTempDeck() {
        File deckPath = new File(Configuration.deckPath());
        String[] tempDecks = deckPath.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return isTempDeck(name);
            }
        });
        return tempDecks.length == 1 ? tempDecks[0] : null;
    }

    public static String[] decks() {
        File deckPath = new File(Configuration.deckPath());
        return deckPath.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return !isTempDeck(name);
            }
        });
    }

    public static String[] cardPicZips() {
        File picsDir = new File(Configuration.cardImgPath());
        String[] zips = picsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.startsWith("pics") && name.endsWith(".zip");
            }
        });
        Arrays.sort(zips, new Comparator<String>() {
            @Override
            public int compare(String zip1, String zip2) {
                if (zip1.length() != zip2.length()) {
                    return zip2.length() - zip1.length();
                }
                for (int i = 0; i < zip1.length(); i++) {
                    if (zip1.charAt(i) != zip2.charAt(i)) {
                        return zip2.charAt(i) - zip1.charAt(i);
                    }
                }
                return 0;
            }
        });
        return zips;
    }

    public static String[] cardPicsInZip(String zip) {
        return ZipReader.listFile(zip, new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jpg");
            }
        });
    }

    public static String[] cardPics() {
        File picsDir = new File(Configuration.cardImgPath());
        return picsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jpg");
            }
        });
    }

    public static int countPics() {
        File picsDir = new File(Configuration.cardImgPath());
        String[] pics = picsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jpg");
            }
        });
        return pics.length;
    }

    public static Bitmap scaleByHeight(Bitmap bitmap, int targetHeight, Bitmap.Config colorSample) {
        Matrix matrix = new Matrix();

        float changeRate = targetHeight * 1.0f / bitmap.getHeight();
        matrix.postScale(changeRate, changeRate);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        if (colorSample == Bitmap.Config.ARGB_8888) {
            return newBitmap;
        } else {
            Bitmap sampledBmp = newBitmap.copy(colorSample, false);
            newBitmap.recycle();
            return sampledBmp;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postScale(1f, 1f);
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    public static final int DRAW_POSITION_FIRST = -0x1000;
    public static final int DRAW_POSITION_CENTER = -0x2000;
    public static final int DRAW_POSITION_LAST = -0x3000;

    public static void drawBitmapOnCanvas(Canvas canvas, Bitmap bitmap, Paint paint, int positionX, int positionY) {
        int posX = 0;
        int posY = 0;
        switch (positionX) {
            case DRAW_POSITION_FIRST:
                posX = 0;
                break;
            case DRAW_POSITION_CENTER:
                posX = (canvas.getWidth() - bitmap.getWidth()) / 2;
                break;
            case DRAW_POSITION_LAST:
                posX = canvas.getWidth() - bitmap.getWidth();
                break;
            default:
                posX = positionX;
        }
        switch (positionY) {
            case DRAW_POSITION_FIRST:
                posY = 0;
                break;
            case DRAW_POSITION_CENTER:
                posY = (canvas.getHeight() - bitmap.getHeight()) / 2;
                break;
            case DRAW_POSITION_LAST:
                posY = canvas.getHeight() - bitmap.getHeight();
                break;
            default:
                posY = positionY;
        }
        if (!bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, posX, posY, paint);
        }
    }

    public static void deleteDeck(String deckName) {
        File deckFile = new File(Configuration.deckPath() + deckName);
        if (deckFile.isFile() && deckFile.exists()) {
            deckFile.delete();
        }
    }

    public static class DrawHelper {
        private int x;
        private int y;

        public DrawHelper(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int center(int frameSize, int size) {
            return (frameSize - size) / 2;
        }

        public int bottom(int frameSize, int size) {
            return frameSize - size;
        }

        public void drawText(Canvas canvas, String str, int left, int top, Paint paint) {
            canvas.drawText(str, x + left, y + top, paint);
        }

        public void drawLine(Canvas canvas, int startX, int startY, int stopX, int stopY, Paint paint) {
            canvas.drawLine(x + startX, y + startY, x + stopX, y + stopY, paint);
        }

        public void drawCircle(Canvas canvas, int left, int top, int radius, Paint paint) {
            canvas.drawCircle(x + left, y + top, radius, paint);
        }

        public void drawRect(Canvas canvas, Rect r, Paint paint) {
            r.offset(x, y);
            canvas.drawRect(r, paint);
        }

        public void drawRoundRect(Canvas canvas, RectF rect, int rx, int ry, Paint paint) {
            rect.offset(x, y);
            canvas.drawRoundRect(rect, rx, ry, paint);
        }

        public void drawBitmap(Canvas canvas, Bitmap bitmap, int left, int top, Paint paint) {
            if (!bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, x + left, y + top, paint);
            }
        }

        public void drawDrawable(Canvas canvas, Drawable drawable, int left, int top) {
            drawable.draw(canvas, x + left, y + top);
        }

        public void drawLayout(Canvas canvas, Layout layout, int left, int top) {
            canvas.translate(x + left, y + top);
            layout.draw(canvas);
            canvas.translate(-x - left, -y - top);
        }
    }

    public static int getSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getVersion() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "0";
        }
    }

    public static String s(int id) {
        return context.getResources().getString(id);
    }

    public static String cutOneLine(String str, TextPaint textPaint, int width) {
        StaticLayout layout = new StaticLayout(str, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        if (layout.getLineCount() > 1) {
            str = str.substring(0, layout.getLineEnd(0)).trim();
        }
        return str;
    }

    public static String cutPages(String str, int page, TextPaint textPaint, int width, int height) {
        StaticLayout layout = new StaticLayout(str, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        int lineCount = layout.getLineCount();
        int linePerPage = lineCount;
        for (int i = 0; i < lineCount; i++) {
            if (layout.getLineTop(i) >= height) {
                linePerPage = i;
                break;
            }
        }
        int pages = (int) Math.ceil(lineCount * 1.0 / linePerPage);
        page = page % pages;
        int pageLineStart = page * linePerPage;
        int pageLineEnd = (page + 1) * linePerPage;
        pageLineEnd = pageLineEnd >= lineCount ? lineCount : pageLineEnd;
        str = str.substring(layout.getLineStart(pageLineStart), layout.getLineStart(pageLineEnd)).trim();
        return str;
    }

    public static boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
}
