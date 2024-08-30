package com.systex.jbranch.platform.common.workday;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Angus Luo
 * @date 2010-05-07
 */
public class CalendarDataProviderFactory {
// ------------------------------ FIELDS ------------------------------

    private static Logger logger = LoggerFactory.getLogger(CalendarDataProviderFactory.class);

    final private static String DEFAULT_CALENDAR_PROVIDER = "default_calendar_data_provider";

// -------------------------- STATIC METHODS --------------------------

    public static CalendarDataProvider getDefaultCalendarProvider() throws JBranchException {
        return getCalendar(DEFAULT_CALENDAR_PROVIDER);
    }

    public static CalendarDataProvider getCalendar(String id) throws JBranchException {
        Object o = PlatformContext.getBean(id);

        if (!(o instanceof CalendarDataProvider)) {
            String msg = "無效的CalendarDataProvider, ID:[" + id + "]";
            logger.error(msg);
            throw new JBranchException(msg);
        }

        return (CalendarDataProvider) o;
    }
}
