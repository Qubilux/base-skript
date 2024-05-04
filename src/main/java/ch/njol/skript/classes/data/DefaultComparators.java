/**
 * This file is part of Skript.
 * <p>
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.classes.data;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.util.*;
import ch.njol.util.StringUtils;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

@SuppressWarnings({"rawtypes", "RedundantMethodOverride"})
public class DefaultComparators {

    public DefaultComparators() {
    }

    static {

        // Number - Number
        Comparators.registerComparator(Number.class, Number.class, new Comparator<Number, Number>() {
            @Override
            public Relation compare(Number n1, Number n2) {
                if (n1 instanceof Long && n2 instanceof Long)
                    return Relation.get(n1.longValue() - n2.longValue());
                Double d1 = n1.doubleValue(),
                        d2 = n2.doubleValue();
                if (d1.isNaN() || d2.isNaN()) {
                    return Relation.SMALLER;
                } else if (d1.isInfinite() || d2.isInfinite()) {
                    return d1 > d2 ? Relation.GREATER : d1 < d2 ? Relation.SMALLER : Relation.EQUAL;
                } else {
                    double diff = d1 - d2;
                    if (Math.abs(diff) < Skript.EPSILON)
                        return Relation.EQUAL;
                    return Relation.get(diff);
                }
            }

            @Override
            public boolean supportsOrdering() {
                return true;
            }
        });
    }

    static {
        // String - String
        Comparators.registerComparator(String.class, String.class, new Comparator<String, String>() {
            @Override
            public Relation compare(String s1, String s2) {
                return Relation.get(StringUtils.equals(s1, s2, SkriptConfig.caseSensitive.value()));
            }

            @Override
            public boolean supportsOrdering() {
                return false;
            }
        });

        // Date - Date
        Comparators.registerComparator(Date.class, Date.class, new Comparator<Date, Date>() {
            @Override
            public Relation compare(Date d1, Date d2) {
                return Relation.get(d1.compareTo(d2));
            }

            @Override
            public boolean supportsOrdering() {
                return true;
            }
        });

        // Time - Time
        Comparators.registerComparator(Time.class, Time.class, new Comparator<Time, Time>() {
            @Override
            public Relation compare(Time t1, Time t2) {
                return Relation.get(t1.getTime() - t2.getTime());
            }

            @Override
            public boolean supportsOrdering() {
                return true;
            }
        });

        // Timespan - Timespan
        Comparators.registerComparator(Timespan.class, Timespan.class, new Comparator<Timespan, Timespan>() {
            @Override
            public Relation compare(Timespan t1, Timespan t2) {
                return Relation.get(t1.getMilliSeconds() - t2.getMilliSeconds());
            }

            @Override
            public boolean supportsOrdering() {
                return true;
            }
        });

        // Time - Timeperiod
        Comparators.registerComparator(Time.class, Timeperiod.class, new Comparator<Time, Timeperiod>() {
            @Override
            public Relation compare(Time t, Timeperiod p) {
                return Relation.get(p.contains(t));
            }

            @Override
            public boolean supportsOrdering() {
                return false;
            }
        });

        // Object - ClassInfo
        Comparators.registerComparator(Object.class, ClassInfo.class, new Comparator<Object, ClassInfo>() {
            @SuppressWarnings("unchecked")
            @Override
            public Relation compare(Object o, ClassInfo c) {
                return Relation.get(c.getC().isInstance(o) || o instanceof ClassInfo && c.getC().isAssignableFrom(((ClassInfo<?>) o).getC()));
            }

            @Override
            public boolean supportsOrdering() {
                return false;
            }
        });
    }
}
