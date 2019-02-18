# JRK - Joakims Rikskringkasting

The JRK Server is a server that plays MP3-files from an AWS-S3 bucket in
shuffle+repeat, and serves them over HLS along with additional metadata over
HTTP(S).

Regardless of you're looking for, this app almost certaintly is not what you
need.

## Required configuration
The project does NOT work straight out of the box, some tinkering is required.

#### AWS Credentials
The credentials are typically looked for in the home-directory of the user
under which the process is running. See AWS SDK documentation for instructions
on how to set this up.

#### Stream Configuration
The JRK server is completely generic, and expects you to define the channel
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


