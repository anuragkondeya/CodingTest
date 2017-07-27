package anuragkondeya.com.anuragkondeya.Fragments;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Fetch images from bitmap cache (if already downloaded) before making the network call
 */

class BitmapCache {

    private static LruCache<String, Bitmap> mMemoryCache;
    private static BitmapCache instance = null;
    private BitmapCache(){}

    /**
     * Init and get singleton instance of the BitmapCache class
     * @return
     */
    public static BitmapCache getInstance(){
        if(null == instance) {
            instance = new BitmapCache();
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
        }

        return instance;
    }

    /**
     * Add a bitmap to the cache
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Reterive a bitmap from the cache
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }






}
