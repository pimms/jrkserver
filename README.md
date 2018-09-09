# Joakims Rikskringkasting Server

An audio-streaming server for shuffling a set of MP3-files stored in AWS S3
over HLS.


## y tho

Primarily for being able to binge old episodes of radio shows that are no
longer available through mainstream channels. Could maybe be used for something
else - not sure.


## Required configuration

The project does NOT work straight out of the box. First of all, you are
obviously going to need some credentials for AWS. Secondly, you need to rename
the file `TODO.properties` to `config.properties` and put in your actual bucket
name. There might be some other values in there that you should change to
something more appropriate as well.

And of course you're going to need a lot of media hosted in your S3 :)

Also this readme is wildly not up to date.
