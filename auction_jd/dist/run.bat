@echo off

setlocal enabledelayedexpansion
set classpath=.;./classes
for %%f in (./lib/*.jar) do (
    set onefile=%%f
    set classpath=!classpath!;./lib/!onefile!
)

@echo on

echo %classpath%
java -server -classpath "%classpath%" com.silence.jd.auction.biz.AuctionManager