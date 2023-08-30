[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.israiloff/checkstyle-rules/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.israiloff/checkstyle-rules)

# Checkstyle

Code checking rules for matching code conventions of company. Must be used as part of [***Apache Checkstyle plugin***](https://checkstyle.sourceforge.io/). 
To configure [***this plugin***](https://github.com/Israiloff/checkstyle) you must add 
[***Apache Checkstyle plugin***](https://checkstyle.sourceforge.io/) into your project 
([***pom.xml***](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html)/[***gradle.json***](https://gradle.org/)).
Then just include [***this plugin***](https://github.com/Israiloff/checkstyle) into the 
***dependencies*** of [***Apache Checkstyle plugin***](https://checkstyle.sourceforge.io/).

Example in [***pom.xml***](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html):

```xml

<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>APACHE_CHECKSTYLE_VERSION</version>
                <executions>
                    <execution>
                        <id>validate</id>
                        <phase>validate</phase>
                        <configuration>
                            <configLocation>checkstyle.config.xml</configLocation>
                            <consoleOutput>true</consoleOutput>
                            <failsOnError>true</failsOnError>
                        </configuration>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>com.github.israiloff</groupId>
                        <artifactId>checkstyle-rules</artifactId>
                        <version>CHECKSTYLE_RULES_VERSION</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
</project>
```

> [***APACHE_CHECKSTYLE_VERSION***](https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-checkstyle-plugin) is version of [***Apache Checkstyle plugin***](https://checkstyle.sourceforge.io/). 
> [***CHECKSTYLE_RULES_VERSION***](https://github.com/Israiloff/checkstyle) is version of [***this plugin***](https://github.com/Israiloff/checkstyle).
