package com.epopcon.advatar.common.model;

import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.model.repo.brand.BrandGoodsRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlineStoreParser {

    public static void getDataFor11st(String searchKeyword, RequestListener requestListener) throws Exception{
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8").replace("+", "").replace("%", "%25");

        String baseUrl = "http://search.11st.co.kr/Search.tmall?kwd=" + searchKeyword;
        List<BrandGoodsRepo> searchItems = new ArrayList<>();

        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Host", "search.11st.co.kr");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "euc-kr"));
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                text += line;
            }

            Document doc = Jsoup.parse(text);

            BrandGoodsRepo item = null;
            Elements elements = doc.select("div.search_content > script");
            String list = elements.get(0).toString().replace("<script charset=\"UTF-8\">\t\t\t\twindow.searchDataFactory.relatedKeywordsList = ", "")
                    .replace("};\t\t\t\twindow.searchDataFactory.nrCase = 'none';\t\twindow.searchDataFactory.pagiNation = {\t\t\ttotalPage: 3340,\t\t\tcurPage: 1\t\t}\t\t\t</script>", "");

            String[] products = list.split("window.searchDataFactory.");

            for (int i = 0 ; i < products.length ; i++) {
                if (products[i].contains("rcmdPrdList") || products[i].contains("focusPrdList") || products[i].contains("powerPrdList") ||
                        products[i].contains("plusPrdList") || products[i].contains("commonPrdList")) {
                    String product = products[i].substring(products[i].indexOf("{")).replace(";", "");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(product);
                    int count = jsonObject.get("count").getAsInt();
                    if (count > 0) {
                        JsonArray items = (JsonArray) jsonObject.get("items");
                        for (int j = 0 ; j < items.size() ; j++) {
                            item = new BrandGoodsRepo();
                            JsonObject prod = (JsonObject) items.get(j);

                            item.siteName = "11st";
                            item.goodsImg = prod.get("imageUrl").getAsString();
                            item.goodsName = prod.get("prdNm").getAsString();
                            item.goodsPrice = Integer.valueOf(prod.get("finalPrc").getAsString().replace(",", ""));
                            item.url = prod.get("productDetailUrl").getAsString();
                            item.deliveryInfo = prod.get("deliveryPriceText").getAsString();

                            searchItems.add(item);
                        }
                    }
                }
            }

            requestListener.onRequestSuccess(0, searchItems);
        } else {
            requestListener.onRequestFailure(new Throwable());
        }
    }

    public static void getDataForGmarket(String searchKeyword, RequestListener requestListener) throws Exception{

        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");
        List<BrandGoodsRepo> searchItems = new ArrayList<>();

        String baseUrl = "http://browse.gmarket.co.kr/search?keyword=" + searchKeyword;

        URL url = new URL(baseUrl);
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

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(text);

            Elements elements = doc.select("#section__inner-content-body-container > .section__module-wrap > .box__component.box__component-itemcard");

            for (Element element : elements) {
                BrandGoodsRepo item = new BrandGoodsRepo();
                item.siteName = "Gmarket";

                Element boxContainer = element.select(".box__item-container").first();

                Element majorInfo = boxContainer.select(".box__information > .box__information-major > .box__item-title > .text__item-title.text__item-title--ellipsis > a").first();
                String itemNum = majorInfo.attr("data-montelena-goodscode");
                item.goodsName = majorInfo.select(".text__item").attr("title");
                item.url = majorInfo.attr("href");

                item.goodsImg = "http://gdimg.gmarket.co.kr/" + itemNum + "/still/280?ver=1577681274";
                Element priceInfo = boxContainer.select(".box__information > .box__information-major > .box__item-price").first();
                String price = null;
                if (!priceInfo.select(".box__price-seller").equals(null)) {
                    price = priceInfo.select(".box__price-seller > .text.text__value").text();
                } else {
                    price = priceInfo.select(".box__price-original > .text.text__value").text();
                }

                item.goodsPrice = Integer.parseInt(price.replace(",", ""));
                searchItems.add(item);
            }

            requestListener.onRequestSuccess(0, searchItems);
        }
    }

    public static void getDataForNaver(String searchKeyword, RequestListener requestListener) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");
        String baseUrl = "https://search.shopping.naver.com/search/all.nhn?query=" + searchKeyword;
        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        connection.setRequestProperty("Host", "search.shopping.naver.com");

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                text += line;
            }

            Document doc = Jsoup.parse(text);

            Map<String, String> parserTagMap = new HashMap<>();
            parserTagMap.put("NORMAL_PRODUCT", ".goods_list > li");

            List<BrandGoodsRepo> searchItemList = new ArrayList<>();
            BrandGoodsRepo searchItemDto = null;

            for (String key : parserTagMap.keySet()) {
                Elements elements = doc.select(parserTagMap.get(key));

                int rank = 1;
                for (Element element : elements) {
                    searchItemDto = new BrandGoodsRepo();
                    searchItemDto.siteName = "Naver";
                    searchItemDto.goodsImg = element.select(".img_area > a > img").attr("data-original");

                    String itemName = "";
                    if (TextUtils.isEmpty(element.select(".tit > a").attr("title"))) {
                        itemName = element.select(".tit > a").text();
                    } else {
                        itemName = element.select(".tit > a").attr("title");
                    }

                    searchItemDto.goodsName = itemName;
                    searchItemDto.url = element.select(".img_area > a").attr("href");

                    String price = "";
                    if (element.select("._price_reload").html().equals("")) {
                        price = element.select(".num").text();
                    } else {
                        price = element.select("._price_reload").html();
                    }

                    searchItemDto.goodsPrice = Integer.parseInt(price.replace(",", ""));

                    String naverYn = element.select("._btn_shopping_detail").attr("data-is-shop-n");

                    if (naverYn != null && naverYn.equals("true")) {
                        searchItemList.add(searchItemDto);
                        rank++;
                    }
                }
            }
            requestListener.onRequestSuccess(0, searchItemList);
        } else {
            requestListener.onRequestFailure(new Throwable());
        }
    }

    public static void getDataForCoupang(String searchKeyword, RequestListener requestListener) throws Exception {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        final String baseUrl = "https://www.coupang.com/np/search?component=&q=" + searchKeyword + "&channel=user";
        final List<BrandGoodsRepo> searchItems = new ArrayList<>();

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Host", "www.coupang.com");
            connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            connection.setRequestProperty("Referer", "https://www.coupang.com/");
            connection.setRequestProperty("Cookie", "PCID=15634192429867390736297; gd1=Y; _fbp=fb.1.1590473819173.1916561863; _ga=GA1.2.1109070188.1599122297; _gcl_au=1.1.1159608435.1603696397; forterToken=66732809a1cf4eceab8ea952b9f81592_1603774864392__UDF43_11ck; TR10147805483_t_uid=51575717020223262.1603774864959; TR10147805483_t_sst=51575774800001223.1603774864959; TR10147805483_t_if=15.0.0.0.null.null.null.0; sc_vid=A00198349; sc_lid=salesepopcon; pdt-boecn=LxNLtjkcV%2FeszWhunPPtkcUS%2F4tW%2BOC4OYdWxp7u%2FHzvsNSrimndFIhT90rB3ErYEXdoj8vzCXiVOW%2Fceu%2F9WFLsmfRzLv%2FsOjM5fBtOfNDO34XjqsAKiUP4D%2BR8gQQM; sid=bee9dbb84ea74a2c897dfcc1c5d8f75bf3ad2c4b; overrideAbTestGroup=%5B%5D; baby-isWide=small; ak_bmsc=5327A6651F78D347C6FB305AC6B16229173502547F7C0000230BA95F3EAFC07F~plObZY/ce9DwXBmjFTArc6mDZjagE4aQf0ICCeuOcJutDv6hrAJzS0vsobtVOZ3XWTEidhqWzx150UpotIQS7Lpu6zd4YebjKuBR7/vhNFAVON6S5B9PDpUqRXM1eXv8oWBGdA5zNGh3iV/GtGMsQ5xJoG5aipkY/EXaK/CrOJEfeOrUuqMwp25FKZ1NOXapF3zaUgwsVU3CiEf1R7+z6ZhoweyivxZCMEAbjlDYQD56ZuuH7XT/2ife+Wyz2ho0d/; searchKeyword=%EC%BF%A0%EC%85%98; searchKeywordType=%7B%22%EC%BF%A0%EC%85%98%22%3A0%7D; FUN=\"{'search':[{'reqUrl':'/search.pang','isValid':true}]}\"; bm_sv=8908F6E3195FC745B608FD674ECEE2D8~djBf742hvcrLEBu1Lpd8kPCAzFqpsJtZh/Zbkg7AN8N/UffZdg+FZcnCKiHeH6pijuqhz5vvP/8lAFPlvO69frg5mOBQuG/EKg6rKuNWkfGzbDU0OmyiScYEdCOusAcMa08AInbg85RxAL4A/TXeAajzt3CSCBwOWk/zFkOigts=");
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String text = "";
                while ((line = br.readLine()) != null) {
                    text += line;
                }

                Document doc = Jsoup.parse(text);

                Map<String, String> parserTagMap = new HashMap<>();
                parserTagMap.put("NORMAL_PRODUCT", "#productList li");

                BrandGoodsRepo item = null;

                String host = "https://www.coupang.com";
                for (String key : parserTagMap.keySet()) {
                    Elements elements = doc.select(parserTagMap.get(key));

                    for (Element element : elements) {
                        item = new BrandGoodsRepo();
                        item.siteName = "Coupang";
                        String imgSrc = element.select(".search-product-wrap-img").attr("data-img-src");
                        if (TextUtils.isEmpty(imgSrc)) {
                            imgSrc = element.select(".search-product-wrap-img").attr("src");
                        }
                        item.goodsImg = "https:" + imgSrc;
                        item.goodsName = element.select(".name").html();
                        item.url = host + element.select("a").attr("href");
                        item.goodsPrice = Integer.parseInt(element.select(".price-value").get(0).html().replace(",", ""));

                        searchItems.add(item);
                    }
                }
                requestListener.onRequestSuccess(0, searchItems);
            } else {
                requestListener.onRequestFailure(new Throwable());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDataForTmon(String searchKeyword, RequestListener requestListener) throws IOException, ParseException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        String baseUrl = "http://search.tmon.co.kr/api/search/v4/deals?keyword=" + searchKeyword + "&useTypoCorrection=true&mainDealOnly=true&page=1&sortType=POPULAR";
        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        connection.setRequestProperty("Host", "search.tmon.co.kr");

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                text += line;
            }

            List<BrandGoodsRepo> searchItemList = new ArrayList<>();
            BrandGoodsRepo searchItemDto = null;

            JsonParser parser = new JsonParser();
            Object obj = parser.parse(text);

            JsonObject jsonObject = (JsonObject) obj;
            JsonObject data = (JsonObject) jsonObject.get("data");
            JsonArray searchDeals = (JsonArray) data.get("searchDeals");

            for (int i = 0; i < searchDeals.size(); i++) {
                searchItemDto = new BrandGoodsRepo();
                JsonObject searchDeals_i = (JsonObject) searchDeals.get(i);
                JsonObject searchDealResponse = (JsonObject) searchDeals_i.get("searchDealResponse");
                JsonObject dealInfo = (JsonObject) searchDealResponse.get("dealInfo");
                searchItemDto.siteName = "Tmon";
                searchItemDto.goodsName = dealInfo.get("titleName").toString();
                JsonObject imageInfo = (JsonObject) dealInfo.get("imageInfo");
                searchItemDto.goodsImg = imageInfo.get("mobile3ColImageUrl").toString();
                JsonObject priceInfo = (JsonObject) dealInfo.get("priceInfo");
                searchItemDto.goodsPrice = Integer.parseInt(priceInfo.get("price").toString());
                JsonObject extraDealInfo = (JsonObject) searchDeals_i.get("extraDealInfo");
                searchItemDto.url = extraDealInfo.get("detailUrl").toString();
                searchItemList.add(searchItemDto);
            }
            requestListener.onRequestSuccess(0, searchItemList);
        } else {
            requestListener.onRequestFailure(new Throwable());
        }
    }

    public static void getDataForWemap(String searchKeyword, RequestListener requestListener) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");
        String baseUrl = "https://search.wemakeprice.com/search?search_cate=top&keyword="+ searchKeyword+"&isRec=1";

        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        connection.setRequestProperty("Host", "search.wemakeprice.com");

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                text += line;
            }

            Document doc = Jsoup.parse(text);

            Map<String, String> parserTagMap = new HashMap<>();
            parserTagMap.put("NORMAL_PRODUCT", ".search_box_listwrap.tab_cont > div > a");

            List<BrandGoodsRepo> searchItemList = new ArrayList<>();
            BrandGoodsRepo searchItemDto = null;

            for (String key : parserTagMap.keySet()) {
                Elements elements = doc.select(parserTagMap.get(key));

                int rank = 1;
                for (Element element : elements) {
                    searchItemDto = new BrandGoodsRepo();
                    searchItemDto.siteName = "Wemap";
                    searchItemDto.url = "https:" + element.attr("href");
                    searchItemDto.goodsName = element.select(".conts_wrap > .item_img > img").attr("alt");
                    searchItemDto.goodsImg = element.select(".conts_wrap > .item_img > img").attr("data-lazy-src");

                    String tempPrice = element.select(".conts_wrap > .item_cont > .option_txt > .txt_price > .price_info").text().replace(",", "");
                    Pattern pattern = Pattern.compile("\\d+(?=원)");
                    Matcher matcher = pattern.matcher(tempPrice);
                    if (matcher.find()) {
                        searchItemDto.goodsPrice = Integer.parseInt(matcher.group());
                    }

                    searchItemList.add(searchItemDto);
                    rank++;
                }
            }
            requestListener.onRequestSuccess(0, searchItemList);
        } else {
            requestListener.onRequestFailure(new Throwable());
        }
    }

    public List<BrandGoodsRepo> getDataForSSG(String searchKeyword) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        HttpPost http = new HttpPost("http://www.ssg.com/search.ssg?target=all&query=" + searchKeyword);
        http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        http.addHeader("Accept-Encoding", "gzip, deflate");
        http.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        http.addHeader("Host", "www.ssg.com");

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(http);

        HttpEntity entity = response.getEntity();

        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while((line=br.readLine()) != null){
            sb.append(line+"\n");
        }

        Document doc = Jsoup.parse(sb.toString());

        Map<String, String> parserTagMap = new HashMap<>();
        parserTagMap.put("NORMAL_PRODUCT", "#idProductImg > li");

        List<BrandGoodsRepo> searchItemList = new ArrayList<>();
        BrandGoodsRepo searchItemDto = null;

        for (String key : parserTagMap.keySet()) {
            Elements elements = doc.select(parserTagMap.get(key));

            int rank = 1;
            for (Element element : elements) {
                searchItemDto = new BrandGoodsRepo();
                searchItemDto.siteName = "SSG";
                searchItemDto.goodsImg = "http:" + element.select(".thmb > a > img").attr("src");
                searchItemDto.goodsName= element.select(".title > a > em").get(0).text();
                searchItemDto.url = "http://www.ssg.com" + element.select(".title > a").attr("href");
                searchItemDto.goodsPrice = Integer.parseInt(element.select(".opt_price > .ssg_price").html().replace(",", ""));

                searchItemList.add(searchItemDto);
                rank++;
            }
        }
        return searchItemList;
    }



    public List<BrandGoodsRepo> getDataForHmall(String searchKeyword) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        HttpPost http = new HttpPost("https://www.hyundaihmall.com/front/pde/search.do?searchTerm=" + searchKeyword + "&gnbSearchYn=Y");
        http.addHeader("Cookie", "ck_nfn_p=check");

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(http);

        HttpEntity entity = response.getEntity();

        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while((line=br.readLine()) != null){
            sb.append(line+"\n");
        }

        Document doc = Jsoup.parse(sb.toString());

        Map<String, String> parserTagMap = new HashMap<>();
        parserTagMap.put("NORMAL_PRODUCT", ".pl_main_category_tabcontents > ._active > ul > li");

        List<BrandGoodsRepo> searchItemList = new ArrayList<>();
        BrandGoodsRepo searchItemDto = null;

        for (String key : parserTagMap.keySet()) {
            Elements elements = doc.select(parserTagMap.get(key));

            int rank = 1;
            for (Element element : elements) {
                searchItemDto = new BrandGoodsRepo();
                searchItemDto.siteName = "Hmall";
                String data = element.attr("data-thumb");
                String itemNum = "";
                try {
                    JsonParser parser = new JsonParser();
                    Object obj = parser.parse(data);
                    JsonObject jsonObject = (JsonObject) obj;
                    itemNum = jsonObject.get("slitmCd").toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                searchItemDto.goodsImg = element.select(".pl_itemlist_img > img").attr("src");
                searchItemDto.goodsName = element.select("a").attr("title");
                searchItemDto.goodsImg = "https://www.hyundaihmall.com/front/pda/itemPtc.do?slitmCd=" + itemNum;
                searchItemDto.goodsPrice = Integer.parseInt(element.select(".pl_item_price_benefit > em").html().replace(",", ""));

                searchItemList.add(searchItemDto);
                rank++;
            }
        }
        return searchItemList;
    }

    public List<BrandGoodsRepo> getDataForInterpark(String searchKeyword) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        HttpPost http = new HttpPost("http://isearch.interpark.com/isearch?q=" + searchKeyword);
        http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        http.addHeader("Accept-Encoding", "gzip, deflate");
        http.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        http.addHeader("Host", "isearch.interpark.com");

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(http);

        HttpEntity entity = response.getEntity();

        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while((line=br.readLine()) != null){
            sb.append(line+"\n");
        }

        Document doc = Jsoup.parse(sb.toString());

        Map<String, String> parserTagMap = new HashMap<>();
        parserTagMap.put("AD_PRODUCT", "#_ssenPickListLi > li");
        parserTagMap.put("NORMAL_PRODUCT", "#_SHOPListLi > li");

        List<BrandGoodsRepo> searchItemList = new ArrayList<>();
        BrandGoodsRepo searchItemDto = null;

        for (String key : parserTagMap.keySet()) {
            Elements elements = doc.select(parserTagMap.get(key));

            for (Element element : elements) {
                searchItemDto = new BrandGoodsRepo();
                searchItemDto.siteName= "Interpark";

                searchItemDto.goodsImg = element.select(".imgBox > a > img").attr("data-src");
                searchItemDto.goodsName = element.select(".productResultList > .info > a").text();
                searchItemDto.url = element.select(".imgBox > a").attr("href");
                searchItemDto.goodsPrice = Integer.parseInt(element.select(".number").html().replace(",", ""));

                searchItemList.add(searchItemDto);
            }
        }
        return searchItemList;
    }

    public List<BrandGoodsRepo> getDataForLotte(String searchKeyword) throws IOException {
        searchKeyword = URLEncoder.encode(searchKeyword, "UTF-8");

        HttpPost http = new HttpPost("http://www.lotte.com/search/searchMain.lotte?tq=&PAGECOUNT=84&init=Y&requery=" + searchKeyword + "&MULTISHOPCATES=LVL1_CATE_NO&SRCHFIELD=&tracking=MH_SEARCH&imgSearchVal=%2Fplanshop%2FviewPlanShopDetail.lotte%3Fspdp_no%3D5493378%26tracking%3DMH_SEARCH_01__PLANSHOP&dpml_no=1&site_no=1&query=" + searchKeyword);
        http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        http.addHeader("Accept-Encoding", "gzip, deflate");
        http.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        http.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        http.addHeader("Host", "www.lotte.com");

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(http);

        HttpEntity entity = response.getEntity();

        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while((line=br.readLine()) != null){
            sb.append(line+"\n");
        }

        Document doc = Jsoup.parse(sb.toString());

        Map<String, String> parserTagMap = new HashMap<>();
        parserTagMap.put("NORMAL_PRODUCT", ".product_typeA > ul > li");

        List<BrandGoodsRepo> searchItemList = new ArrayList<>();
        BrandGoodsRepo searchItemDto = null;

        for (String key : parserTagMap.keySet()) {
            Elements elements = doc.select(parserTagMap.get(key));

            int rank = 1;
            for (Element element : elements) {
                if (element.className().equals("relate_sch_li")) {
                    continue;
                }
                searchItemDto = new BrandGoodsRepo();
                searchItemDto.siteName = "Lotte";
                searchItemDto.goodsImg = element.select(".photo_zone > a > img").attr("src");
                searchItemDto.goodsName = element.select(".contents > p > a").text();

                String target = element.select("div.photo_zone > div").attr("onmouseover");
                String[] targets = target.split(",");
                String targetUrl = targets[targets.length -1].replace("'", "").replace(");", "");

                searchItemDto.url = "https://lotte.com" + targetUrl;
                searchItemDto.goodsPrice = Integer.parseInt(element.select(".finalPrice > strong").html()
                        .replace(",", "").replace("원", "").replace("~", ""));

                searchItemList.add(searchItemDto);
                rank++;
            }
        }
        return searchItemList;
    }
}
