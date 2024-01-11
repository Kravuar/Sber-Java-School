package net.kravuar.components.beans;

import net.kravuar.components.subjects.assign.GetterGuy;
import net.kravuar.components.subjects.assign.GoodSetterGuy;
import net.kravuar.components.subjects.assign.SetterGuyWithIncompatibleProperty;
import net.kravuar.components.subjects.reflection.HierarchyBottom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeanUtilsTests {
    GetterGuy getterGuy;

    @BeforeAll
    void init() {
        getterGuy = new GetterGuy(
                5,
                new ArrayList<Integer>(),
                "baba",
                new HierarchyBottom()
        );
    }

    @Test
    void assignWorksFine() {
        var setterGuy = new GoodSetterGuy();

        BeanUtils.assign(setterGuy, getterGuy);

        assertEquals(getterGuy.getBaba(), setterGuy.getBaba());
        assertEquals(getterGuy.getBebe(), setterGuy.getBebe());
        assertEquals(getterGuy.getBobo(), setterGuy.getBobo());
        assertEquals(getterGuy.getByby(), setterGuy.getByby());
    }

    @Test
    void assignSkipsIncompatibleProperties() {
        var setterGuy = new SetterGuyWithIncompatibleProperty();

        BeanUtils.assign(setterGuy, getterGuy);

        assertEquals(getterGuy.getBaba(), setterGuy.getBaba());
        assertEquals(getterGuy.getBebe(), setterGuy.getBebe());
        assertEquals(getterGuy.getBobo(), setterGuy.getBobo());
        assertNull(setterGuy.getByby());
    }
}