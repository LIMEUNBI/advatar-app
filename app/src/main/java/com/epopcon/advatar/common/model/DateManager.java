package com.epopcon.advatar.common.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 월별 이동에 사용되는 날짜 유틸리티 클래스
 */
public class DateManager {

    private static final String TAG = DateManager.class.getSimpleName();

    /**
     * 현재 선택된 월의 구분값
     * @see #getCurrentMonthType()
     */
    public static final int BEFORE_MONTH = -1;
    /**
     * 현재 선택된 월의 구분값
     * @see #getCurrentMonthType()
     */
    public static final int THIS_MONTH = 0;
    /**
     * 현재 선택된 월의 구분값
     * @see #getCurrentMonthType()
     */
    public static final int AFTER_MONTH = 1;

    /**
     * 액티비티별로 월별 이동시마다 시간값의 저장이 각각 필요한 경우 사용
     */
    private Map<Integer, Long> saveMonthMap = new HashMap<>();
    private static int DATE_TYPE_PERIOD = 0x0100;
    private static int DATE_TYPE_START  = 0x0200;
    private static int DATE_TYPE_END    = 0x0400;

    /**
     * 액티비티별로 월별 이동시마다 시간값의 저장이 필요한 경우 사용
     * 추가 시, saveActivityList 에도 추가하여야 함
     */
    public static int SAVE_ALL = 0x0000;
    public static int SAVE_DASHBOARD = 0x0001;
    public static int SAVE_TIME_1 = 0x0002;
    public static int SAVE_TIME_2 = 0x0003; // 나의 카드 화면에서 항상 현재 월의 금액을 가져오기 위해 사용함
    public static int SAVE_TIME_3 = 0x0004;
    public static int SAVE_TIME_4 = 0x0005;
    public static int SAVE_TIME_5 = 0x0006;

    private ArrayList<Integer> saveActivityList = new ArrayList<>();
    {
        saveActivityList.add(SAVE_DASHBOARD);
        saveActivityList.add(SAVE_TIME_1);
        saveActivityList.add(SAVE_TIME_2);
        saveActivityList.add(SAVE_TIME_3);
        saveActivityList.add(SAVE_TIME_4);
        saveActivityList.add(SAVE_TIME_5);
    }

    /**
     * Default Date format
     */
    private Map<String, SimpleDateFormat> simpleDateFormat = new HashMap<>();
    {
        simpleDateFormat.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"));
        simpleDateFormat.put("yy-MM-dd", new SimpleDateFormat("yy-MM-dd"));
        simpleDateFormat.put("yyyy.MM.dd", new SimpleDateFormat("yyyy.MM.dd"));
        simpleDateFormat.put("yy.MM.dd", new SimpleDateFormat("yy.MM.dd"));
        simpleDateFormat.put("yyyyMMdd", new SimpleDateFormat("yyyyMMdd"));
    }

    private static DateManager instance;

    private Calendar calNow;
    private Calendar calPeriod;
    private Calendar calStart;
    private Calendar calEnd;
    // 옵션에 의해 기준일(calPeriod) 이 변경되기 때문에..
    // 현재 시간이 calNow 는 변경하지 않고 새로운 현재시간 기준의 cal 을 새로 추가함.
    // 월별 이동 시, 해당 시간값도 +- 로 이동함.
    // setMonth() 에서 기준일 설정 시, 현재일을 기준으로 전월 또는 차월로 변경하기 위한 기준값으로 사용.
    private Calendar calNowPeriodBase;

    /**
     * DateManager 생성자
     * @return
     */
    public static synchronized DateManager getInstance() {
        if (instance == null) {
            instance = new DateManager();
        }
        return instance;
    }

    private DateManager() {
        calNow = Calendar.getInstance();
        calPeriod = Calendar.getInstance();
        calStart = Calendar.getInstance();
        calEnd = Calendar.getInstance();
        calNowPeriodBase = Calendar.getInstance();

        setDefaultDate(SAVE_ALL);
    }

