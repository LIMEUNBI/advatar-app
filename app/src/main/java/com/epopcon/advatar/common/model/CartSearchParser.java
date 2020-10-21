package com.epopcon.advatar.common.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CartSearchParser {

    public static List<CartSearchInfo> parseCartSearch(String content) {
        List<CartSearchInfo> cartSearchInfos = new ArrayList<>();
        Document doc = Jsoup.parse(content);

        if (doc.select("div.noResult_no_result__1ad0P").size() == 0) {
            Elements items = doc.select("ul.list_basis > div > div > li");

            for (int i = 0 ; i < items.size() ; i++) {
                CartSearchInfo cartSearchInfo = new CartSearchInfo();
                Element item = items.get(i);

                String dealUrl = item.select("div.thumbnail_thumb_wrap__1pEkS > a").attr("href");

                String title = item.select("div.basicList_title__3P9Q7 > a").text();
                int price = Integer.valueOf(item.select("span.price_num__2WUXn").text().replace(",", "").replace("원", ""));

                Elements cateList = item.select("div.basicList_depth__2QIie > a");
                String cate = "";
                for (int k = 0 ; k < cateList.size() ; k++) {
                    if (k == cateList.size() - 1) {
                        cate += cateList.get(k).text();
                    } else {
                        cate += cateList.get(k).text() + " > ";
                    }
                }

                String detail = item.select("span.basicList_event__fLNNU").text();

                Elements etcList = item.select("div.basicList_etc_box__1Jzg6 > a");
                int review = 0;
                int purchase = 0;
                for (int j = 0 ; j < etcList.size() ; j++) {
                    String type = etcList.get(j).attr("data-nclick");
                    if (type.contains("comment")) {
                        String star = etcList.get(j).select("span.basicList_star__3NKBn").text();
                        review = Integer.valueOf(etcList.get(j).text().replace("리뷰", "").replace(",", "").replace(star, ""));
                    } else if (type.contains("purchase")) {
                        purchase = Integer.valueOf(etcList.get(j).text().replace("구매건수", "").replace(",", ""));
                    }
                }

                String regDt = "";
                Elements etcList2 = item.select("span.basicList_etc__2uAYO");
                for (int l = 0 ; l < etcList2.size() ; l++) {
                    String etc = etcList2.get(l).text();
                    if (etc.contains("등록일")) {
                        regDt = etc.replace("등록일", "").trim();
                    }
                }

                String sellerName = item.select("div.basicList_mall_title__3MWFY > a").get(0).text();
                if (sellerName.equals("쇼핑몰별 최저가")) {
                    // 쇼핑몰별 정보 파싱 추가 (최대 5개)
                    try {
                        URL url = new URL(dealUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            String text = "";
                            while ((line = br.readLine()) != null) {
                                text += line;
                            }
                            getSellerParser(text, cartSearchInfo);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (sellerName.isEmpty()) {
                    sellerName = item.select("div.basicList_mall_title__3MWFY > a > img").attr("alt");
                }
                String delivery = "";
                Elements deli = item.select("em.basicList_option__3eF2s");
                if (deli.size() != 0) {
                    delivery = item.select("em.basicList_option__3eF2s").get(0).text();
                }

                cartSearchInfo.setProductName(title);
                cartSearchInfo.setProductUrl(dealUrl);
                cartSearchInfo.setProductPrice(price);
                cartSearchInfo.setProductCate(cate);
                cartSearchInfo.setProductDetail(detail);
                cartSearchInfo.setProductReviewCount(review);
                cartSearchInfo.setProductPurchaseCount(purchase);
                cartSearchInfo.setProductRegDt(regDt);
                cartSearchInfo.setSellerName(sellerName);
                cartSearchInfo.setDeliveryInfo(delivery);

                cartSearchInfos.add(cartSearchInfo);
            }
        }

        return cartSearchInfos;
    }

    public static CartSearchInfo getSellerParser(String content, CartSearchInfo cartSearchInfo) {

        Document doc = Jsoup.parse(content);
        Elements items = doc.select("div.productPerMall_item_inner__2W0Pu");

        String imgUrl = doc.select("div.simpleTop_thumb_area__14OSp > img").attr("src");
        cartSearchInfo.setProductImg(imgUrl);

        if (items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                String sellerName = items.get(i).select("span.productPerMall_mall__2hgpx").text();
                int sellerPrice = Integer.valueOf(items.get(i).select("span.productPerMall_price___KkW0 > em").text().replace(",", ""));
                String delivery = items.get(i).select("span.productPerMall_info_delivery_price__31apv").text();
                String sellerUrl = items.get(i).select("a").attr("href");

                if (i == 0) {
                    cartSearchInfo.setSellerStore1(sellerName);
                    cartSearchInfo.setSellerPrice1(sellerPrice);
                    cartSearchInfo.setSellerDelivery1(delivery);
                    cartSearchInfo.setSellerUrl1(sellerUrl);
                } else if (items.size() > 1 && i == 1) {
                    cartSearchInfo.setSellerStore2(sellerName);
                    cartSearchInfo.setSellerPrice2(sellerPrice);
                    cartSearchInfo.setSellerDelivery2(delivery);
                    cartSearchInfo.setSellerUrl2(sellerUrl);
                } else if (items.size() > 2 && i == 2) {
                    cartSearchInfo.setSellerStore3(sellerName);
                    cartSearchInfo.setSellerPrice3(sellerPrice);
                    cartSearchInfo.setSellerDelivery3(delivery);
                    cartSearchInfo.setSellerUrl3(sellerUrl);
                } else if (items.size() > 3 && i == 3) {
                    cartSearchInfo.setSellerStore4(sellerName);
                    cartSearchInfo.setSellerPrice4(sellerPrice);
                    cartSearchInfo.setSellerDelivery4(delivery);
                    cartSearchInfo.setSellerUrl4(sellerUrl);
                } else if (items.size() > 4 && i == 4) {
                    cartSearchInfo.setSellerStore5(sellerName);
                    cartSearchInfo.setSellerPrice5(sellerPrice);
                    cartSearchInfo.setSellerDelivery5(delivery);
                    cartSearchInfo.setSellerUrl5(sellerUrl);
                }

            }
        }

        return cartSearchInfo;
    }
}
