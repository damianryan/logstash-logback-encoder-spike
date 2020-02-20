# Logstash Formatted Error Logs Do Not Appear When Running A Spring Boot Application in IntelliJ IDEA With Spring Boot Devtools Applied

## Reproduction Environment

1. IntelliJ IDEA 2019.3.3 (only version tested, may occur with earlier versions)
2. JDK 8, 9, 10, 11, 12 or 13
3. MacOS or Windows
4. Spring Boot 2.2.4.RELEASE
5. Logstash Logback Encoder 6.3

## Doh!

I blame my age and the need for reading glasses.

What originally set me down the road of this spike was that I couldn't see error level logs in the console in IntelliJ 
IDEA when using logstash logback encoder to write the log lines in logstash JSON format.

A bit of investigation (and a couple of dead ends) later, I discovered that this only happens when running a spring
boot application inside IntelliJ IDEA with spring boot devtools on the runtime classpath.

It does not happen if the application is run from a packaged executable JAR (which disables devtools) or if the
application is run inside IntelliJ IDEA with devtools taken off the classpath (that is, if its `implementation`
 dependency is commented out in `build.gradle`.)

This lead me initially to believe that there was some sort of unintended interaction occurring between logstash logback
encoder and spring boot devtools.

It wasn't until I copied some log lines out of the console and pasted them into a text editor that I saw that the error 
line I'd thought was missing was actually there along along, and perfectly visible in the pasted lines.

So it turns out that there never was an issue with logstash logback encoder or spring boot devtools. The issue is that
IntelliJ IDEA folds the error line when devtools is on the runtime classpath, and does not fold the same line if
devtools is not on the runtime classpath:

### What's Logged Without Devtools

```
{"@timestamp":"2020-02-20T11:07:35.776Z","@version":"1","message":"no errors yet","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"main","level":"INFO","level_value":20000,"service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
{"@timestamp":"2020-02-20T11:07:35.778Z","@version":"1","message":"something really bad happened","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"main","level":"ERROR","level_value":40000,"stack_trace":"java.lang.RuntimeException: Boom!\n\tat com.damianryan.spikes.logging.Main.run(Main.java:21)\n\tat org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784)\n\tat org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:768)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:322)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)\n\tat com.damianryan.spikes.logging.Main.main(Main.java:14)\n","service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
{"@timestamp":"2020-02-20T11:07:35.778Z","@version":"1","message":"you should have seen an error logged","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"main","level":"INFO","level_value":20000,"service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
```

### What's Logged With Devtools

```
{"@timestamp":"2020-02-20T11:22:58.746Z","@version":"1","message":"no errors yet","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"restartedMain","level":"INFO","level_value":20000,"service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
{"@timestamp":"2020-02-20T11:22:58.747Z","@version":"1","message":"something really bad happened","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"restartedMain","level":"ERROR","level_value":40000,"stack_trace":"java.lang.RuntimeException: Boom!\n\tat com.damianryan.spikes.logging.Main.run(Main.java:21)\n\tat org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784)\n\tat org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:768)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:322)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)\n\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)\n\tat com.damianryan.spikes.logging.Main.main(Main.java:14)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)\n","service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
{"@timestamp":"2020-02-20T11:22:58.748Z","@version":"1","message":"you should have seen an error logged","logger_name":"com.damianryan.spikes.logging.Main","thread_name":"restartedMain","level":"INFO","level_value":20000,"service_name":"logstash-logback-encoder-spike","service_version":"1.0-SNAPSHOT"}
```

### What's the difference?

There are 2 differences between the lines that are logged with and without devtools applied:

1. Without devtools, the `thread_name` is `main`, with devtools it is `restartedMain`. This isn't a trigger for folding.
2. With devtools, the logged stacktrace contains a bunch of extra elements:

```
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.base/java.lang.reflect.Method.invoke(Method.java:566)
at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)
```

It's the appearance of reflective invocations in the stacktrace that cause IntelliJ IDEA to fold the log line.

On closer inspection of the console, it's possible to see a very faint light grey `+` icon against the white background
of the console indicating that a fold has occurred.

This is what I hadn't spotted.

Looks like I need to wear my reading glasses all the time during coding, and/or switch to a higher contrast
colour scheme!