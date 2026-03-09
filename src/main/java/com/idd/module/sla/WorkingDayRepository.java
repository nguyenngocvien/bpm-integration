package com.idd.module.sla;

import com.idd.module.sql.SQLConnector;
import com.idd.shared.util.BpmLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

public class WorkingDayRepository extends SQLConnector {

        private static final String SQL_FIND_WORKING_DAY = "SELECT TIME_END_MORNING_INT, TIME_END_ALTERNOON_INT, " +
                        "TIME_START_MORNING_INT, TIME_START_ALTERNOON_INT " +
                        "FROM ADM_CONFIG_WORKING_DAY " +
                        "WHERE YEAR_IN_DATE = ? " +
                        "AND MONTH_IN_DATE = ? " +
                        "AND DAY_IN_DATE = ? " +
                        "AND DATE_WORKING_TYPE = 1 " +
                        "FETCH FIRST 1 ROWS ONLY";

        private static final String SQL_FIND_NEXT_WORKING_DAY = "SELECT DAY_IN_DATE, MONTH_IN_DATE, YEAR_IN_DATE, TIME_START_MORNING "
                        +
                        "FROM ADM_CONFIG_WORKING_DAY " +
                        "WHERE FULL_DATE >= ? " +
                        "AND DATE_WORKING_TYPE = 1 " +
                        "ORDER BY FULL_DATE " +
                        "FETCH FIRST 1 ROWS ONLY";

        public WorkingDayRepository(String dataSourceName) {
                super(dataSourceName);
        }

        public WorkingDayConfig findWorkingDay(Calendar calendar) {

                Connection conn = null;

                try {

                        conn = getConnection();

                        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_WORKING_DAY)) {

                                ps.setInt(1, calendar.get(Calendar.YEAR));
                                ps.setInt(2, calendar.get(Calendar.MONTH) + 1);
                                ps.setInt(3, calendar.get(Calendar.DATE));

                                try (ResultSet rs = ps.executeQuery()) {

                                        if (!rs.next()) {
                                                return null;
                                        }

                                        WorkingDayConfig config = new WorkingDayConfig();

                                        config.setStartMorning((int) (rs.getDouble("TIME_START_MORNING_INT") * 60));
                                        config.setEndMorning((int) (rs.getDouble("TIME_END_MORNING_INT") * 60));
                                        config.setStartAfternoon((int) (rs.getDouble("TIME_START_ALTERNOON_INT") * 60));
                                        config.setEndAfternoon((int) (rs.getDouble("TIME_END_ALTERNOON_INT") * 60));

                                        return config;
                                }
                        }

                } catch (Exception e) {

                        BpmLogger.error(
                                        "Load working day config failed: "
                                                        + calendar.getTime(),
                                        e);

                        return null;

                } finally {

                        try {
                                if (conn != null)
                                        conn.close();
                        } catch (Exception e) {
                        }
                }
        }

        public Calendar findNextWorkingDay(Calendar calendar) {

                Connection conn = null;

                try {

                        conn = getConnection();

                        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_NEXT_WORKING_DAY)) {

                                java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());

                                ps.setDate(1, sqlDate);

                                try (ResultSet rs = ps.executeQuery()) {

                                        if (!rs.next()) {
                                                return null;
                                        }

                                        Calendar result = Calendar.getInstance();

                                        result.set(Calendar.DATE, rs.getInt("DAY_IN_DATE"));
                                        result.set(Calendar.MONTH, rs.getInt("MONTH_IN_DATE") - 1);
                                        result.set(Calendar.YEAR, rs.getInt("YEAR_IN_DATE"));

                                        return result;
                                }
                        }

                } catch (Exception e) {

                        BpmLogger.error(
                                        "Load next working day failed: "
                                                        + calendar.getTime(),
                                        e);

                        return null;

                } finally {

                        try {
                                if (conn != null)
                                        conn.close();
                        } catch (Exception e) {
                        }
                }
        }

        public Calendar findNextWorkingDay(Calendar calendar, int skipday) {

                Connection conn = null;

                try {

                        conn = getConnection();

                        String sql = "SELECT DAY_IN_DATE, MONTH_IN_DATE, YEAR_IN_DATE, TIME_START_MORNING " +
                                        "FROM ADM_CONFIG_WORKING_DAY " +
                                        "WHERE FULL_DATE >= ? " +
                                        "AND DATE_WORKING_TYPE = 1 " +
                                        "ORDER BY FULL_DATE " +
                                        "FETCH FIRST ? ROWS ONLY";

                        try (PreparedStatement ps = conn.prepareStatement(sql)) {

                                java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());

                                ps.setDate(1, sqlDate);
                                ps.setInt(2, skipday);

                                try (ResultSet rs = ps.executeQuery()) {

                                        Calendar result = null;

                                        while (rs.next()) {

                                                result = Calendar.getInstance();

                                                result.set(Calendar.DATE, rs.getInt("DAY_IN_DATE"));
                                                result.set(Calendar.MONTH, rs.getInt("MONTH_IN_DATE") - 1);
                                                result.set(Calendar.YEAR, rs.getInt("YEAR_IN_DATE"));

                                                double startMorning = rs.getDouble("TIME_START_MORNING");

                                                int minuteOfDay = (int) (startMorning * 60);

                                                result.set(Calendar.HOUR_OF_DAY, minuteOfDay / 60);
                                                result.set(Calendar.MINUTE, minuteOfDay % 60);
                                                result.set(Calendar.SECOND, 0);
                                                result.set(Calendar.MILLISECOND, 0);
                                        }

                                        return result;
                                }
                        }

                } catch (Exception e) {

                        BpmLogger.error(
                                        "Load next working day with skip failed: "
                                                        + calendar.getTime(),
                                        e);

                        return null;

                } finally {

                        try {
                                if (conn != null)
                                        conn.close();
                        } catch (Exception e) {
                        }
                }
        }

}
