#!/bin/bash
rm Parser.class
rm Lexer.java
java -jar /usr/share/java/jflex/jflex.jar lexical.flex
javac -cp Libraries/commons-lang3-3.4.jar:. Parser2.java
jar cfm Parser2.jar Manifest.txt *.class
