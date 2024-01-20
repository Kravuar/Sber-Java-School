package net.kravuar.cache.addapting;

import java.io.Serial;
import java.io.Serializable;

public final class NullValue implements Serializable {
    /**
     * The canonical representation of a {@code null} replacement, as used by the
     * default implementation of {@link AbstractValueAdaptingCache#toStoreValue}/
     * {@link AbstractValueAdaptingCache#fromStoreValue}.
     */
    public static final Object INSTANCE = new NullValue();
    @Serial
    private static final long serialVersionUID = 1L;

    private NullValue() {}

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || other == null);
    }

    @Override
    public int hashCode() {
        return NullValue.class.hashCode();
    }
}