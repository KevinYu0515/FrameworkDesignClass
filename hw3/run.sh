#!/bin/bash

shopt -s expand_aliases
alias javac="cmd.exe /c javac"
alias java="cmd.exe /c java"

rm -f src/*.class
rm -f src/**/*.class

javac src/Main.java

java src/Main