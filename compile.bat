setlocal
del *.class
javac -Xlint:unchecked -deprecation Snapshot.java
makeJar.bat
endlocal
