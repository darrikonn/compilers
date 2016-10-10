#!/bin/bash
rm Lexer.java
java -jar /usr/share/java/jflex/jflex.jar lexical.flex
javac -cp Libraries/commons-lang3-3.4.jar:. Parser.java
jar cfm Parser.jar Manifest.txt *.class
