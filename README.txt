make sure your JAVA_HOME is pointing to a proper jdk version 1.7 or newer
run parser/gradlew installApp
there's a few places it can be run from here:
-via the gradle wrapper, but you have to set a variable for the required arguments to be passed to the program:
-- eg. gradlew run -Dexec.args="--algorithm lru --input path/to/data/file"
-via the "installed" runner:
-- eg. ./os_memory/build/install/os_memory/bin/os_memory --algorithm lru --input path/to/data/file