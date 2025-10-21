#!/bin/bash

MARKER_FILE="$SNAPCRAFT_PART_INSTALL/built_marker"

if [ -f "$MARKER_FILE" ]; then
  echo "Build already completed, skipping."
  exit 0
fi

touch "$MARKER_FILE"

getProxy () {
  host=$(echo "$1" | sed -E 's|https?://([^:/]*):?[0-9]*/?|\1|')
  port=$(echo "$1" | sed -E 's|https?://[^:/]*:?([0-9]*)/?|\1|')
}

echo "Starting to build FromGtoG at $(date)"
START_TIME=$(date +%s)
echo "Start building FromGtoG at $(date)"

set -eux
JLINK_JDK_PATH="/usr/lib/jvm/java-21-openjdk-$(dpkg-architecture -q DEB_BUILD_MULTIARCH)"
REQUIRED_MODULES="java.base,java.desktop,java.net.http,java.naming,java.sql,java.management,java.security.jgss,java.xml,java.logging,jdk.crypto.ec,java.security.sasl"

echo "Creating custom JRE runtime with the following modules: $REQUIRED_MODULES"

/usr/bin/jlink \
  --add-modules $REQUIRED_MODULES \
  --output "$SNAPCRAFT_PART_INSTALL"/usr/lib/jvm/custom-jre \
  --compress=2 \
  --no-header-files \
  --no-man-pages \
  --strip-debug

http=""
https=""

if [ -n "${http_proxy:-}" ]; then
    getProxy "$http_proxy"
    http="-Dhttp.proxyHost=$host -Dhttp.proxyPort=$port"
fi

if [ -n "${https_proxy:-}" ]; then
    getProxy "$https_proxy"
    https="-Dhttps.proxyHost=$host -Dhttps.proxyPort=$port"
fi

export MAVEN_OPTS="$http $https"

mvn clean install -DskipTests -T 8

APP_JAR_NAME="fromgtog.jar"
APP_JAR_PATH="$SNAPCRAFT_PART_BUILD/target/$APP_JAR_NAME"

if [ ! -f "$APP_JAR_PATH" ]; then
    echo "ERROR: jar not found in $APP_JAR_PATH"
    exit 1
fi

mkdir -p "$SNAPCRAFT_PART_INSTALL"/jar
echo "Directory created: $SNAPCRAFT_PART_INSTALL/jar"

echo "Copying $APP_JAR_NAME in $SNAPCRAFT_PART_INSTALL/jar/fromgtog.jar"
cp "$APP_JAR_PATH" "$SNAPCRAFT_PART_INSTALL"/jar/fromgtog.jar

echo "custom JRE runtime created in $SNAPCRAFT_PART_INSTALL/usr/lib/jvm/custom-jre"
rm -rf "$JLINK_JDK_PATH"
echo "temporary JDK removed."

echo "Finished building FromGtoG at $(date)"
END_TIME=$(date +%s)
echo "Total time: $((${END_TIME} - ${START_TIME})) seconds"