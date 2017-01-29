# cybersecuritybase-project-1

[![Build Status](https://travis-ci.org/juhofriman/cybersecuritybase-project-1.svg?branch=master)](https://travis-ci.org/juhofriman/cybersecuritybase-project-1)

This is my take on [Cyber Security Base](https://cybersecuritybase.github.io/) course project I.

Application is simple messaging service, in which users can send global and private messages.

**Warning!** This software contains intentional security flaws.

## Prerequisites

This is built in clojure but do not fear. It's easy.

### Never used clojure?

Clojure is JVM hosted language and you need just JVM to run this application. I have prebuilt jar, so evaluator does not have to install leininge.

https://dl.dropboxusercontent.com/u/68780282/cybersecuritybase-project-1-0.1.0-SNAPSHOT-standalone.jar

Download jar and start it with:

```
java -jar cybersecuritybase-project-1-0.1.0-SNAPSHOT-standalone.jar
```

And point browser to localhost:3000. Application sets some initial state and at the moment it is not possible to launch it without initial messages and users. This could be easily done with environment though.

### Smug clojure weenie?

If you're into clojure you can play with leiningen. This is for more advanced clojurists and it is not needed for evaluation.

```bash
$ lein repl
2017-01-29 13:50:31.262:INFO::main: Logging initialized @1852ms
nREPL server started on port 64470 on host 127.0.0.1 - nrepl://127.0.0.1:64470
REPL-y 0.3.7, nREPL 0.2.12
Clojure 1.8.0
Java HotSpot(TM) 64-Bit Server VM 1.8.0_45-b14
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

user=> (start!)
2017-01-29 13:50:39.986:INFO:oejs.Server:nREPL-worker-0: jetty-9.2.10.v20150310
2017-01-29 13:50:40.047:INFO:oejs.ServerConnector:nREPL-worker-0: Started ServerConnector@332420c6{HTTP/1.1}{0.0.0.0:3000}
2017-01-29 13:50:40.047:INFO:oejs.Server:nREPL-worker-0: Started @10637ms
```

It's also possible to launch this application by `lein ring server` or `lein ring server-headless`, both open nrepl so cider is well suited for development.

Uberjar must be build with `lein ring uberjar`