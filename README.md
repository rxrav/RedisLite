## Redis (Lite) 
#### This is a solution to this challenge https://codingchallenges.fyi/challenges/challenge-redis Write your own Redis Server by John Crickett
This solution is in the Java programming language (Java 21).

### Description
This is "Redis"-like server, with support for RESP v2 protocol for serialization and deserialization, and a subset of Redis commands: PING, ECHO, GET, SET (with NX, XX, EX and PX options), DEL, EXISTS, INCR, DECR, LPUSH, RPUSH, LRANGE, FLUSHALL and SAVE. It seamlessly works with the Redis CLI, as well as the Jedis (Java) Client for Redis.

### Setup

You need `Java 21` because this project uses virtual threads, which was a preview in earlier versions. 

Also, I have been waiting for a decade to use string templates in Java. 
Though it is a preview feature in this version, I have heavily used string templates in this project. So, at places we need to use the
`--enable-preview` flag.

- Install `Java 21`
- Set `JAVA_HOME` and add to `PATH`
- Download `Maven`
- Setup `MAVEN_HOME` and add to `PATH`

### Check out this project from GitHub 

Run `git clone https://github.com/rxrav/RedisLite.git` in git-scm

### Downloading dependencies

If you're using an IDE like IntelliJ IDEA, you know your way around :) Just open the `pom.xml` and load Maven changes.

However, to download all Maven dependencies, you can `cd` into the project base folder, same place where the `pom.xml` is 
and run `mvn dependency:copy-dependencies`. This will kickstart the download.

We use:
- Log4J2 for logging
- Jedis, used as a client to the RedisLite server
- Jackson, used to convert Maps into JSON strings and vice versa
- JUnit5, for unit tests

### Running tests

Open powershell (or terminal), `cd` into the projects base directory.

If you have set up everything correctly, running `mvn test` should run all the tests.

### Package

Open powershell (or terminal), `cd` into the projects base directory, if you are not already there.

Run `mvn clean package -DskipTests` to package everything into an uber jar without running tests again.

Run `mvn clean package`, does as above, but also runs the tests.

*(Uber jar or fat jar, is a Java (jar) executable, which contains the Java code compiled with all external dependencies)*

### Run

At this point you should have the uber jar ready to run in the `target` directory, named `redislite-1.0-SNAPSHOT.jar`

To run the jar, execute `java --enable-preview -jar .\target\redislite-1.0-SNAPSHOT.jar` from the projects base directory.

(In IntelliJ IDEA, run the `main` method in `App.java`)

### Playground

Now, you can connect to this server using RedisCLI and execute the supported commands.

### Redis for Windows by Microsoft

Microsoft Archive: https://github.com/microsoftarchive/redis/releases

Download the `msi` for Windows and install, it will create a folder named `Redis` inside `C:\Program Files`
which will contain, `redis-cli.exe` and `redis-benchmark.exe`. It will also contain the `redis-server.exe` :)

### Performance

Benchmarked performance of this RedisLite implementation in Java with `redis (redis-server.exe)`, details available in 
this LinkedIn post https://www.linkedin.com/feed/update/urn:li:activity:7146891617956786178/

Edit: After performance improvements at the source code level, this server can handle 60K+ requests per second, compared to ~42K requests previously.
This was done simply by moving the hash maps into their own classes and accessing by reference, rather than having them as static variables in the server class :)

*This has been tested on my HP Laptop (Intel Core i5 + 8GB, running Windows 10), keeping in mind Java's WORA principle, 
I trust this will run the same way on other OS with JRE 21 installed on it.*

### Connect with me on LinkedIn

URL to my LinkedIn profile https://www.linkedin.com/in/sauravdey/