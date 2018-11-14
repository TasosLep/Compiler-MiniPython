#!/bin/bash

if [ "$1" != "" ]; then
	directory=minipython;
	if [ -d "$directory" ]; then
		echo "deleting the $directory directory...";
		rm -rf "$directory";
		echo "deleted";
	fi
	./sablecc "$1";
fi

