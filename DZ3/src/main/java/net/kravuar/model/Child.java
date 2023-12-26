package net.kravuar.model;

public class Child extends Parent implements Comparable<Child> {
    private final String name;

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Child (" + name + ")";
    }

    @Override
    public int compareTo(Child o) {
        return Integer.compare(
                this.getName().length(),
                o.getName().length()
        );
    }
}
