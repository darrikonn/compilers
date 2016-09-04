#!/bin/bash
rm Lexer.java Lexer.class
java -jar jflex/lib/jflex-1.6.1.jar lexical.flex
javac Lexer.java
javac TokenDumper.java
