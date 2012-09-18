#!/bin/sh
cd usecase/ ; mvn install ; cd ..
cd 2apl/ ; mvn install ; cd ..
cd eventbus.instance/ ; mvn install ; cd ..
cd eventbus/ ; mvn install ; cd ..
cd test.scenario/ ; mvn install ; cd ..
