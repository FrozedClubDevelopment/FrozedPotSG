package me.elb1to.frozedsg.utils;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
    private Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
        if (this.base.get(a) >= this.base.get(b)) {
            return -1;
        }
        return 1;
    }
}
