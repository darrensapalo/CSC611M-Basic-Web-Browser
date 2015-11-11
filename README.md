Socket implementation of HTTP GET request
=========================
A basic web browser implemented in Java

### About
A mini exercise for the CSC611M (ADVANOS) class AY2014-2015, Term 2 by Kyle DELA CRUZ, Kristine KALAW,
Kevin RIVERA, and Darren SAPALO

## Changelog

After a year, I (Darren) branched Kristine's work and reduced the code to a simple code snippet that
performs an HTTP/1.1 GET request. It stores the request response and the content response in a folder
named cache.

To change the fetched URL, the app accepts the first command line argument. You can also change the
default url found in the Driver class.

```Java
public static final String defaultUrl = "google.com";
```

## Issues

When receiving HTTP 302 or 301 responses, the app blocks while waiting for a content response at the
following line:

```Java
// blocks at reader.readLine()
while ((response = reader.readLine()) != null) {
    fileWriter.append(response + "\n");
    fileWriter.flush();
    System.out.println(response);
}
```

It seems that the web server does not send any content when the response is a redirect.
