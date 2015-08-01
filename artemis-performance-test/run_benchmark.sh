#/bin/bash

java -jar target/benchmarks.jar -f 1 -gc false -rf json -rff bench-$1-$(date -d "today" +"%Y%m%d%H%M").json