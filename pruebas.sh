#!/bin/sh
# Pruebas automáticas de lógica y de contrato REST. Solo necesita un JDK 17 o superior (no usa Spring, ni Maven, ni PostgreSQL):
# compila el dominio y los servicios (con tablas en memoria) y las pruebas.
cd "$(dirname "$0")"
rm -rf out; mkdir -p out
javac -encoding UTF-8 -d out $(find backend/src/main/java -name "*.java" -not -path "*/spring/*" -not -path "*/persistencia/jdbc/*") $(find backend/src/test/java -name "*.java") || exit 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out co.itm.prestamo.PruebasFlujo || exit 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out co.itm.prestamo.PruebasApi || exit 1
