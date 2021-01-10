# Sounds

## Overview

This is a system for generating and playing tones, with the original motivation of playing telephony noises.

It implements the Sipura/LinkSys/Cisco ToneScript language for defining the sounds to make &mdash; and implements sound audio generation and playback of those sounds. Playback can be done in both blocking and unblocking ways.

It was written a really long time ago (see below). If I'd known about Processing and Minim when I wrote it I would probably have done it differently. 

Anyway, I decided now (Jan 2021) would be a good time put it into the public domain before it's overtaken by any more events.

It is licensed under the Apache 2 License and is currently usable.

    The work is provided on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or
    implied, including, without limitation, any warranties
    or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY,
    or FITNESS FOR A PARTICULAR PURPOSE.

    You are solely responsible for determining the appropriateness
    of using or redistributing the Work and assume any risks
    associated with Your exercise of permissions under this License.

See [license](#license).

## How it works

Sounds are created with factories capable of generating Sine, Square, Triangle, Sawtooth and Random waveforms. These are PCM encoded and readied for playback using [`java.sound.sampled.Clip`](https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Clip.html). These can be played directly or may be combined using [`org.lingafranca.sounds.cadence.CadenceScript`](./src/main/java/org/linguafranca/sound/cadence/CadenceScript.java) and related classes, which define which tones are to be played in what sequence and playback is handled 

Playback itself is coordinated by the [`org.linguafranca.sound.audio.CadenceSectionPlayback`](./src/main/java/org/linguafranca/sound/audio/CadenceSectionPlayback.java) class which can be invoked directly via its `play` methods. For non-blocking operation the `PlaybackThread` class is run as a separate thread &mdash; and the class [`org.linguafranca.sound.audio.WaveCadencePlayer`](./src/main/java/org/linguafranca/sound/player/WaveCadencePlayer.java) can be used to enqueue cadences to be played on that thread.

Class [`org.linguafranca.sound.cadence.ToneScriptParser`](src/main/java/org/linguafranca/sound/cadence/ToneScriptParser.java) parses ToneScript (see below) and creates Cadences that may then be played.

## Quick Start

### Make and Play a Raw Tone 

See [`AudioEncoderTest.java`](./src/test/java/org/linguafranca/sound/audio/AudioEncoderTest.java)

### Play a Tone

See [`EncodedToneTest.java`](./src/test/java/org/linguafranca/sound/audio/EncodedToneTest.java)

### Synchronous Playback of a Script, Using Playback

See [`CadenceSectionPlaybackTest.java`](./src/test/java/org/linguafranca/sound/audio/CadenceSectionPlaybackTest.java)


### Synchronous Playback of a Script, Using WaveCadencePlayer

See [`WaveCadencePlayerTest.java`](./src/test/java/org/linguafranca/sound/player/WaveCadencePlayerTest.java)

### Asynchronous Playback of a Script, Using WaveCadencePlayer

See [`WaveCadencePlayerTest.java`](./src/test/java/org/linguafranca/sound/player/WaveCadencePlayerTest.java)

## Original Motivation

This all started a long time ago (2011, to be exact) when I once owned a [Sipura 3102](https://www.cisco.com/c/en/us/support/unified-communications/spa3102-voice-gateway-router/model.html) telephony adaptor. Cutting a long story short, this ~~is~~ was a little box that you can stick on your Ethernet network and provides a SIP telephony interface, as well as an interface to the old-fashioned analogue land-line network. 

After I bought a [FritzBox](https://en.avm.de/products/fritzbox/) as my main router, with IP and analogue telephony all built in, the SPA got relegated to a drawer, and some portion of geekery disappeared from my life. Thank goodness, really, since it was a bear to configure, and dragged me into looking at [Asterisk](https://www.asterisk.org/) and other things you're better off not knowing about, compared with going out and getting exercise in the fresh air.

Anyway, before Fritzbox arrived, I thought about how one might provide a usable interface for configuring the SPA3102, and as part of that figuring out what kind of call progress sounds you want to configure using what subsequently showed up on Wikipedia as "[ToneScript](https://en.wikipedia.org/wiki/ToneScript)" configuration notation.

Part of the problem was that it wasn't described in the SPA3102 manual &mdash; and part was that even if you did understand it, configuring the tones in the device and then deciding that you didn't like the sound was quite a faff, round-trip irritation, so why not make a little testbed in which you could try this all out and tweak away to your heart's content, then load it up. 

What follows is a discussion of what I developed to do that, though not why. Anyway, I feel I know more about sounds and Java sounds in particular, and that might well stand me in good stead one day.

By the way, if you do have a SPA3102, and you'd like some introductory material relating to setting it up on the UK telephony network, I found [Andrew Oakley's discussion](http://www.aoakley.com/articles/2008-01-08.php) very helpful.

## ToneScript

I have [reformatted and tidied](./spec.md) the original Cisco documentation and wrote a BNF for Tonescript [see below](#tonescript-bnf). 

The original documentation is [Small Business Systems Configuration Guide (pdf)](https://www.cisco.com/c/dam/en/us/td/docs/voice_ip_comm/csbpvga/ata/provisioning/guide/Provisioning.pdf) (under section "Data Types").

It looks somewhat like this, as an example:

    350@-19,440@-19;2(.1/.1/1+2);10(*/0/1+2)

Which means:


    Number of Frequencies = 2
    
    Frequency 1 = 350 Hz at –19 dBm
    Frequency 2 = 440 Hz at –19 dBm
    
    Number of Cadence Sections = 2
    
    Cadence Section 1: Section Length = 2s
    Number of Segments = 1
    Segment 1: On=0.1s, Off=0.1s with Frequencies 1 and 2
    
    Cadence Section 2: Section Length = 10s
    Number of Segments = 1
    Segment 1: On=forever, with Frequencies 1 and 2


### <a name="tonescript-bnf">ToneScript BNF</a>
I made a little [BNF](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form) for it, as follows:


    // An optional list of freqScript (up to 6 in total), followed by a cadenceScript
    <toneScript>   ::= [{<freqScript>','}*5<freqScript>';']<cadenceScript>

    // a comma separated list of tones  (up to 6)
    <freqScript>     ::= <tone>{','<tone>}*5

    // a tone is the combination of a frequency and a volume
    <tone>           ::= <frequency>'@'<dBm>

    // a frequency in Hertz  (must be an integer)
    <frequency>      ::= <integer>

    // a loudness value -35 is very soft, 0 is the loudest distinguishable, one decimal place is allowed
    <dBm>            ::= <float>


    // a script is one or two sections
    <cadenceScript>  ::= <cadenceSection>[';'<cadenceSection>]

    // a section is a duration followed by a comma separated list up to 6 segments
    <cadenceSection> ::= <duration>'('<cadenceSegment>'{,<cadenceSegment>}*5)'

    // the duration to make the sound for - with a resolution of 1 ms, specified in secs
    // the segments are repeated, in order, till the duration of the section has elapsed 
    <duration>       ::= <float>


    // if <freqScript> is present - a <toneCadence> must be used.
    // if <freqScript> is not present then the third element of a <toneCadence> if present is ignored.
    // not clear if it should be ignored or if that's an error
    <cadenceSegment>        ::= <toneCadence> | <nonToneCadence> 

    // meaning sound-on/sound-off in secs ('*' for the on-value means for the <duration> of the cadence)
    <nonToneCadence> ::= (<duration> | '*') '/' <float> 

    // meaning sound-on/sound-off/tones-to-play
    <toneCadence>    ::= <nonToneCadence>'/'<toneList>

    // a plus separated list of indexes into the list of <tones>
    // nothing is done about duplicates - not clear if that is an error
    <toneList>       ::= <toneIndex>{'+'<toneIndex>]}*5

    // referencing the earlier <tones> 1 is the first
    <toneIndex>      ::= <integer>

## Dependencies

It's written for Java 1.8.

Has a dependency on Google Guava.


## Change Log

See [this file](./changelog.md)

##  <a name="license">License</a>

Copyright (c) 2021 Jo Rabin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.