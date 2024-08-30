package com.systex.jbranch.platform.common.workday.impl.rule;

import com.systex.jbranch.platform.common.dataaccess.datasource.DataSource;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.workday.WorkDayHelper;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Angus Luo
 * @date 2010-05-07
 */
public class ManualRollWorkCalendar extends BaseCalendar {
// ------------------------------ FIELDS ------------------------------

    private static final String DATE_PATTERN = "yyyyMMdd";

    private static final long serialVersionUID = -6053466648935576820L;

    protected String name = WorkDayHelper.PARAM_CODE;
    //	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	private Logger logger = LoggerFactory.getLogger(ManualRollWorkCalendar.class);

// -------------------------- OTHER METHODS --------------------------

    @Override
    public Date getToday() throws JBranchException {
        Calendar workDate = syncCurrentDate();
        return workDate.getTime();
    }

//	private static XmlInfo info = new XmlInfo();

    private Calendar syncCurrentDate() throws JBranchException {
        DataSource ds = new DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        Session session = sf.openSession();

        SessionImpl sessionImpl = (SessionImpl) session;
        Connection con = sessionImpl.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String currentDate = null;//info.getVariable(WorkDayHelper.PARAM_TYPE, getName(), FormatHelper.FORMAT_3);
            ps = con.prepareStatement("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE=? and PARAM_CODE=?");
            ps.setString(1, WorkDayHelper.PARAM_TYPE);
            ps.setString(2, getName());
            rs = ps.executeQuery();

            Calendar workDate = Calendar.getInstance();
            if (rs.next()) {
                currentDate = rs.getString("PARAM_NAME");
                try {
                    Date date = DateUtils.parseDate(currentDate, new String[]{DATE_PATTERN});
                    workDate.setTime(date);
                }
                catch (ParseException e) {
                    workDate = Calendar.getInstance();
                }
            }
            else {
                currentDate = DateFormatUtils.format(workDate.getTime(), DATE_PATTERN);
                ps.close();
                long tm = Calendar.getInstance().getTimeInMillis();
                ps = con.prepareStatement("insert into TBSYSPARAMETER(PARAM_TYPE, PARAM_CODE, PARAM_ORDER, PARAM_NAME, PARAM_NAME_EDIT, PARAM_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE)" +
                        " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, WorkDayHelper.PARAM_TYPE);
                ps.setString(2, getName());
                ps.setInt(3, 0);
                ps.setString(4, currentDate);
                ps.setString(5, currentDate);
                ps.setString(6, "3");
                ps.setBigDecimal(7, new BigDecimal(0));
                ps.setTimestamp(8, new Timestamp(tm));
                ps.setString(9, "SYSTEM");
                ps.setString(10, "SYSTEM");
                ps.setTimestamp(11, new Timestamp(tm));

                ps.execute();
            }

            workDate.set(Calendar.HOUR_OF_DAY, 0);
            workDate.set(Calendar.MINUTE, 0);
            workDate.set(Calendar.SECOND, 0);
            workDate.set(Calendar.MILLISECOND, 0);
            return workDate;
        }
        catch (SQLException e) {
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    @Override
    public void rollNextDay() throws JBranchException {
        Calendar workDate = syncCurrentDate();
        workDate.add(Calendar.DATE, 1);
        String date = DateFormatUtils.format(workDate.getTime(), DATE_PATTERN);

        DataSource ds = new DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        Session session = sf.openSession();

        SessionImpl sessionImpl = (SessionImpl) session;
        Connection con = sessionImpl.connection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement("update TBSYSPARAMETER set PARAM_NAME=?, PARAM_NAME_EDIT=? where PARAM_TYPE=? and PARAM_CODE=?");
            ps.setString(1, date);
            ps.setString(2, date);
            ps.setString(3, WorkDayHelper.PARAM_TYPE);
            ps.setString(4, getName());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Calendar[" + getName() + "] rollNextDay[" + date + "]");
        }
    }

    @Override
    public void rollNextWorkDay() throws JBranchException {
        Calendar workDate = syncCurrentDate();
        do {
            workDate.add(Calendar.DATE, 1);
        } while (!isTimeIncluded(workDate.getTimeInMillis()));

        String date = DateFormatUtils.format(workDate.getTime(), DATE_PATTERN);
//		info.setVariable(WorkDayHelper.PARAM_TYPE, getName(), date);
        DataSource ds = new DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        Session session = sf.openSession();

        SessionImpl sessionImpl = (SessionImpl) session;
        Connection con = sessionImpl.connection();
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement("update TBSYSPARAMETER set PARAM_NAME=?, PARAM_NAME_EDIT=?  where PARAM_TYPE=? and PARAM_CODE=?");
            ps.setString(1, date);
            ps.setString(2, date);
            ps.setString(3, WorkDayHelper.PARAM_TYPE);
            ps.setString(4, getName());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Calendar[" + getName() + "] rollNextWorkDay[" + date + "]");
        }
    }
}
