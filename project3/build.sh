#!/bin/bash
rm MyMain.class
rm Lexer.java
java -jar /usr/share/java/jflex/jflex.jar lexical.flex
javac -cp Libraries/commons-lang3-3.4.jar:. MyMain.java
jar cfm TacGenerator.jar Manifest.txt *.class
