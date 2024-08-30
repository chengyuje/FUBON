package com.systex.jbranch.comutil.collection;

import org.apache.commons.lang.ObjectUtils;

import java.util.Comparator;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomComparator<T> implements Comparator<T> {
    public enum ORDER {ASC, DESC}

    public enum NULL {FIRST, LAST}

    private static final int CONTINUE = 2;

    private final Comparator<T>[] comparators;

    @SafeVarargs
    public CustomComparator(Comparator<T>... comparators) {
        this.comparators = comparators;
    }

    @Override
    public int compare(Object o1, Object o2) {
        int compareCurrVal = 0;
        for (Comparator<T> c : comparators) {
            compareCurrVal = c.compare((T) o1, (T) o2);
            if (compareCurrVal != 0)
                break;
        }
        return compareCurrVal;
    }

    // 預設 ASC 並且不做 NULL 任何設定
    public static Comparator<Map<String, Object>> byField(final Object field) {
        return byField(field, ORDER.ASC, null);
    }

    // 預設不做 NULL 任何設定
    public static Comparator<Map<String, Object>> byField(final Object field, final ORDER order) {
        return byField(field, order, null);
    }

    public static Comparator<Map<String, Object>> byField(final Object field, final ORDER order, final NULL nul) {
        return new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Object obj1 = o1.get(field);
                Object obj2 = o2.get(field);

                int compareNum;
                compareNum = sortNull(obj1, obj2);
                if (compareNum != CONTINUE) return compareNum;

                if (obj1 instanceof Comparable) {
                    Comparable c1 = (Comparable) obj1;
                    Comparable c2 = (Comparable) obj2;

                    if (ORDER.DESC.equals(order)) {
                        return c2.compareTo(c1);
                    } else { // ASC 以及沒有傳入排序的話預設 ASC
                        return c1.compareTo(c2);
                    }
                } else {
                    return 0; // 非 Comparable 就回傳相等
                }
            }

            private int sortNull(final Object obj1, final Object obj2) {
                // 針對 null、以及空字串的情況做處理
                String s1 = ObjectUtils.toString(obj1);
                String s2 = ObjectUtils.toString(obj2);

                if (s1.isEmpty() && !s2.isEmpty()) {
                    if (null != nul) {
                        return NULL.LAST.equals(nul) ? 1 : -1;
                    } else {
                        if (ORDER.ASC.equals(order))
                            return 1;
                        if (ORDER.DESC.equals(order))
                            return -1;
                    }
                } else if (!s1.isEmpty() && s2.isEmpty()) {
                    if (null != nul) {
                        return NULL.LAST.equals(nul) ? -1 : 1;
                    } else {
                        if (ORDER.ASC.equals(order))
                            return -1;
                        if (ORDER.DESC.equals(order))
                            return 1;
                    }
                } else if (s1.isEmpty()) { // 兩者皆為空
                    return 0;
                }
                // 兩者不為空
                return CONTINUE;
            }
        };
    }
}
