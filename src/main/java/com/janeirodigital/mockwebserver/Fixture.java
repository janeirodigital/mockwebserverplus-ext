package com.janeirodigital.mockwebserver;

import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Originated from: https://github.com/orhanobut/mockwebserverplus (apache license)
 * Key changes:
 *  - Did not support non-JSON body response
 *  - Added minor token replacement for server base url in fixture contents
 *
 * A value container that holds all information about the fixture file.
 */
@Getter @Setter
public class Fixture {

    private int statusCode;
    private String body;
    private List<String> headers;
    private int delay;

    /**
     * Parse the given filename and returns the Fixture object.
     *
     * @param fileName filename should not contain extension or relative path. ie: login
     */
    public static Fixture parseFrom(String fileName, RecordedRequest request) {
        return parseFrom(fileName, new YamlParser(), request);
    }


    /**
     * Parse the given filename and returns the Fixture object.
     *
     * @param fileName filename should not contain extension or relative path. ie: login
     * @param parser   parser is required for parsing operation, it should not be null
     */
    public static Fixture parseFrom(String fileName, Parser parser, RecordedRequest request) {
        if (fileName == null) {
            throw new NullPointerException("File name should not be null");
        }
        String path = "fixtures/" + fileName + ".yaml";
        Map<String, String> variables = new HashMap<>();
        variables.put("SERVER_BASE", getServerBaseFromRequest(request));
        StringSubstitutor substitutor = new StringSubstitutor(variables);
        try {
            return parser.parse(substitutor.replace(readPathIntoString(path)));
        } catch (IOException ex) {
            throw new IllegalStateException("Test Harness: Error reading from " + path + ": " + Arrays.toString(ex.getStackTrace()));
        }
    }

    private static String getServerBaseFromRequest(RecordedRequest request) {
        return request.getRequestUrl().scheme() + "://" + request.getRequestUrl().host() + ":" + request.getRequestUrl().port();
    }

    private static InputStream openPathAsStream(String path) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalStateException("Test Harness: Invalid path: " + path);
        }

        return inputStream;
    }

    private static String readPathIntoString(String path) throws IOException {
        InputStream inputStream = openPathAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        int read;
        while ((read = reader.read()) != -1) {
            out.append((char) read);
        }
        reader.close();

        return out.toString();
    }

    public MockResponse toMockResponse() {
        MockResponse mockResponse = new MockResponse();
        if (this.statusCode != 0) {
            mockResponse.setResponseCode(this.statusCode);
        }
        if (this.body != null) {
            mockResponse.setBody(this.body);
        }
        if (this.delay != 0) {
            mockResponse.setBodyDelay(this.delay, TimeUnit.MILLISECONDS);
        }
        if (this.headers != null) {
            for (String header : this.headers) {
                mockResponse.addHeader(header);
            }
        }
        return mockResponse;
    }
}
