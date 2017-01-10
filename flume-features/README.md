Flume features
============

E8 components dedicated to flume such interceptors, sink, ....

# interceptos


# sinks


## hbase sink
hbase sink relies on protobuf, which need a local install of the compiler

### build protobuf
~~~
wget https://protobuf.googlecode.com/files/protobuf-2.5.0.tar.gz
tar xvzf protobuf-2.5.0.tar.gz 
cd protobuf-2.5.0
./configure CC=clang CXX=clang++ CXXFLAGS='-std=c++11 -stdlib=libc++ -O3 -g' LDFLAGS='-stdlib=libc++' LIBS="-lc++ -lc++abi"
make -j 4 
sudo make install
~~~

### setup toolchains
1. mvn version: 3.0.5
    > NOTE: protobuf plugin requires mvn 3.0.5 to work. The latest version 3.3.3 does not work.
2. create in your .m2 directory a file named toolchains.xml
   
    ```
    $ cat ~/.m2/toolchains.xml
    
    <?xml version="1.0" encoding="UTF-8"?>
    <toolchains>
      <toolchain>
        <type>protobuf</type>
        <provides>
          <version>2.5</version>
        </provides>
        <configuration>
          <protocExecutable>/usr/local/bin/protoc</protocExecutable>
        </configuration>
      </toolchain>
    </toolchains>
    ```
