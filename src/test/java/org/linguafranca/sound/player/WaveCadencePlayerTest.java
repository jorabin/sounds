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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jo
 */
public class WaveCadencePlayerTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() {
        // Programmatically set the logging level to TRACE
        // ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.TRACE);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.linguafranca.sound.audio")).setLevel(ch.qos.logback.classic.Level.TRACE);
    }

    @Test
    public void playSync() {
        WaveCadencePlayer player = new WaveCadencePlayer();

        player.openSound();
        try {
            CadenceScript cs = ToneScriptParser.parseToneScript("Stutter Tone", "350@-19,440@-19;2(.1/.1/1+2);10(*/0/1+2)");
            for (CadenceSection cadence : cs.getCadences())
                player.queue(cadence, true);
        } finally {
            player.closeSound(100);
        }
    }

    public void play (String description, String text) throws Exception {
        // it's an array because lambdas need (effectively) final variables, but we still need to set the condition
        final Boolean[] flag = {false};
        final Object lock = new Object();

        // you can add a listener to the queued items to get event updates
        QueueItem.QueueItemUpdateListener<CadenceSection> listener = event -> {
            logger.info(event.toString());
            if (event.isFinished()) {
                // signal calling thread that we are done
                synchronized (lock) {
                    flag[0] = true;
                    lock.notify();
                }
            }
        };

        WaveCadencePlayer player = new WaveCadencePlayer();
        player.openSound();
        try {
            CadenceScript script = ToneScriptParser.parseToneScript(description, text);
            for (CadenceSection section : script.getCadences()) {
                QueueItem<CadenceSection> item = player.queue(section, false);
                item.addCompletionListener(listener);
            }
            // go off and do something else ..
            logger.info("Doing something very interesting and exciting");
            // got bored now I need to wait for the music to stop (sic)
            synchronized (lock) {
                logger.info("Start Waiting");
                if (flag[0]) {
                    logger.warn("Unexpected early completion before wait");
                } else {
                    lock.wait(11000);
                }
                if (!flag[0]) {
                    logger.warn("Looks like we woke up without completion ...");
                }
            }
            logger.info("Finished waiting");
        } finally {
            player.closeSound(100);
        }
    }


    @Test
    public void playAsync() throws Exception {
        play( "test", "350@-21,440@-19;10(*/0/1+2)");
    }

    @Test
    public void playAsync2() throws Exception {
        play( "uk-SIT", Cadences.CALL_PROGRESS.get("uk_SIT"));
    }

    @Test
    public void playAsync3() throws Exception {
        play("ringer", "10(.2/.2,.2/.2,.2/.2,1/4)");
    }
}