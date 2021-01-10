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

package org.linguafranca.sound.player;

import org.junit.Before;
import org.junit.Test;
import org.linguafranca.sound.cadence.CadenceScript;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.cadence.Cadences;
import org.linguafranca.sound.cadence.ToneScriptParser;
import org.slf4j.LoggerFactory;

/**
 * @author jo
 */
public class MidiCadencePlayerTest {


    @Before
    public void setUp() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.TRACE);
    }


    @Test
    public void testMidi() throws Exception {
        MidiCadencePlayer mcp = new MidiCadencePlayer();
        mcp.openSound();
        CadenceScript cs = ToneScriptParser.parseToneScript(Cadences.CALL_PROGRESS.get("uk_dial"));
        for (CadenceSection cadence : cs.getCadences())
            mcp.queue(new QueueItem<>(cadence), true);
        mcp.closeSound(10);
    }

    @Test
    public void testMidi2() throws Exception {
        MidiCadencePlayer mcp = new MidiCadencePlayer();
        mcp.openSound();
        CadenceScript cs = ToneScriptParser.parseToneScript(Cadences.CALL_PROGRESS.get("uk_SIT"));
        for (CadenceSection cadence : cs.getCadences())
            mcp.queue(new QueueItem<>(cadence), true);
        mcp.closeSound(10);
    }
}