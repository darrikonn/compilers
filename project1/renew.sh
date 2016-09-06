#!/bin/bash
rm Lexer.java
java -jar ../jflex/lib/jflex-1.6.1.jar lexical.flex
javac TokenDumper.java
jar cfm LexAnal.jar Manifest.txt *.class
