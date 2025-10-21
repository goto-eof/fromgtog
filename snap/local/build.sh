#!/bin/bash

set -eux

JLINK_JDK_PATH="/usr/lib/jvm/java-21-openjdk-$(dpkg-architecture -q DEB_BUILD_MULTIARCH)/bin/jlink"
REQUIRED_MODULES="java.base,java.desktop,java.net.http,java.naming,java.sql,java.management,java.security.jgss,java.xml,java.logging,jdk.crypto.ec,java.security.sasl"

$JLINK_JDK_PATH \
  --add-modules $REQUIRED_MODULES \
  --output "$SNAPCRAFT_PART_INSTALL/usr/lib/jvm/custom-jre" \
  --compress=0 \
  --no-header-files \
  --no-man-pages \
  --strip-debug \
  --verbose