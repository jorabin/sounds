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

import org.junit.Before;
import org.junit.Test;
import org.linguafranca.sound.cadence.*;
import org.slf4j.LoggerFactory;

/**
 * @author jo
 */
public class CadenceSectionPlaybackTest {


    @Before
    public void setUp() {
        // Programmatically set the logging level to TRACE
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.TRACE);
    }

    @Test
    public void play() {
        CadenceSectionPlayback playback = new CadenceSectionPlayback();

        CadenceScript script = ToneScriptParser.parseToneScript("UK Dial Tone", "350@-21,440@-19;10(*/0/1+2)");
        for (CadenceSection section : script.getCadences()) {
            playback.play(section);
        }
    }

    @Test
    public void play2()  {
        CadenceSectionPlayback playback = new CadenceSectionPlayback();

        CadenceScript script = ToneScriptParser.parseToneScript("UK SIT", Cadences.CALL_PROGRESS.get("uk_SIT"));
        for (CadenceSection section : script.getCadences()) {
            playback.play(section);
        }
    }

    @Test
    public void playDTMF() {
        CadenceSectionPlayback playback = new CadenceSectionPlayback();

        for (DTMF.Digit d : DTMF.Digit.values()) {
            CadenceSection pt = d.getCadences();
            playback.play(pt);
        }
    }


}