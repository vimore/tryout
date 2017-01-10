#!/usr/bin/env bash


mvn dependency:build-classpath -Dmdep.pathSeparator=":" -Dmdep.prefix='' -Dmdep.fileSeparator=":" -Dmdep.outputFile=classpath.file.txt  >/dev/null 2>&1


function morphline_classpath () {
  perl -ane 'while ($_=~/:([^:]+):/g){print $1, "\n";}' < classpath.file.txt | while read jarfile; do find ~/.m2/repository/ -name $jarfile; done | perl -ane 'chomp; $out.=":".$_;END{print $out, "\n";}';
}


echo  $(morphline_classpath)
