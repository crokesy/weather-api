# Dockerfile for Scala Project

# Base Image
FROM openjdk:8

# Environment Variables
ENV SCALA_VERSION 2.13.8

# Install Scala
RUN \
  curl -fsL http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
   echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
   echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
   curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
   apt-get update && \
   apt-get install sbt


# Create Project Directory
RUN mkdir -p /root/project

# Set Working Directory
WORKDIR /root/project

# Copy Project Files
COPY . /root/project

# Build Project
RUN sbt compile

# Run Project
CMD ["sbt", "run"]