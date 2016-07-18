/**
 * Licensed to ESUP-Portail under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * ESUP-Portail licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.nfctagdroid.localstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.JavascriptInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalStorageJavaScriptInterface {
    private Context mContext;
    private LocalStorage localStorageDBHelper;
    private SQLiteDatabase database;
    private static final Logger log = LoggerFactory.getLogger(LocalStorageJavaScriptInterface.class);
    public LocalStorageJavaScriptInterface(Context c) {
        mContext = c;
        localStorageDBHelper = LocalStorage.getInstance(mContext);
    }

    /**
     * This method allows to get an item for the given key
     * @param key : the key to look for in the local storage
     * @return the item having the given key
     */
    @JavascriptInterface
    public String getItem(String key)
    {
        String value = null;
        if(key != null)
        {
            database = localStorageDBHelper.getReadableDatabase();
            Cursor cursor = database.query(LocalStorage.LOCALSTORAGE_TABLE_NAME,
                    null,
                    LocalStorage.LOCALSTORAGE_ID + " = ?",
                    new String [] {key},null, null, null);
            if(cursor.moveToFirst())
            {
                value = cursor.getString(1);
            }
            cursor.close();
            database.close();
        }
        return value;
    }

    /**
     * set the value for the given key, or create the set of datas if the key does not exist already.
     * @param key
     * @param value
     */
    @JavascriptInterface
    public void setItem(String key,String value)
    {
        log.info(key+" is set to : "+value);
        if(key != null && value != null)
        {
            String oldValue = getItem(key);
            database = localStorageDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LocalStorage.LOCALSTORAGE_ID, key);
            values.put(LocalStorage.LOCALSTORAGE_VALUE, value);
            if(oldValue != null)
            {
                database.update(LocalStorage.LOCALSTORAGE_TABLE_NAME, values, LocalStorage.LOCALSTORAGE_ID + "='" + key + "'", null);
            }
            else
            {
                database.insert(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, values);
            }
            database.close();
        }
    }

    /**
     * removes the item corresponding to the given key
     * @param key
     */
    @JavascriptInterface
    public void removeItem(String key)
    {
        if(key != null)
        {
            database = localStorageDBHelper.getWritableDatabase();
            database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, LocalStorage.LOCALSTORAGE_ID + "='" + key + "'", null);
            database.close();
        }
    }

    /**
     * clears all the local storage.
     */
    @JavascriptInterface
    public void clear()
    {
        database = localStorageDBHelper.getWritableDatabase();
        database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
        database.close();
    }
}