#!/bin/bash
set -eux

exec $SNAP/bin/java \
    -Djava.util.prefs.userRoot="$SNAP_USER_DATA" \
    -Dawt.useSystemAAFontSettings=on \
    -Dswing.aatext=true \
    -Djava.io.tmpdir=$SNAP_USER_DATA/tmp \
    -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel \
    -Djava.desktop.appName=$SNAP/meta/gui/fromgtog.desktop \
    -Dswing.crossplatformlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel \
    -Djdk.gtk.version=3  \
    -jar $SNAP/jar/fromgtog.jar "$@"