    private void setDefaultDate(int saveActivity) {
        int periodDate = 1;

        /*boolean isDefaultPeriodMode = Utils.getPrefBoolean(Utils.getApplicationContext(), Config.SETTING_PERIOD_MODE, true);
        if (isDefaultPeriodMode == false) {
            periodDate = Utils.getPrefInt(Utils.getApplicationContext(), Config.SETTING_PERIOD_DATE, 1);
        }*/

        setPeriodDate(periodDate);

        setMonth(periodDate);

        setStartDate(periodDate);
        setEndDate(periodDate);

        if (saveActivity == SAVE_ALL) {
            saveTimeAll();
        } else {
            saveTime(saveActivity);
        }

        /*for (int saveActivity : saveActivityList) {

            int periodDate = 1;

            if (saveActivity == SAVE_DASHBOARD) {
                periodDate = Utils.getPrefInt(Utils.getApplicationContext(), Config.SETTING_PERIOD_DATE, 1);
            }

            setPeriodDate(periodDate);

            setMonth(periodDate);

            setStartDate(periodDate);
            setEndDate(periodDate);

            saveTime(saveActivity);
        }*/
    }

    private void setMonth(int periodDate) {
        // 기준일이 1~15일 인경우..
        if (periodDate <= 15) {
            // 현재일이 기준일 보다 작으면 기본은 전월로 표시
            if (calNowPeriodBase.getTimeInMillis() < calPeriod.getTimeInMillis()) {
                calStart.add(Calendar.MONTH, -1);
                calEnd.add(Calendar.MONTH, -1);
                calPeriod.add(Calendar.MONTH, -1);
            }
            // 현재일이 기준일 보다 크면 현재월로 표시
            else {
            }
        }
        // 기준일이 16이후 인경우..
        else {
            // 현재일이 기준일 보다 크면 다음월로 표시
            if (calNowPeriodBase.getTimeInMillis() >= calPeriod.getTimeInMillis()) {
                calPeriod.add(Calendar.MONTH, 1);
            }
            // 현재일이 기준일 보다 작으면 기본은 현재월로 표시
            else {
                calStart.add(Calendar.MONTH, -1);
                calEnd.add(Calendar.MONTH, -1);
            }
        }
    }

