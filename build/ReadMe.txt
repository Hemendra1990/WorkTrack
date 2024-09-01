#cd /Volumes/Java/Projects/CRM/AllInOne/Projects/WorkTrac

jpackage \
--input target \
--name WebTrackerMac \
--main-jar wt-1.jar \
--main-class com.hemendra.WorkTrackApp \
--type dmg \
--resource-dir src/main/resources \
--mac-package-identifier com.hemendra \
--mac-package-name WorkTrackerMac