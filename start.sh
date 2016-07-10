#!/bin/bash

APPID=de.appwerft.icymetaclient
VERSION=1.2.0

cd android;rm -rf build/*; rm -rf libs/*;rm -rf /disr/*;ant clean;ant ;  unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/;cd ..
#cd iphone/; python build.py;  unzip -uo  $APPID-iphone-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/;cd ..
cp android/dist/$APPID-android-$VERSION.zip .
#cp iphone/$APPID-iphone-$VERSION.zip .
