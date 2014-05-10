package eu.ttbox.ecard.model;


import com.jaccal.util.NumUtil;

import java.util.Arrays;

public class RecvTag {

    public final byte[] key;

    public final int valueSize;

    public RecvTag(String key) {
        this( NumUtil.toStringHex(key));
    }

    public RecvTag(byte[] key) {
        this(key,0);
    }

    public RecvTag(byte[] key, int valueSize) {
        this.key = key;
        this.valueSize = valueSize;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        byte[] otherKey = null;
        if ((o instanceof RecvTag)) {
            RecvTag tag = (RecvTag) o;
            otherKey = tag.key;
        }
        // Compare
        if (otherKey!=null) {
            if (!Arrays.equals(key, otherKey)) return false;
        } else {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }


    @Override
    public String toString() {
        return "RecvTag{" +
                "key=" + Arrays.toString(key) +
                ", valueSize=" + valueSize +
                '}';
    }
}
