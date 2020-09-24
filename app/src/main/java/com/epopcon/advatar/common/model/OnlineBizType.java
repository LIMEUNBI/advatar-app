package com.epopcon.advatar.common.model;

import android.content.Context;
import android.text.TextUtils;

import com.epopcon.advatar.R;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OnlineBizType {

    private Context context;

    private Map<String, String[]> groups = new LinkedHashMap<>();
    private Map<OnlineConstant, String[]> types = new LinkedHashMap<>();

    public OnlineBizType(Context context) {
        this.context = context;
        loadData();
    }

    public static Map<String, Integer> onlineStoreMap = new HashMap<>();
    static {
        onlineStoreMap.put("GMARKET", 100);
        onlineStoreMap.put("AUCTION", 101);
        onlineStoreMap.put("_11ST", 102);
        onlineStoreMap.put("NAVER", 103);
        onlineStoreMap.put("INTERPARK", 104);
        onlineStoreMap.put("LOTTE_COM", 105);
        onlineStoreMap.put("SSG", 106);
        onlineStoreMap.put("G9", 108);
        onlineStoreMap.put("AK_MALL", 109);
        onlineStoreMap.put("COUPANG", 114);
        onlineStoreMap.put("TMON", 115);
        onlineStoreMap.put("WEMAP", 116);
    }

    private void loadData() {
        String[] array;

        // 온라인상점 그룹 유형
        array = context.getResources().getStringArray(R.array.online_store_groups);
        for (String str : array) {
            String[] token = str.split(";");

            String groupCode = token[0];
            String groupName = token[1];

            groups.put(groupCode, new String[]{groupName});
        }

        // 온라인상점 유형
        array = context.getResources().getStringArray(R.array.online_store_types);
        for (String str : array) {
            String[] token = str.split(";");

            String code = token[0];
            String groupCode = token[1];
            String name = token[2];
            String searchName = token[3];
            String url = token[4];
            boolean enable = Boolean.parseBoolean(token[5]);

            if (enable)
                types.put(OnlineConstant.valueOf(code), new String[]{groupCode, name, searchName, url});
        }
    }

    public String groupName(String groupCode) {
        return groups.get(groupCode)[0];
    }

    public String name(OnlineConstant type) {
        String[] temp = types.get(type);

        if (temp == null)
            return null;
        return types.get(type)[1];
    }

    public String searchName(OnlineConstant type) {
        String[] temp = types.get(type);

        if (temp == null)
            return null;
        return types.get(type)[2];
    }

    public String url(OnlineConstant type) {
        String[] temp = types.get(type);

        if (temp == null)
            return null;
        return types.get(type)[3];
    }

    public List<String> groupCodes() {
        List<String> list = new ArrayList<>();

        Iterator<String> keys = groups.keySet().iterator();

        while (keys.hasNext())
            list.add(keys.next());

        return list;
    }

    public List<String[]> types() {
        List<String[]> list = new ArrayList<>();

        for (Map.Entry<OnlineConstant, String[]> entry : types.entrySet()) {
            OnlineConstant type = entry.getKey();
            String[] token = entry.getValue();

            String groupCode = token[0];
            String name = token[1];
            String url = token[3];

            list.add(new String[]{groupCode, type.toString(), name, url});
        }
        return list;
    }

    public List<OnlineConstant> getAvailableTypes() {
        List<OnlineConstant> list = new ArrayList<>();

        for (Map.Entry<OnlineConstant, String[]> entry : types.entrySet()) {
            OnlineConstant type = entry.getKey();

            if (OnlineDeliveryInquiryHelper.hasStoredIdAndPassword(context, type)) {
                list.add(type);
            }
        }
        return list;
    }

    public static OnlineConstant valueOf(String type) {
        try {
            if (!TextUtils.isEmpty(type))
                return OnlineConstant.valueOf(type);
        } catch (Exception e) {

        }
        return null;
    }
}
