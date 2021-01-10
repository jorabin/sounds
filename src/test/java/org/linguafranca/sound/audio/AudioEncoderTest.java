/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.linguafranca.sound.audio;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

/**
 * @author jo
 */
public class AudioEncoderTest {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * a utility to play an audio input stream
     * @param millis duration
     * @param stream the stream
     * @throws InterruptedException thrown by javax.sound
     * @throws IOException thrown by javax.sound
     * @throws LineUnavailableException thrown by javax.sound
     */
    public void playStream(int millis, AudioInputStream stream) throws InterruptedException, IOException, LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(event -> logger.info("Event {}", event));

        clip.open(stream);
        clip.setFramePosition(0);

        // how many loops needed to play the sound
        long loops = (millis * 1000L / clip.getMicrosecondLength());
        clip.loop((int) (loops > 0 ? loops : 1));
        logger.info("Loops {}, Micro second length {}", loops, clip.getMicrosecondLength());

        // play the sound
        clip.start();
        Thread.sleep(millis);
        if (clip.isActive()) {
            logger.info("Stopping clip, as it is still active");
            clip.stop();
        }
        clip.close();

        logger.info("Clip is done");

    }

    @Before
    public void setUp() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);
    }

    @Test
    public void play() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(440, 0.25, 200));
    }

    @Test
    public void play2() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(WaveGenerator.Waveform.SQUARE, 440, 0.25,200));
    }

    @Test
    public void play3() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(WaveGenerator.Waveform.TRIANGLE, 440, 0.25, 200));
    }

    @Test
    public void play4() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(WaveGenerator.Waveform.SAWTOOTH, 440, 0.25, 200));
    }


    @Test
    public void play5() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(WaveGenerator.Waveform.RANDOM, 440, 0.25,200));
    }

    @Test
    public void play6() throws LineUnavailableException, InterruptedException, IOException {
        playStream(5000, AudioEncoder.createSample(WaveGenerator.Waveform.SINE, 1100, 0.8, 200));
    }
}