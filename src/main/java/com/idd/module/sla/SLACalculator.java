package com.idd.module.sla;

import java.util.Calendar;
import java.util.Date;

public class SLACalculator {

    private final WorkingDayRepository repository;

    public SLACalculator(String datasourceName) {
        this.repository = new WorkingDayRepository(datasourceName);
    }

    public Date calculateSLA(int minutes, Calendar calendar) throws Exception {

        double time = minutes;

        if (time < 0) {
            time = 0;
        }

        int timeRest = 0;
        boolean isCal = false;
        int skipday = 0;

        long timeSLA = calendar.getTimeInMillis();

        WorkingDayConfig config = repository.findWorkingDay(calendar);

        if (config == null) {

            calendar = repository.findNextWorkingDay(calendar);

            timeSLA = calendar.getTimeInMillis();

            config = repository.findWorkingDay(calendar);
        }

        int endMorning = config.getEndMorning();
        int endAfternoon = config.getEndAfternoon();
        int startMorning = config.getStartMorning();
        int startAfternoon = config.getStartAfternoon();

        long timeWork1Day = (endMorning - startMorning)
                + (endAfternoon - startAfternoon);

        boolean isFirstTime = true;

        while (!isCal) {

            int now = minuteOfDay(calendar);

            if (time < timeWork1Day && !isFirstTime) {
                isCal = true;
            }

            if (now >= endMorning) {

                if (now >= (endAfternoon - timeRest)) {

                    if (isCal) {

                        timeSLA = calendar.getTimeInMillis()
                                + (long) (time * 60 * 1000);

                    } else {

                        skipday = (int) (time / timeWork1Day);

                        time = time - (skipday * timeWork1Day);

                        setMinute(calendar, startMorning);

                        calendar.add(Calendar.DATE, 1);

                        calendar = repository.findNextWorkingDay(calendar, skipday + 1);
                    }

                } else {

                    if (now + time >= endAfternoon) {

                        if (isCal) {

                            timeSLA = calendar.getTimeInMillis()
                                    + (long) (time * 60 * 1000);

                        } else {

                            double timeCal;

                            if (now < startAfternoon) {
                                timeCal = time + startAfternoon;
                            } else {
                                timeCal = now + time;
                            }

                            double timeMinute = timeCal - endAfternoon;

                            skipday = (int) (timeMinute / timeWork1Day);

                            time = timeMinute - (skipday * timeWork1Day);

                            setMinute(calendar, startMorning);

                            calendar.add(Calendar.DATE, 1);

                            calendar = repository.findNextWorkingDay(calendar, skipday + 1);
                        }

                    } else {

                        if (now > endMorning) {

                            if (now < startAfternoon) {

                                setMinute(calendar, startAfternoon);

                                timeSLA = calendar.getTimeInMillis()
                                        + (long) (time * 60 * 1000);

                                isCal = true;

                            } else {

                                timeSLA = calendar.getTimeInMillis()
                                        + (long) (time * 60 * 1000);

                                isCal = true;
                            }

                        } else {

                            if ((now + time) > endMorning) {

                                time = (now + time)
                                        - endMorning;

                                setMinute(calendar, startAfternoon);

                                timeSLA = calendar.getTimeInMillis()
                                        + (long) (time * 60 * 1000);

                                isCal = true;

                            } else {

                                timeSLA = calendar.getTimeInMillis()
                                        + (long) (time * 60 * 1000);

                                isCal = true;
                            }
                        }
                    }
                }

            } else {

                if ((now + time) > endMorning) {

                    double timeMinute = (now + time)
                            - endMorning;

                    if ((endAfternoon - startAfternoon - timeRest) > timeMinute) {

                        setMinute(calendar, startAfternoon);

                        timeSLA = calendar.getTimeInMillis()
                                + (long) (timeMinute * 60 * 1000);

                        isCal = true;

                    } else {

                        timeMinute = timeMinute
                                - (endAfternoon - startAfternoon);

                        skipday = (int) (timeMinute / timeWork1Day);

                        time = timeMinute
                                - (skipday * timeWork1Day);

                        setMinute(calendar, startMorning);

                        calendar.add(Calendar.DATE, 1);

                        calendar = repository.findNextWorkingDay(calendar, skipday + 1);
                    }

                } else {

                    timeSLA = calendar.getTimeInMillis()
                            + (long) (time * 60 * 1000);

                    isCal = true;
                }
            }

            isFirstTime = false;
        }

        if (timeSLA == 0) {
            timeSLA = calendar.getTimeInMillis();
        }

        return new Date(timeSLA);
    }
    
    private int minuteOfDay(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    }

    private void setMinute(Calendar cal, int minuteOfDay) {

        cal.set(Calendar.HOUR_OF_DAY, minuteOfDay / 60);
        cal.set(Calendar.MINUTE, minuteOfDay % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
