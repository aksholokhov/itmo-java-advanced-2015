#!/bin/sh
    javadoc -classpath artifacts/*:lib/* -sourcepath src/ -d Documentation/ -linkoffline http://java.sun.com/javase/7/docs/api/ http://java.sun.com/javase/7/docs/api/  -private ru.ifmo.ctddev.sholokhov.implementor info.kgeorgiy.java.advanced.implementor