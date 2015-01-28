package com.sampullara;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class Main {
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(9998).build();

   

    public static HttpServer createServer(URI uri) throws IOException {
        ResourceConfig rc = new PackagesResourceConfig("com.sampullara");  
        return HttpServerFactory.create(uri, rc);
    }
}