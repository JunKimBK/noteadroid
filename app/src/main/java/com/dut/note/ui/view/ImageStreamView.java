package com.dut.note.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.dut.note.lib.FileUtil;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.net.sync.ISync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageStreamView extends ImageView{
    private volatile static List<Bitmap> bitmaps = new ArrayList<>();
    private static Object lock = new Object();

    public static void disposeStaticResources() {
        synchronized (lock) {
            for (Bitmap bitmap : bitmaps) {
                bitmap.recycle();
            }
            bitmaps.clear();
        }
    }

    private Bitmap bitmap;
    private byte[] buffer;
    private boolean loaded = false;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                bitmap = (Bitmap) msg.obj;
                setImageBitmap(bitmap);
            } else if (msg.what == 2) {
                if (getWidth() > 0 && getHeight() > 0) {
                    beginDrawAsync(getWidth(), getHeight());
                }
            }
            return true;
        }
    });

    private BitmapFactory.Options getImageBound(byte[] buff) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(buff, 0, buff.length, options);
        return options;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmap(byte[] buff,int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = getImageBound(buff);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(buff, 0, buff.length, options);
    }

    private void beginDrawAsync(final int w, final int h) {
        if (!loaded && buffer != null) {
            loaded = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = decodeSampledBitmap(buffer, w, h);
                    synchronized (lock) {
                        bitmaps.add(bitmap);
                    }
                    buffer = null;
                    Message msg = handler.obtainMessage(1, bitmap);
                    handler.sendMessage(msg);
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        beginDrawAsync(w, h);
    }

    public void setSyncImageResource(final String name, final ISync sync) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setImageStreamAsync(sync.getFileForRead(name));
            }
        }).start();
        bitmap = null;
    }

    public void setImageStreamAsync(final FileInputStreamSync ownInput) {
        if (ownInput == null )
            return;
        byte[] buff = FileUtil.readAllBytes(ownInput.getInputStream());
        ownInput.close();
        if (buff != null) {
            buffer = buff;
            handler.sendEmptyMessage(2);
        }
    }

    public void setImageFileAsync(String fileName) {
        setImageStreamAsync(new FileInputSync(fileName));
    }

    public ImageStreamView(Context context) {
        super(context);
    }

    public ImageStreamView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageStreamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        synchronized (lock) {
            if (bitmap != null) {
                bitmaps.remove(bitmap);
                bitmap.recycle();
                bitmap = null;

            }
        }
        super.onDetachedFromWindow();
    }

    private class FileInputSync extends FileInputStreamSync {
        public FileInputSync(String fileName) {
            try {
                mStream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
