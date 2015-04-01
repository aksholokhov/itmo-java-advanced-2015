#!/bin/sh
export CLASSPATH="lib/*;Task6.jar;ParallelMapperTest.jar"
java info.kgeorgiy.java.advanced.mapper.Tester scalar ru.ifmo.ctddev.sholokhov.iterativeparallelism.ParallelMapperImpl,ru.ifmo.ctddev.sholokhov.iterativeparallelism.IterativeParallelism $1
