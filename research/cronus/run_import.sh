#!/bin/bash

pig -p environment=research \
    -p datefunc=ISOToHour \
    -p filter=hour \
    -p year=2014 \
    -p month=06 \
    -p day=25 \
    -p hour=01 \
    import_data.pig 
