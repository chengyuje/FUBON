package com.systex.jbranch.platform.server.workflow.spi.memory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.util.DataUtil;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.Expression;
import com.opensymphony.workflow.query.FieldExpression;
import com.opensymphony.workflow.query.NestedExpression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.SimpleStep;
import com.opensymphony.workflow.spi.SimpleWorkflowEntry;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/** memory implementation. */
public class TransientWorkflowStore implements WorkflowStore {
    private static Map<Long, SimpleWorkflowEntry> entryCache = new ConcurrentHashMap<Long, SimpleWorkflowEntry>();
    private static Map<Long, List<SimpleStep>> currentStepsCache = new ConcurrentHashMap<Long, List<SimpleStep>>();
    private static Map<Long, List<SimpleStep>> historyStepsCache = new ConcurrentHashMap<Long, List<SimpleStep>>();
    private static Map<Long, PropertySet> propertySetCache = new ConcurrentHashMap<Long, PropertySet>();
    private static AtomicLong globalEntryId = new AtomicLong(1);
    private static AtomicLong globalStepId = new AtomicLong(1);

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void clear(long entryId) {
        entryCache.remove(entryId);
        currentStepsCache.remove(entryId);
        historyStepsCache.remove(entryId);
        propertySetCache.remove(entryId);
    }

    public void setEntryState(long entryId, int state) throws StoreException {
        SimpleWorkflowEntry theEntry = (SimpleWorkflowEntry) findEntry(entryId);
        theEntry.setState(state);
    }

    public PropertySet getPropertySet(long entryId) {
        PropertySet ps = propertySetCache.get(entryId);

        if (ps == null) {
            ps = PropertySetManager.getInstance("memory", null);
            propertySetCache.put(entryId, ps);
        }

        return ps;
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) {
        long id = globalStepId.getAndIncrement();
        SimpleStep step = new SimpleStep(id, entryId, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);

        List<SimpleStep> currentSteps = currentStepsCache.get(entryId);

        if (currentSteps == null) {
            currentSteps = new ArrayList<SimpleStep>();
            currentStepsCache.put(entryId, currentSteps);
        }

        currentSteps.add(step);

        return step;
    }

    /**
     * Reset the MemoryWorkflowStore so it doesn't have any information.
     * Useful when testing and you don't want the MemoryWorkflowStore to
     * have old data in it.
     */
    public static void reset() {
        entryCache.clear();
        currentStepsCache.clear();
        historyStepsCache.clear();
        propertySetCache.clear();
    }

    public WorkflowEntry createEntry(String workflowName) {
        long id = globalEntryId.getAndIncrement();
        SimpleWorkflowEntry entry = new SimpleWorkflowEntry(id, workflowName, WorkflowEntry.CREATED);
        entryCache.put(id, entry);

        return entry;
    }

    public List findCurrentSteps(long entryId) {
        List<SimpleStep> currentSteps = currentStepsCache.get(entryId);

        if (currentSteps == null) {
            currentSteps = new ArrayList<SimpleStep>();
            currentStepsCache.put(entryId, currentSteps);
        }

        return new ArrayList<SimpleStep>(currentSteps);
    }

    public WorkflowEntry findEntry(long entryId) {
        return entryCache.get(entryId);
    }

    public List findHistorySteps(long entryId) {
        List<SimpleStep> historySteps = historyStepsCache.get(entryId);

        if (historySteps == null) {
            historySteps = new ArrayList<SimpleStep>();
            historyStepsCache.put(entryId, historySteps);
        }

        return new ArrayList<SimpleStep>(historySteps);
    }

    public void init(Map props) {
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String caller) {
        List<SimpleStep> currentSteps = currentStepsCache.get(step.getEntryId());

        for (SimpleStep theStep : currentSteps) {
            if (theStep.getId() == step.getId()) {
                theStep.setStatus(status);
                theStep.setActionId(actionId);
                theStep.setFinishDate(finishDate);
                theStep.setCaller(caller);

                return theStep;
            }
        }

        return null;
    }