    private void setPeriodDate(int periodDate) {
        if (periodDate == -1) {
            calPeriod.set(Calendar.DATE, calPeriod.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        } else if (periodDate > 28) {
            calPeriod.set(Calendar.DATE, calPeriod.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            calPeriod.set(Calendar.DATE, periodDate);
        }
        calPeriod.set(Calendar.HOUR_OF_DAY, 0);
        calPeriod.set(Calendar.MINUTE, 0);
        calPeriod.set(Calendar.SECOND, 0);
        calPeriod.set(Calendar.MILLISECOND, 0);
    }

    private void setStartDate(int periodDate) {
        if (periodDate > 28) {
            if (calStart.getActualMaximum(Calendar.DAY_OF_MONTH) == 28) {
                calStart.set(Calendar.DATE, 28);
            } else {
                calStart.set(Calendar.DATE, periodDate);
            }
        } else if(periodDate == -1) {
            calStart.set(Calendar.DATE, calStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            calStart.set(Calendar.DATE, periodDate);
        }
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
    }

    private void setEndDate(int periodDate) {
        if (periodDate == 1) {
            calEnd.set(Calendar.DATE, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            calEnd.add(Calendar.MONTH, 1);
            if (calEnd.getActualMaximum(Calendar.DAY_OF_MONTH) == 28 || periodDate == -1) {
                calEnd.set(Calendar.DATE, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
            } else {
                if (calEnd.getActualMaximum(Calendar.DAY_OF_MONTH) < periodDate) {
                    calEnd.set(Calendar.DATE, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
                } else {
                    calEnd.set(Calendar.DATE, periodDate - 1);
                }
            }
        }
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 999);
    }

    private SimpleDateFormat getSimpleDateFormat(String format) {
        SimpleDateFormat sdf;
        try {
            sdf = simpleDateFormat.get(format);
            if (sdf != null) {
                return sdf;
            } else {
                sdf = new SimpleDateFormat(format);
                simpleDateFormat.put(format, sdf);
            }
        } catch (Exception e) {
            sdf = simpleDateFormat.get("yyyy-MM-dd");
        }
        return sdf;
    }

    private void saveTimeAll() {
        for (int saveActivity : saveActivityList) {
            saveTime(saveActivity);
        }
    }

    private void saveTime(int saveActivity) {
        saveMonthMap.put(saveActivity | DATE_TYPE_PERIOD, calPeriod.getTimeInMillis());
        saveMonthMap.put(saveActivity | DATE_TYPE_START, calStart.getTimeInMillis());
        saveMonthMap.put(saveActivity | DATE_TYPE_END, calEnd.getTimeInMillis());
    }


    /**
     * DateManager 초기화 및 현재시간 기준으로 설정함
     */
    public void resetDefaultDate() {
        long nowDt = System.currentTimeMillis();
        calNow.setTimeInMillis(nowDt);
        calPeriod.setTimeInMillis(nowDt);
        calStart.setTimeInMillis(nowDt);
        calEnd.setTimeInMillis(nowDt);
        calNowPeriodBase.setTimeInMillis(nowDt);

        setDefaultDate(SAVE_ALL);
    }

    /**
     * DateManager 초기화 및 현재시간 기준으로 설정함
     * 요청된 시간설정 구분(activity) 만 현재시간으로 초기화 한다.
     */
    public void resetDefaultDate(int saveActivity) {
        long nowDt = System.currentTimeMillis();
        calNow.setTimeInMillis(nowDt);
        calPeriod.setTimeInMillis(nowDt);
        calStart.setTimeInMillis(nowDt);
        calEnd.setTimeInMillis(nowDt);
        calNowPeriodBase.setTimeInMillis(nowDt);

        setDefaultDate(saveActivity);
    }

    /**
     * 액티비티별로 마지막 사용시간이 저장된 시간으로 이동함.
     * @param saveActivity
     * true : 기준일이 설정되어 있고, SettingApi.isDefaultPeriodMode() == true 인 경우, 사용자설정 기준일로 변경됨.
     * false : 월 기준으로 변경됨.
     */
    public void moveToSaveTime(int saveActivity) {
        calPeriod.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_PERIOD));
        calStart.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_START));
        calEnd.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_END));
    }

    /**
     * 요청된 값만큼 월을 이동하고, 변경된 시간값은 saveActivity 별로 저장함.
     * @param month 이동하려는 월(-1 or 1)
     * @param saveActivity
     * @see #saveActivityList
     */
    public void moveMonthAndSaveMonth(int month, int saveActivity) {
        calPeriod.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_PERIOD));
        calStart.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_START));
        calEnd.setTimeInMillis(saveMonthMap.get(saveActivity | DATE_TYPE_END));

        moveMonth(month);
        saveTime(saveActivity);
    }

