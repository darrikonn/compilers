#!/bin/bash
rm Lexer.java
java -jar ../jflex/lib/jflex-1.6.1.jar lexical.flex
javac Parser.java
jar cfm Parser.jar Manifest.txt *.class
