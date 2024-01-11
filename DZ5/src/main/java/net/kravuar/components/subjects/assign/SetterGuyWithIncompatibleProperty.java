package net.kravuar.components.subjects.assign;

import lombok.Getter;
import lombok.Setter;
import net.kravuar.components.subjects.reflection.HierarchyBottom;

@Setter
@Getter
public class SetterGuyWithIncompatibleProperty {
    private int bebe;
    private Object bobo;
    private String baba;
    private HierarchyBottom byby; // This one
}
