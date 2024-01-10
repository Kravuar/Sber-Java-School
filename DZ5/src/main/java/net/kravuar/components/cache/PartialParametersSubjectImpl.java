package net.kravuar.components.cache;

public class PartialParametersSubjectImpl implements PartialParametersSubject {
    @Override
    public int test(int a, int b, int c) {
        // b used for some side effects
        return a * c;
    }
}
