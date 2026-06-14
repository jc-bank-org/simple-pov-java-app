// =============================================================================
// Tiny demo HTTP service (Java) — the "app" Scenario 3 builds & pushes to ECR.
// =============================================================================
// No external dependencies: uses the JDK's built-in com.sun.net.httpserver so
// the Maven build is fast and the image stays small. The point isn't the Java —
// it's that the image gets tagged with the pom.xml <version>.
//
// version() resolves in this order:
//   1. APP_VERSION env var       (set at deploy time from the artifact tag)
//   2. JAR Implementation-Version (stamped from pom.xml <version> at build time)
//   3. "dev" fallback
// so the running container echoes the real version even with no env set.
// =============================================================================
package com.santander.pov;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    static String version() {
        String env = System.getenv("APP_VERSION");
        if (env != null && !env.isBlank()) {
            return env;
        }
        String impl = App.class.getPackage().getImplementationVersion();
        return (impl != null && !impl.isBlank()) ? impl : "dev";
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/healthz", ex -> respond(ex, 200, "ok\n"));
        server.createContext("/", ex ->
            respond(ex, 200, "Santander PoV Java app — version " + version() + "\n"));
        server.setExecutor(null);
        System.out.println("santander-pov-java version=" + version() + " listening on :8080");
        server.start();
    }

    static void respond(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
