package com.epopcon.advatar.common.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.util.Utils;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlBuilder {

    private final static String TAG = SqlBuilder.class.getSimpleName();

    private static SqlBuilder sqlBuilder;

    private Properties properties = new Properties();
    private Context context;

    SqlBuilder(Context context) {
        this.context = context;
    }

    public static void initialize(Context context) {
        if (sqlBuilder == null) {
            synchronized (SqlBuilder.class) {
                if (sqlBuilder == null) {
                    sqlBuilder = new SqlBuilder(context);
                    sqlBuilder.initialize();
                }
            }
        }
    }

    public static SqlBuilder getInstance() {
        return sqlBuilder;
    }

    private void initialize() {
        AssetManager assetManager = context.getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open("prop/sql.xml");
            properties.loadFromXML(stream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            Utils.closeQuietly(stream);
        }
    }

    public String getQueryString(String queryId, Object... params) {
        String query = properties.getProperty(queryId, "");
        if (!TextUtils.isEmpty(query)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String)
                    params[i] = ((String) params[i]).replaceAll("'", "''");
            }
            query = String.format(query, params);
        }
        return query;
    }

    public String getQueryString(String queryId, Map<String, String> params) {
        String query = properties.getProperty(queryId, "");
        if (!TextUtils.isEmpty(query)) {
            Pattern p = Pattern.compile("\\{\\{([^\\}]*)\\}\\}|\\[\\[([^\\}]*)\\]\\]");
            Matcher m = p.matcher(query);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                String key1 = m.group(1);
                String key2 = m.group(2);

                if (!TextUtils.isEmpty(key1))
                    key1 = key1.trim();
                if (!TextUtils.isEmpty(key2))
                    key2 = key2.trim();

                if (!TextUtils.isEmpty(key1) && params.containsKey(key1)) {
                    String value = params.get(key1);
                    if (!TextUtils.isEmpty(value))
                        value = value.replaceAll("'", "''");
                    else
                        value = "";

                    m.appendReplacement(sb, value);
                    continue;
                } else if (!TextUtils.isEmpty(key2) && params.containsKey(key2)) {
                    String value = params.get(key2);
                    if (TextUtils.isEmpty(value))
                        value = "";

                    m.appendReplacement(sb, value);
                    continue;
                }
                m.appendReplacement(sb, "");
            }
            m.appendTail(sb);
            return sb.toString();
        }
        return query;
    }

    public String getQueryString(String queryId, Map<String, String> params, boolean isReplaceStr) {
        String query = properties.getProperty(queryId, "");
        if (!TextUtils.isEmpty(query)) {
            Pattern p = Pattern.compile("\\{\\{([^\\}]*)\\}\\}|\\[\\[([^\\}]*)\\]\\]");
            Matcher m = p.matcher(query);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                String key1 = m.group(1);
                String key2 = m.group(2);

                if (!TextUtils.isEmpty(key1))
                    key1 = key1.trim();
                if (!TextUtils.isEmpty(key2))
                    key2 = key2.trim();

                if (!TextUtils.isEmpty(key1) && params.containsKey(key1)) {
                    String value = params.get(key1);
                    if (!TextUtils.isEmpty(value)) {
                        if (isReplaceStr)
                            value = value.replaceAll("'", "''");
                    }
                    else {
                        value = "";
                    }

                    m.appendReplacement(sb, value);
                    continue;
                } else if (!TextUtils.isEmpty(key2) && params.containsKey(key2)) {
                    String value = params.get(key2);
                    if (TextUtils.isEmpty(value))
                        value = "";

                    m.appendReplacement(sb, value);
                    continue;
                }
                m.appendReplacement(sb, "");
            }
            m.appendTail(sb);
            return sb.toString();
        }
        return query;
    }
}
