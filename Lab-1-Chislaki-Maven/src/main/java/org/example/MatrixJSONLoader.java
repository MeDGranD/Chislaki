package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class MatrixJSONLoader {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SLAU {
        public Double[][] matrix;
        public Double[] b;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Accuracy {
        public double tol;
        public int maxIter;
    }

    public static SLAU loadSLAU(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), SLAU.class);
    }

    public static Matrix loadMatrix(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Double[][] data = objectMapper.readValue(new File(filePath), Double[][].class);
        return new Matrix(data);
    }

    public static Accuracy loadAccuracy(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), Accuracy.class);
    }

}
