package com.janeirodigital.mockwebserver;

import okhttp3.mockwebserver.MockWebServer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MockWebServerHelper {

    private MockWebServerHelper() { }

    public static URL toMockUrl(MockWebServer server, String path) {
        try {
            return new URL(server.url(path).toString());
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Can't convert dispatcher request path to URL");
        }
    }

    public static URI toMockUri(MockWebServer server, String path) {
        try {
            return toMockUrl(server, path).toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Can't convert dispatcher request path to URI");
        }
    }

}
