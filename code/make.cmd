setlocal enabledelayedexpansion
@echo off

rem if not "!JAVA8_64_HOME!"=="" (
rem     set PATH=!JAVA8_64_HOME!\bin;!PATH!
rem     set JAVA_HOME=!JAVA8_64_HOME!
rem )

if "%1" == "" (
    echo Usage: make ^<profile-name^>
    echo Example: make dev
) else (
    if not exist profiles\%1.properties (
        echo ERROR: file "profiles\%1.properties" not found
        pause
    ) else (
        del profile.properties
        copy profiles\%1.properties profile.properties
        mvn clean package assembly:single
    )
)