//    public void moveMonthAndSaveMonthAll(int month) {
//        for (int saveActivity : saveActivityList) {
//            moveToSaveTime(saveActivity);
//            moveMonth(month);
//            saveTime(saveActivity);
//        }
//    }

    /**
     * 요청된 값만큼 월을 이동한다.<br/>
     * @param month
     */
    public void moveMonth(int month) {
        calPeriod.add(Calendar.MONTH, month);
        calStart.add(Calendar.MONTH, month);
        calEnd.add(Calendar.MONTH, month);
        calNowPeriodBase.add(Calendar.MONTH, month);

        if (calPeriod.get(Calendar.DATE) == 1) {
            calEnd.set(Calendar.DATE, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
    }


    /**
     * 현재 월인지 판단한다.
     * @return true or false
     */
    public boolean isThisMonth() {
        if (calNow.getTimeInMillis() >= calStart.getTimeInMillis() &&
                calNow.getTimeInMillis() <= calEnd.getTimeInMillis()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 설정된 월이 현재월보다 이전,현재,이후인지 판단한다.
     * @return
     */
    public int getCurrentMonthType() {
        if (calNow.getTimeInMillis() > calEnd.getTimeInMillis()) {
            return BEFORE_MONTH;
        } else if (calNow.getTimeInMillis() < calStart.getTimeInMillis()) {
            return AFTER_MONTH;
        } else {
            return THIS_MONTH;
        }
    }

    /**
     * 설정된 월의 시작일이 현재일로부터의 몇일 전인지 반환한다.
     * @return 계산된 날짜의 일수
     */
    public int getStartToNowDayCount() {
        int diffDay = 0;

        if (isThisMonth()) {
            long diff = calNow.getTimeInMillis() - calStart.getTimeInMillis();
            diffDay = (int) (diff / (24 * 60 * 60 * 1000));
        }
        return diffDay;
    }

    /**
     * 기준일의 날짜를 반환한다.
     * @return
     */
    public int getPeriodDay() {
        return calPeriod.get(Calendar.DATE);
    }

    /**
     * 기준일의 월을 반환한다.
     * @return
     */
    public int getPeriodMonth() {
        return (calPeriod.get(Calendar.MONTH) + 1);
    }

    /**
     * 기준일의 월 시작 시간을 반환한다.<br/>
     * 기준실적 계산용 1일 (월) 말일
     * @return
     */
    public long getPeriodStartDt() {
        Calendar cal = (Calendar) calPeriod.clone();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 기준일의 월 끝 시간을 반환한다.<br/>
     * 기준실적 계산용 1일 (월) 말일
     * @return
     */
    public long getPeriodEndDt() {
        Calendar cal = (Calendar) calPeriod.clone();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * 기준일로부터 요청된 월을 더한 월을 반환한다.
     * @param month 이동할 월
     * @return
     */
    public int getPeriodMonth(int month) {
        Calendar cal = (Calendar) calPeriod.clone();
        cal.add(Calendar.MONTH, month);
        return (cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 기준일의 년도를 반환한다.
     * @return
     */
    public int getPeriodYear() {
        return calPeriod.get(Calendar.YEAR);
    }


    /**
     * 설정된 월의 시작시간(DateTime)을 반환한다.
     * @return
     */
    public long getLongStartDt() {
        return calStart.getTimeInMillis();
    }

    /**
     * 설정된 월의 종료시간(DateTime)을 반환한다.
     * @return
     */
    public long getLongEndDt() {
        return calEnd.getTimeInMillis();
    }

    /**
     * 설정된 월의 시작시간(DateTime)을 요청된 월을 더하여 반환한다.
     * @param month
     * @return
     */
    public long getLongStartDt(int month) {
        Calendar cal = (Calendar) calStart.clone();
        cal.add(Calendar.MONTH, month);
        return cal.getTimeInMillis();
    }

    /**
     * 설정된 월의 종료시간(DateTime)을 요청된 월을 더하여 반환한다.
     * @param month
     * @return
     */
    public long getLongEndDt(int month) {
        Calendar cal = (Calendar) calEnd.clone();
        cal.add(Calendar.MONTH, month);
        if (calStart.get(Calendar.DATE) == 1) {
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return cal.getTimeInMillis();
    }

    /**
     * 기준일의 시간(DateTime)을 반환한다.
     * @return
     */
    public long getLongPeriodDt() {
        return calPeriod.getTimeInMillis();
    }

    /**
     * 기준일의 시간(DateTime)을 요청된 월을 더하여 반환한다.
     * @param month
     * @return
     */
    public long getLongPeriodDt(int month) {
        Calendar cal = (Calendar) calPeriod.clone();
        cal.add(Calendar.MONTH, month);
        return cal.getTimeInMillis();
    }

    /**
     * 설정된 월의 시작 시간을 지정된 스트링 포멧으로 반환한다.
     * @param format
     * @return
     * @see #simpleDateFormat
     */
    public String getStringStartDt(String format) {
        return getSimpleDateFormat(format).format(calStart.getTime());
    }

    /**
     * 설정된 월의 종료 시간을 지정된 스트링 포멧으로 반환한다.
     * @param format
     * @return
     * @see #simpleDateFormat
     */
    public String getStringEndDt(String format) {
        return getSimpleDateFormat(format).format(calEnd.getTime());
    }

    /**
     * 기준일의 시간을 지정된 스트링 포멧으로 반환한다.
     * @param format
     * @return
     * @see #simpleDateFormat
     */
    public String getStringPeriodDt(String format) {
        return getSimpleDateFormat(format).format(calPeriod.getTime());
    }

    /**
     * 설정된 월의 시작 시간을 요청된 월만큼 더하여 지정된 스트링 포멧으로 반환한다.
     * @param month
     * @param format
     * @return
     * @see #simpleDateFormat
     */
    public String getStringStartDt(int month, String format) {
        Calendar cal = (Calendar) calStart.clone();
        cal.add(Calendar.MONTH, month);
        return getSimpleDateFormat(format).format(cal.getTime());
    }

    /**
     * 설정된 월의 종료 시간을 요청된 월만큼 더하여 지정된 스트링 포멧으로 반환한다.
     * @param month
     * @param format
     * @return
     */
    public String getStringEndDt(int month, String format) {
        Calendar cal = (Calendar) calEnd.clone();
        cal.add(Calendar.MONTH, month);
        if (calStart.get(Calendar.DATE) == 1) {
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return getSimpleDateFormat(format).format(cal.getTime());
    }

    /**
     * 기준일의 시간을 요청된 월만큼 더하여 지정된 스트링 포멧으로 반환한다.
     * @param month
     * @param format
     * @return
     */
    public String getStringPeriodDt(int month, String format) {
        Calendar cal = (Calendar) calPeriod.clone();
        cal.add(Calendar.MONTH, month);
        return getSimpleDateFormat(format).format(cal.getTime());
    }

    /**
     * 요청된 시간(DateTime)을 월 기준으로 시작,종료 시간을 반환한다.
     * @param dateTime
     * @return 시작 00시~종료24시
     */
    public static Calendar[] getStartEndTime(long dateTime) {

        Calendar calBase = Calendar.getInstance();
        calBase.setTimeInMillis(dateTime);

        Calendar start = Calendar.getInstance();
        start.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), calBase.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new Calendar[] {start, end};
    }

    /**
     * 요청된 시간(DateTime)을 월 기준으로 시작,종료 시간을 반환한다.(DateTime)
     * @param dateTime
     * @return 시작 00시~종료24시
     */
    public static long[] getStartEndTimeDt(long dateTime) {

        Calendar calBase = Calendar.getInstance();
        calBase.setTimeInMillis(dateTime);

        Calendar start = Calendar.getInstance();
        start.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), calBase.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new long[] {start.getTimeInMillis(), end.getTimeInMillis()};
    }

//    /**
//     * 현재 월의 시작,마지막 시간을 반환한다.
//     * @return 시작 00시
//     */
//    public static long[] getStartTimeOfThisMonth() {
//
//        Calendar calNow = Calendar.getInstance();
//
//        calNow.set(Calendar.DATE, 1);
//        calNow.set(Calendar.HOUR_OF_DAY, 0);
//        calNow.set(Calendar.MINUTE, 0);
//        calNow.set(Calendar.SECOND, 0);
//        calNow.set(Calendar.MILLISECOND, 0);
//        long startDt = calNow.getTimeInMillis();
//
//        calNow.set(Calendar.DATE, calNow.getActualMaximum(Calendar.DAY_OF_MONTH));
//        calNow.set(Calendar.HOUR_OF_DAY, 23);
//        calNow.set(Calendar.MINUTE, 59);
//        calNow.set(Calendar.SECOND, 59);
//        calNow.set(Calendar.MILLISECOND, 999);
//        long endDt = calNow.getTimeInMillis();
//
//        long[] startEndDt = {startDt, endDt};
//
//        return startEndDt;
//    }

    /**
     * 요청된 시간(DateTime)을 일 기준으로 시작,종료 시간을 반환한다.
     * @param dateTime
     * @return 시작 00시~종료24시
     */
    public static Calendar[] getDailyStartEndTime(long dateTime) {

        Calendar calBase = Calendar.getInstance();
        calBase.setTimeInMillis(dateTime);

        Calendar start = Calendar.getInstance();
        start.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), calBase.get(Calendar.DATE));
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(calBase.get(Calendar.YEAR), calBase.get(Calendar.MONTH), calBase.get(Calendar.DATE));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new Calendar[] {start, end};
    }
}
