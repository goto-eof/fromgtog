#!/bin/bash

LIB_DIR=$(find "$SNAPCRAFT_PRIME/usr/lib/" -maxdepth 1 -type d -name "*-linux-gnu" -print -quit)
ln -sf libappindicator3.so.1.0.0 "$LIB_DIR/libappindicator3.so"
ln -sf libasound.so "$LIB_DIR/liboss4-salsa.so.2.0.0"