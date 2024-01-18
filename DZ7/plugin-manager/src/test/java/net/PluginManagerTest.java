package net;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class PluginManagerTest {
    @Test
    void reversedManager_loadsPlugin_PluginHasDifferentJUnitVersion() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                null,
                true
        );
        var subdirectory = Path.of("willLoad/");
        var className = "net.kravuar.Test";

        // when
        var plugin = assertDoesNotThrow(() -> reversedPluginManager.loadPlugin(subdirectory, className));
        @SuppressWarnings("unchecked")
        Supplier<Class<?>> castPlugin = (Supplier<Class<?>>) plugin;

        // then
        assertNotEquals(castPlugin.get(), Test.class);
    }

    @Test
    void regularManager_andParentIsCurrentClassloader_loadsPlugin_PluginHasSameJUnitVersion() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                getClass().getClassLoader(),
                false
        );
        var subdirectory = Path.of("willLoad/");
        var className = "net.kravuar.Test";

        // when
        var plugin = assertDoesNotThrow(() -> reversedPluginManager.loadPlugin(subdirectory, className));
        @SuppressWarnings("unchecked")
        Supplier<Class<?>> castPlugin = (Supplier<Class<?>>) plugin;

        // then
        assertEquals(castPlugin.get(), Test.class);
    }

    @Test
    void pluginWithGivenClassNameNotFound_ThrowsPluginLoadingException() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                null,
                true
        );
        var subdirectory = Path.of("wontLoad/");
        var className = "doesNotExist";

        // when & then
        assertThrows(PluginLoadingException.class, () -> reversedPluginManager.loadPlugin(subdirectory, className));
    }

    @Test
    void givenAbsoluteSubdirPath_ThrowsIllegalArgumentException() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                null,
                true
        );
        var subdirectory = Path.of("C:/absolute/");
        var className = "irrelevant";
        // when & then
        assertThrows(IllegalArgumentException.class, () -> reversedPluginManager.loadPlugin(subdirectory, className));
    }

    @Test
    void givenPluginThatDoesNotImplementInterface_ThrowsPluginLoadingException() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                null,
                true
        );
        var subdirectory = Path.of("wontLoad/");
        var className = "net.kravuar.Test";
        // when & then
        assertThrows(PluginLoadingException.class, () -> reversedPluginManager.loadPlugin(subdirectory, className));
    }

    @Test
    void givenPluginWithoutNoArgsConstructor_ThrowsPluginLoadingException() {
        // given
        PluginManager<Supplier> reversedPluginManager = new PluginManager<>(
                Paths.get("src", "test", "resources"),
                Supplier.class,
                null,
                true
        );
        var subdirectory = Path.of("wontLoad/");
        var className = "net.kravuar.Test";
        // when & then
        assertThrows(PluginLoadingException.class, () -> reversedPluginManager.loadPlugin(subdirectory, className));
    }
}