    public void moveToHistory(Step step) {
        List<SimpleStep> currentSteps = currentStepsCache.get(step.getEntryId());

        List<SimpleStep> historySteps = historyStepsCache.get(step.getEntryId());

        if (historySteps == null) {
            historySteps = new ArrayList<SimpleStep>();
            historyStepsCache.put(step.getEntryId(), historySteps);
        }

        SimpleStep simpleStep = (SimpleStep) step;

        for (Iterator<SimpleStep> iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step currentStep = iterator.next();

            if (simpleStep.getId() == currentStep.getId()) {
                iterator.remove();
                historySteps.add(0, simpleStep);

                break;
            }
        }
    }

    public List query(WorkflowQuery query) {
        ArrayList<Long> results = new ArrayList<Long>();

        for (Map.Entry<Long, SimpleWorkflowEntry> mapEntry : entryCache.entrySet()) {
            Long entryId = mapEntry.getKey();

            if (query(entryId, query)) {
                results.add(entryId);
            }
        }

        return results;
    }

    public List query(WorkflowExpressionQuery query) {
        ArrayList<Long> results = new ArrayList<Long>();

        for (Map.Entry<Long, SimpleWorkflowEntry> mapEntry : entryCache.entrySet()) {
            Long entryId = mapEntry.getKey();

            if (query(entryId, query)) {
                results.add(entryId);
            }
        }

        return results;
    }

