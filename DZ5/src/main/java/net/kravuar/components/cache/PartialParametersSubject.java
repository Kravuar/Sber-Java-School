package net.kravuar.components.cache;

public interface PartialParametersSubject {

    @Cache
    int test(@Cache.CachedParameter int a, int b, @Cache.CachedParameter int c);
}
