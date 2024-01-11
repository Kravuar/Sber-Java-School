package net.kravuar.components.subjects.someSubject;

import net.kravuar.components.cache.Cache;

public interface PartialParametersSubject {

    @Cache
    int test(@Cache.CachedParameter int a, int b, @Cache.CachedParameter int c);
}
