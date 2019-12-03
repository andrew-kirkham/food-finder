# Global vars
ARG BUILD_DIR="/tmp/app/"
ARG JAR_NAME="food-finder.jar"

# Build JAR
FROM library/gradle:6.0-jdk13 as builder
ARG BUILD_DIR
COPY . $BUILD_DIR
WORKDIR $BUILD_DIR
RUN gradle clean jar

# Build generic runtime container with non-root user
FROM library/openjdk:13.0.1-jdk as runtime
ARG BUILD_DIR
ENV USR_PATH="/usr/app"
ENV USER="food"
ENV USER_NUM=666
ENV GROUP="application"
ENV GROUP_NUM=666

RUN groupadd -g $GROUP_NUM $GROUP && \
    adduser -g $GROUP -u $USER_NUM -m -d $USR_PATH $USER
WORKDIR $USR_PATH
USER $USER

# Add JAR to runtime container
FROM runtime
ARG BUILD_DIR
ARG JAR_NAME
ENV JAR=$JAR_NAME
COPY --from=builder $BUILD_DIR/build/libs/$JAR_NAME .
COPY scripts /usr/scripts
CMD ["sh", "-c", "java -jar $JAR"]