#!/bin/bash

shopt -s expand_aliases
alias javac="cmd.exe /c javac"
alias java="cmd.exe /c java"

rm -f *.class

javac structure.java
javac entity.java
javac chessGame.java
javac Test.java
javac chessGameGUI.java

java chessGameGUI
