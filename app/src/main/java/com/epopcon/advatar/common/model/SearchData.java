package com.epopcon.advatar.common.model;

import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;

import java.util.ArrayList;

public class SearchData {
    private static SearchData instance;
    private ArrayList<BrandGoodsRepo> _11stData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> coupangData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> wemapData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> tmonData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> gmarketData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> hmallData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> interParkData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> lotteData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> naverData = new ArrayList<>();
    private ArrayList<BrandGoodsRepo> ssgData = new ArrayList<>();

    private SearchData() {}

    public static SearchData getInstance() {
        if (instance == null) {
            instance = new SearchData();
        }
        return instance;
    }

    public ArrayList<BrandGoodsRepo> get_11stData() {
        return _11stData;
    }

    public void set_11stData(ArrayList<BrandGoodsRepo> _11stData) {
        this._11stData.addAll(_11stData);
    }

    public ArrayList<BrandGoodsRepo> getCoupangData() {
        return coupangData;
    }

    public void setCoupangData(ArrayList<BrandGoodsRepo> coupangData) {
        this.coupangData.addAll(coupangData);
    }

    public ArrayList<BrandGoodsRepo> getWemapData() {
        return wemapData;
    }

    public void setWemapData(ArrayList<BrandGoodsRepo> wemapData) {
        this.wemapData.addAll(wemapData);
    }

    public ArrayList<BrandGoodsRepo> getTmonData() {
        return tmonData;
    }

    public void setTmonData(ArrayList<BrandGoodsRepo> tmonData) {
        this.tmonData.addAll(tmonData);
    }

    public ArrayList<BrandGoodsRepo> getGmarketData() {
        return gmarketData;
    }

    public void setGmarketData(ArrayList<BrandGoodsRepo> gmarketData) {
//        this.gmarketData = gmarketData;
        this.gmarketData.addAll(gmarketData);
    }

    public ArrayList<BrandGoodsRepo> getHmallData() {
        return hmallData;
    }

    public void setHmallData(ArrayList<BrandGoodsRepo> hmallData) {
        this.hmallData.addAll(hmallData);
    }

    public ArrayList<BrandGoodsRepo> getInterParkData() {
        return interParkData;
    }

    public void setInterParkData(ArrayList<BrandGoodsRepo> interParkData) {
        this.interParkData.addAll(interParkData);
    }

    public ArrayList<BrandGoodsRepo> getLotteData() {
        return lotteData;
    }

    public void setLotteData(ArrayList<BrandGoodsRepo> lotteData) {
        this.lotteData.addAll(lotteData);
    }

    public ArrayList<BrandGoodsRepo> getNaverData() {
        return naverData;
    }

    public void setNaverData(ArrayList<BrandGoodsRepo> naverData) {
        this.naverData.addAll(naverData);
    }

    public ArrayList<BrandGoodsRepo> getSsgData() {
        return ssgData;
    }

    public void setSsgData(ArrayList<BrandGoodsRepo> ssgData) {
        this.ssgData.addAll(ssgData);
    }


    public void clear() {
        this._11stData = new ArrayList<>();
        this.coupangData = new ArrayList<>();
        this.gmarketData = new ArrayList<>();
        this.naverData = new ArrayList<>();
        this.interParkData = new ArrayList<>();
        this.ssgData = new ArrayList<>();
        this.tmonData = new ArrayList<>();
        this.wemapData = new ArrayList<>();
        this.hmallData = new ArrayList<>();
        this.lotteData = new ArrayList<>();
    }
}
