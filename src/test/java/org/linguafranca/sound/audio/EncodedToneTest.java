/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.linguafranca.sound.audio;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.junit.Test;
import org.linguafranca.sound.tone.Tone;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;

import java.io.IOException;

/**
 * @author jo
 */
public class EncodedToneTest {

    org.slf4j.Logger logger = LoggerFactory.getLogger(EncodedToneTest.class);

    @Before
    public void setUp(){
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);
    }

    @Test
    public void makePlayableToneTest() throws LineUnavailableException, InterruptedException, IOException {
        // make a playable tone of the dimensions specified below
        int duration = 1000;
        Tone tone = Tone.makeToneFA(440, 0.25);
        EncodedTone encodedTone = EncodedTone.encodeTone(tone, duration);

        // add a listener to see what's going on
        encodedTone.getClip().addLineListener(event -> logger.info("Event {}", event));
        // play (this is async)
        encodedTone.play();

        // sleep to wait for finish
        Thread.sleep(duration);
        // the clips generated are often longer - sometimes much longer - than requested
        if (encodedTone.getClip().isActive()) {
            encodedTone.stop();
        }
    }

}