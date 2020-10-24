FROM openjdk:8-jre-alpine

COPY target/musiqueue-0.0.1-SNAPSHOT.jar musiqueue-0.0.1-SNAPSHOT.jar

CMD java $JAVA_OPTS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Djava.security.egd=file:/dev/./urandom -jar musiqueue-0.0.1-SNAPSHOT.jar
