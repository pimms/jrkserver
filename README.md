# Radio on the Internet
Putting radio on the internet. Again.

The ROI server is a server that plays MP3-files from an AWS-S3 bucket in
shuffle+repeat, and serves them over HLS along with additional metadata over
HTTP(S).


## Required configuration
The project does NOT work straight out of the box, some tinkering is required.

#### AWS Credentials
The credentials are typically looked for in the home-directory of the user
under which the process is running. See AWS SDK documentation for instructions
on how to set this up.

#### Stream Configuration
The ROI server is completely generic, and expects you to define the channel
name and other configurable properties. There's a placeholder file named
`TODO.properties` in the `resources/` directory of the Kotlin-project that you
must rename to `config.properties` and fill with proper values.

You also need to add an image to the resources-directory named
'stream-picture.png'.  The image **should** be square and large enough to be
used to fill the width of a modern smart-phone without looking poorly.


#### S3 Content
When looking in the given S3-bucket, the ROI expects files of the following
naming scheme: `/yyyyMMdd.mp3/`. This is an application developed solely for
podcast-playback, and episodes should therefore be named as such.


#### Building and pushing to Docker
The script `build-push.sh` builds the Docker-image and attempts to push it to a
Docker repository pointed to by the envionment variable `$ECS_DOCKER_REPO`.


## What, why.
ROI is an app invented to reinvent radio, again, but not really, because all of
this really did exist before.

I started this application because I'm unhealthily fond of Radioresepsjonen,
and the official channels don't offer exactly what I want; a radio channel
playing every single episode they have ever made, in a shuffled order, just
like an actual radio channel. I'm fairly certain that I can't distribute or
publish this application as a "Radioresepsjonen player", so both the server and
client are completely generic, and can in theory be used to play anything.

This app also works as a tool for me to learn iOS-development _(which is why
I'm adding features that serve virtually no benefit to anyone)_ and further
hone my backend-blade. Mostly though, I'm just scratching my own RR-itch.

