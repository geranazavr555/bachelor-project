FROM openjdk:21

WORKDIR /iomarkup
COPY target/io-markup-1.0-SNAPSHOT-jar-with-dependencies.jar /iomarkup

EXPOSE 8456
ENTRYPOINT ["/usr/bin/java", "-jar", "io-markup-1.0-SNAPSHOT-jar-with-dependencies.jar"]
CMD ["-d", "-p", "8456"]
