SRC := $(shell find src -name '*.java')

.PHONY: build run clean
build:
	mkdir -p out
	javac --release 21 -d out $(SRC)

run: build
	java -cp out httpserver.Main

clean:
	rm -rf out
