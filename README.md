# simple-pov-app-java

A dependency-free Maven app (one Java file, JDK built-in HTTP server) whose only
job is to be a *real* Java build - Test scenario: Harness reads the `pom.xml`
`<version>` and tags the image with it.

```
pom.xml                              <version>1.2.3</version>  (the value Harness reads)
src/main/java/com/santander/pov/App.java   :8080  "/" and "/healthz"
Dockerfile                           maven build -> distroless/java17 (nonroot)
.dockerignore
```
