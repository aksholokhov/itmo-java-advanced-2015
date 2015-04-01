#!/bin/sh
export CLASSPATH="lib/*;Task6.jar;IterativeParallelismTest.jar"
java info.kgeorgiy.java.advanced.concurrent.Tester scalar ru.ifmo.ctddev.sholokhov.iterativeparallelism.IterativeParallelism $1
