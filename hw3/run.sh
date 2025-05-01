#!/bin/bash

shopt -s expand_aliases
alias javac="cmd.exe /c javac"
alias java="cmd.exe /c java"

rm -f *.class

javac Main.java

java Main