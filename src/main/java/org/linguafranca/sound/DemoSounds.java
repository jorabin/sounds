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

package org.linguafranca.sound;

import org.linguafranca.sound.cadence.CadenceScript;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.cadence.ToneScriptParser;
import org.linguafranca.sound.player.CadencePlayer;
import org.linguafranca.sound.player.QueueItem;
import org.linguafranca.sound.player.WaveCadencePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.linguafranca.sound.cadence.Cadences.CALL_PROGRESS;


/**
 * Created by IntelliJ IDEA.
 * User: Jo
 * Date: 26/11/2011
 * Time: 16:39
 *
 */
public class DemoSounds {

    public Logger logger;
    private final CadencePlayer waveCadencePlayer;
    private final QueueItem.QueueItemUpdateListener<CadenceSection> queueItemUpdateListener;

    public DemoSounds() {
        logger = LoggerFactory.getLogger(DemoSounds.class);
        waveCadencePlayer = new WaveCadencePlayer();
        queueItemUpdateListener = item -> logger.info(item.toString());
    }


    public void dial(String what){
        logger.info("Dialling {}", what);
        waveCadencePlayer.dial(what);
    }

    public void play(String what) {
        try {
            play(what, false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void play(String what, boolean block) throws Exception {
        play(ToneScriptParser.parseToneScript(what), block);
    }

    public void play(CadenceScript cadenceScript, boolean block) {
        logger.info("Queueing {}" , cadenceScript.getDescription());
        for (CadenceSection cs: cadenceScript.getCadences()){
            QueueItem<CadenceSection> qi = new QueueItem<>(cs);
            qi.addCompletionListener(queueItemUpdateListener);
            waveCadencePlayer.queue(qi, block);
        }
    }

    public void playSIT() {
        playSIT(false);
    }

    public void playSIT(boolean block) {
        try {
            play("950@-12,1400@-12,1800@-12;1(.33/0/1,.33/0/2,.33/0/3, 0/1/0)", block);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void openSound(){
        logger.info("Opening Sound");
        try {
            waveCadencePlayer.openSound();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    public void stopSound() {
        logger.debug("Stopping current sound");
        boolean waveStopped;
        if (!(waveStopped = waveCadencePlayer.stopSound(100))) {
            logger.warn("Could not stop sound on tone player");
        }
        if (waveStopped){
            logger.info("Sound stopped");
        }
    }

    public void close() {
        logger.info("Closing Sounds");
        waveCadencePlayer.closeSound(300);
    }

    public static void main(String[] argv) throws Exception {

        DemoSounds sounds = new DemoSounds();

        sounds.openSound();

        sounds.dial("02089950859");

        sounds.play("10(.2/.2,.2/.2,.2/.2,1/4)");

    /*    for (Map.Entry<String, String> entry : CALL_PROGRESS.entrySet()) {
            sounds.play(ToneScriptParser.parseToneScript(entry.getKey(), entry.getValue()), true);
        }
*/
        sounds.close();

        System.out.println("Exit");
        System.exit(0);

    }
}
