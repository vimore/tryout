#!/bin/bash

for testscript in *_test.py; do
  python $testscript;
done
