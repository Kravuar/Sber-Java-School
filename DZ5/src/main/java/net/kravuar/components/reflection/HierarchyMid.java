package net.kravuar.components.reflection;

public class HierarchyMid implements HierarchyTop {
    String middleMethod() {
        return null;
    }
    int getMiddle() {
        return 0;
    }

    @Override
    public String topMethod() {
        return null;
    }

    @Override
    public int getTop() {
        return 0;
    }
}
