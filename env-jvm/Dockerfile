FROM java:openjdk-8u45-jdk

MAINTAINER Muon Core Ltd

RUN mkdir /applocal

COPY build/libs/env-jvm-1.0.jar /applocal/

WORKDIR /applocal

CMD /usr/bin/java -Xmx100m -jar /applocal/env-jvm-1.0.jar
