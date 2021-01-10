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

package org.linguafranca.sound.player;

import ch.qos.logback.classic.Logger;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.audio.PlaybackThread;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Jo
 * Date: 26/12/2011
 * Time: 17:19

 */
public class WaveCadencePlayer extends CadencePlayer.Skeleton {

    /** How much longer to sleep in millis beyond the nominal queue time if queue not finished */
    public static final int GRACE_PERIOD = 2000;

    private final Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(WaveCadencePlayer.class);

    private PlaybackThread player;

    public WaveCadencePlayer(){
        player = new PlaybackThread();

    }

    @Override
    public void openSound() {
        player.start();
    }

    @Override
    public QueueItem<CadenceSection> queue(CadenceSection section, boolean block) {
        QueueItem<CadenceSection> item = new QueueItem<>(section);
        queue(item, block);
        return item;
    }

    @Override
    public void queue(QueueItem<CadenceSection> queuedCadences, boolean block) {

        logger.trace("Queueing item {}", queuedCadences.getItem());
        if (! player.queue(queuedCadences)) return /* TODO false */;
        if (block){
            try {
                logger.trace("Waiting for queued item to play");
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (queuedCadences){
                    while (!queuedCadences.isFinished() && !queuedCadences.isAbandoned()){
                        if (queuedCadences.isStarted()){
                            logger.trace("Item is playing {}", queuedCadences.getItem());
                        } else {
                            logger.trace("Item has not played yet {}", queuedCadences.getItem());
                        }
                        queuedCadences.wait(player.getQueueLengthMillis() + queuedCadences.getItem().getDuration() + GRACE_PERIOD);
                    }
                    if (queuedCadences.isFinished()){
                        logger.trace("Item finished {}", queuedCadences.getItem());

                    } else if (queuedCadences.isAbandoned()) {
                        logger.trace("Item abandoned {}", queuedCadences.getItem());
                    }
                }
            } catch (InterruptedException e) {
                logger.trace("not expecting to be interrupted while playing sound (blocking)");
            }
        }
    }

    @Override
    public boolean stopSound(int timeToWait) {
        if (player == null || !player.isAlive()) {
            return true;
        }
        logger.debug("Player is alive, trying to stop sound");
        player.cancelPlayback();
        return true;
    }

    @Override
    public boolean closeSound(int timeToWait) {
        if (player == null || !player.isAlive()) {
            player = null;
            return true;
        }

        logger.debug("Player is alive, trying to stop thread");
        boolean stopped = player.stopThread(timeToWait);
        player = null;
        return stopped;
    }
}
