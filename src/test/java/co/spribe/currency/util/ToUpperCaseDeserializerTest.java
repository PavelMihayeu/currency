package co.spribe.currency.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class ToUpperCaseDeserializerTest {

    private ToUpperCaseDeserializer deserializer;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        deserializer = new ToUpperCaseDeserializer();
    }

    @Test
    void testDeserialize() throws IOException {
        String input = "chF";
        String expectedOutput = "CHF";

        when(jsonParser.getValueAsString()).thenReturn(input);

        String actualOutput = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(expectedOutput, actualOutput, "The deserialized string should be converted to upper case.");
    }

    @Test
    void testDeserializeWithEmptyString() throws IOException {
        String input = "";
        String expectedOutput = "";

        when(jsonParser.getValueAsString()).thenReturn(input);

        String actualOutput = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(expectedOutput, actualOutput, "The deserialized string should handle empty strings correctly.");
    }

    @Test
    void testDeserializeWithNull() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(null);

        String actualOutput = deserializer.deserialize(jsonParser, deserializationContext);

        assertNull(actualOutput, "The deserialized string should handle null values correctly.");
    }
}
