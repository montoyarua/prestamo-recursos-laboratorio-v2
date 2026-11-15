#!/bin/sh
# Arranca la aplicación Spring Boot (necesita JDK 21, Maven y PostgreSQL con la base «prestamos»; ver README).
cd "$(dirname "$0")/backend" && mvn spring-boot:run