    private boolean checkExpression(long entryId, FieldExpression expression) {
        Object value = expression.getValue();
        int operator = expression.getOperator();
        int field = expression.getField();
        int context = expression.getContext();

        if (context == FieldExpression.ENTRY) {
            SimpleWorkflowEntry theEntry = entryCache.get(entryId);

            if (field == FieldExpression.NAME) {
                return this.compareText(theEntry.getWorkflowName(), (String) value, operator);
            }

            if (field == FieldExpression.STATE) {
                return this.compareLong(DataUtil.getInt((Integer) value), theEntry.getState(), operator);
            }

            throw new InvalidParameterException("unknown field");
        }

        List<SimpleStep> steps;

        if (context == FieldExpression.CURRENT_STEPS) {
            steps = currentStepsCache.get(entryId);
        }
        else if (context == FieldExpression.HISTORY_STEPS) {
            steps = historyStepsCache.get(entryId);
        }
        else {
            throw new InvalidParameterException("unknown field context");
        }

        if (steps == null) {
            return false;
        }

        boolean expressionResult = false;

        switch (field) {
            case FieldExpression.ACTION:

                long actionId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (this.compareLong(step.getActionId(), actionId, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.CALLER:

                String caller = (String) value;

                for (SimpleStep step : steps) {
                    if (this.compareText(step.getCaller(), caller, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.FINISH_DATE:

                Date finishDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (this.compareDate(step.getFinishDate(), finishDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.OWNER:

                String owner = (String) value;

                for (SimpleStep step : steps) {
                    if (this.compareText(step.getOwner(), owner, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.START_DATE:

                Date startDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (this.compareDate(step.getStartDate(), startDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.STEP:

                int stepId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (this.compareLong(step.getStepId(), stepId, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.STATUS:

                String status = (String) value;

                for (SimpleStep step : steps) {
                    if (this.compareText(step.getStatus(), status, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FieldExpression.DUE_DATE:

                Date dueDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (this.compareDate(step.getDueDate(), dueDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;
        }

        if (expression.isNegate()) {
            return !expressionResult;
        }
        else {
            return expressionResult;
        }
    }

    private boolean checkNestedExpression(long entryId, NestedExpression nestedExpression) {
        for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
            boolean expressionResult;
            Expression expression = nestedExpression.getExpression(i);

            if (expression.isNested()) {
                expressionResult = this.checkNestedExpression(entryId, (NestedExpression) expression);
            }
            else {
                expressionResult = this.checkExpression(entryId, (FieldExpression) expression);
            }

            if (nestedExpression.getExpressionOperator() == NestedExpression.AND) {
                if (!expressionResult) {
                    return nestedExpression.isNegate();
                }
            }
            else if (nestedExpression.getExpressionOperator() == NestedExpression.OR) {
                if (expressionResult) {
                    return !nestedExpression.isNegate();
                }
            }
        }

        if (nestedExpression.getExpressionOperator() == NestedExpression.AND) {
            return !nestedExpression.isNegate();
        }
        else if (nestedExpression.getExpressionOperator() == NestedExpression.OR) {
            return nestedExpression.isNegate();
        }

        throw new InvalidParameterException("unknown operator");
    }

    private boolean compareDate(Date value1, Date value2, int operator) {
        switch (operator) {
            case FieldExpression.EQUALS:
                return value1.compareTo(value2) == 0;

            case FieldExpression.NOT_EQUALS:
                return value1.compareTo(value2) != 0;

            case FieldExpression.GT:
                return (value1.compareTo(value2) > 0);

            case FieldExpression.LT:
                return value1.compareTo(value2) < 0;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private boolean compareLong(long value1, long value2, int operator) {
        switch (operator) {
            case FieldExpression.EQUALS:
                return value1 == value2;

            case FieldExpression.NOT_EQUALS:
                return value1 != value2;

            case FieldExpression.GT:
                return value1 > value2;

            case FieldExpression.LT:
                return value1 < value2;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private boolean compareText(String value1, String value2, int operator) {
        switch (operator) {
            case FieldExpression.EQUALS:
                return TextUtils.noNull(value1).equals(value2);

            case FieldExpression.NOT_EQUALS:
                return !TextUtils.noNull(value1).equals(value2);

            case FieldExpression.GT:
                return TextUtils.noNull(value1).compareTo(value2) > 0;

            case FieldExpression.LT:
                return TextUtils.noNull(value1).compareTo(value2) < 0;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private boolean query(Long entryId, WorkflowQuery query) {
        if (query.getLeft() == null) {
            return queryBasic(entryId, query);
        }
        else {
            int operator = query.getOperator();
            WorkflowQuery left = query.getLeft();
            WorkflowQuery right = query.getRight();

            switch (operator) {
                case WorkflowQuery.AND:
                    return query(entryId, left) && query(entryId, right);

                case WorkflowQuery.OR:
                    return query(entryId, left) || query(entryId, right);

                case WorkflowQuery.XOR:
                    return query(entryId, left) ^ query(entryId, right);
            }
        }

        return false;
    }

    private boolean query(long entryId, WorkflowExpressionQuery query) {
        Expression expression = query.getExpression();

        if (expression.isNested()) {
            return this.checkNestedExpression(entryId, (NestedExpression) expression);
        }
        else {
            return this.checkExpression(entryId, (FieldExpression) expression);
        }
    }

    private boolean queryBasic(Long entryId, WorkflowQuery query) {
        // the query object is a comparison
        Object value = query.getValue();
        int operator = query.getOperator();
        int field = query.getField();
        int type = query.getType();

        switch (operator) {
            case WorkflowQuery.EQUALS:
                return queryEquals(entryId, field, type, value);

            case WorkflowQuery.NOT_EQUALS:
                return queryNotEquals(entryId, field, type, value);

            case WorkflowQuery.GT:
                return queryGreaterThan(entryId, field, type, value);

            case WorkflowQuery.LT:
                return queryLessThan(entryId, field, type, value);
        }

        return false;
    }

    private boolean queryEquals(Long entryId, int field, int type, Object value) {
        List<SimpleStep> steps;

        if (type == WorkflowQuery.CURRENT) {
            steps = currentStepsCache.get(entryId);
        }
        else {
            steps = historyStepsCache.get(entryId);
        }

        if (steps == null) {
            return false;
        }

        switch (field) {
            case WorkflowQuery.ACTION:

                long actionId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (step.getActionId() == actionId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.CALLER:

                String caller = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getCaller()).equals(caller)) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.FINISH_DATE:

                Date finishDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (finishDate.equals(step.getFinishDate())) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.OWNER:

                String owner = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getOwner()).equals(owner)) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.START_DATE:

                Date startDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (startDate.equals(step.getStartDate())) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STEP:

                int stepId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (stepId == step.getStepId()) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STATUS:

                String status = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getStatus()).equals(status)) {
                        return true;
                    }
                }

                return false;
        }

        return false;
    }

    private boolean queryGreaterThan(Long entryId, int field, int type, Object value) {
        List<SimpleStep> steps;

        if (type == WorkflowQuery.CURRENT) {
            steps = currentStepsCache.get(entryId);
        }
        else {
            steps = historyStepsCache.get(entryId);
        }

        switch (field) {
            case WorkflowQuery.ACTION:

                long actionId = DataUtil.getLong((Long) value);

                for (SimpleStep step : steps) {
                    if (step.getActionId() > actionId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.CALLER:

                String caller = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getCaller()).compareTo(caller) > 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.FINISH_DATE:

                Date finishDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (step.getFinishDate().compareTo(finishDate) > 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.OWNER:

                String owner = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getOwner()).compareTo(owner) > 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.START_DATE:

                Date startDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (step.getStartDate().compareTo(startDate) > 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STEP:

                int stepId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (step.getStepId() > stepId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STATUS:

                String status = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getStatus()).compareTo(status) > 0) {
                        return true;
                    }
                }

                return false;
        }

        return false;
    }

    private boolean queryLessThan(Long entryId, int field, int type, Object value) {
        List<SimpleStep> steps;

        if (type == WorkflowQuery.CURRENT) {
            steps = currentStepsCache.get(entryId);
        }
        else {
            steps = historyStepsCache.get(entryId);
        }

        switch (field) {
            case WorkflowQuery.ACTION:

                long actionId = DataUtil.getLong((Long) value);

                for (SimpleStep step : steps) {
                    if (step.getActionId() < actionId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.CALLER:

                String caller = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getCaller()).compareTo(caller) < 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.FINISH_DATE:

                Date finishDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (step.getFinishDate().compareTo(finishDate) < 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.OWNER:

                String owner = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getOwner()).compareTo(owner) < 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.START_DATE:

                Date startDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (step.getStartDate().compareTo(startDate) < 0) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STEP:

                int stepId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (step.getStepId() < stepId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STATUS:

                String status = (String) value;

                for (SimpleStep step : steps) {
                    if (TextUtils.noNull(step.getStatus()).compareTo(status) < 0) {
                        return true;
                    }
                }

                return false;
        }

        return false;
    }

    private boolean queryNotEquals(Long entryId, int field, int type, Object value) {
        List<SimpleStep> steps;

        if (type == WorkflowQuery.CURRENT) {
            steps = currentStepsCache.get(entryId);
        }
        else {
            steps = historyStepsCache.get(entryId);
        }

        switch (field) {
            case WorkflowQuery.ACTION:

                long actionId = DataUtil.getLong((Long) value);

                for (SimpleStep step : steps) {
                    if (step.getActionId() != actionId) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.CALLER:

                String caller = (String) value;

                for (SimpleStep step : steps) {
                    if (!TextUtils.noNull(step.getCaller()).equals(caller)) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.FINISH_DATE:

                Date finishDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (!finishDate.equals(step.getFinishDate())) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.OWNER:

                String owner = (String) value;

                for (SimpleStep step : steps) {
                    if (!TextUtils.noNull(step.getOwner()).equals(owner)) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.START_DATE:

                Date startDate = (Date) value;

                for (SimpleStep step : steps) {
                    if (!startDate.equals(step.getStartDate())) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STEP:

                int stepId = DataUtil.getInt((Integer) value);

                for (SimpleStep step : steps) {
                    if (stepId != step.getStepId()) {
                        return true;
                    }
                }

                return false;

            case WorkflowQuery.STATUS:

                String status = (String) value;

                for (SimpleStep step : steps) {
                    if (!TextUtils.noNull(step.getStatus()).equals(status)) {
                        return true;
                    }
                }

                return false;
        }

        return false;
    }
}
