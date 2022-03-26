package com.janeirodigital.mockwebserver;

import okhttp3.mockwebserver.MockWebServer;

import java.net.MalformedURLException;
import java.net.URL;

public class MockWebServerHelper {

    private MockWebServerHelper() { }

    public static URL toUrl(MockWebServer server, String path) {
        try {
            return new URL(server.url(path).toString());
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Can't convert dispatcher request path to URL");
        }
    }

}
