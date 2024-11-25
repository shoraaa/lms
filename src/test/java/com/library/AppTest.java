package com.library;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import javafx.scene.Parent;

public class AppTest {

    @Test
    public void testHelloWorld() {
        assertEquals(1 + 1, 2);
    }

    @Test
    public void testLoadFXML() throws Exception {
        // Use reflection to access the private method 'loadFXML'
        Method loadFXMLMethod = App.class.getDeclaredMethod("loadFXML", String.class);
        loadFXMLMethod.setAccessible(true);

        Parent root = (Parent) loadFXMLMethod.invoke(null, "/com/library/views/Dashboard");
        assertNotNull(root, "FXML should be loaded and not null");
    }

    @Test
    public void testSetRoot() throws IOException, NoSuchFieldException, IllegalAccessException {
        App.setRoot("/com/library/views/Dashboard");

        // Use reflection to access the private field 'scene'
        Field sceneField = App.class.getDeclaredField("scene");
        sceneField.setAccessible(true);
        Object scene = sceneField.get(null); // Assuming 'scene' is a static field

        assertNotNull(scene, "Scene root should be set and not null");
    }

    @Test
    public void testLoadFXMLThrowsIOException() throws Exception {
        // Use reflection to access the private method 'loadFXML'
        Method loadFXMLMethod = App.class.getDeclaredMethod("loadFXML", String.class);
        loadFXMLMethod.setAccessible(true);

        assertThrows(IOException.class, () -> {
            loadFXMLMethod.invoke(null, "/com/library/views/NonExistentView");
        }, "Loading a non-existent FXML should throw IOException");
    }
}