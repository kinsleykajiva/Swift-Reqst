package africa.jopen.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class JacksonPOJOGenerator {

    public static void generatePOJOClassFromJSONString(String jsonString, String className, String packageName, String outputPath) throws IOException {

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert the JSON string to a JsonNode object
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Create a new ObjectNode to represent the class
        ObjectNode classNode = objectMapper.createObjectNode();

        // Set the class name
        classNode.put("class", className);

        // Set the package name
        classNode.put("package", packageName);

        // Create a new ObjectNode to represent the fields
        ObjectNode fieldsNode = objectMapper.createObjectNode();

        // Loop through the JSON fields and create a field in the class for each one
        jsonNode.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode fieldNode = entry.getValue();
            String fieldType = fieldNode.getNodeType().toString().toLowerCase();
            fieldsNode.put(fieldName, fieldType);
        });

        // Add the fields to the class
        classNode.set("fields", fieldsNode);

        // Convert the class node to a JSON string
        String classJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(classNode);

        // Write the class to a file
        // File outputFile = new File(outputPath, className + ".java");
      //  objectMapper.writeValue(outputFile, classJsonString);

      // System.out.println("Generated POJO class: " + outputFile.getAbsolutePath());
        System.out.println("Generated POJO class: " +classJsonString);
    }
}

