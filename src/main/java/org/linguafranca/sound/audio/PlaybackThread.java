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

import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.player.QueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class PlaybackThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PlaybackThread.class);

    private final CadenceSectionPlayback sectionPlayback = new CadenceSectionPlayback();

    private volatile Boolean stopThreadRequested;

    private final ArrayBlockingQueue<QueueItem<CadenceSection>> playbackQueueItems;

    public PlaybackThread() {
        super("Player Thread");
        stopThreadRequested = false;
        playbackQueueItems = new ArrayBlockingQueue<>(16);
    }

    /**
     * Stop the playback thread
     * @param timeToWait in milliseconds
     * @return true if the thread stopped before time out
     */
    public boolean stopThread(int timeToWait) {

        stopThreadRequested = true;
        // the following interrupt will stop the current sound, but playback might then continue
        sectionPlayback.cancelPlayback();
        this.interrupt();

        if (timeToWait > 0) {
            try {
                this.join(timeToWait);
            } catch (InterruptedException e) {
                throw new RuntimeException("Did not expect to be interrupted waiting for player thread to exit");
            }
        }
        return !this.isAlive();
    }

    /**
     * Stop the cadence currently being played.
     */
    public void cancelPlayback() {
        drainQueue();
        sectionPlayback.cancelPlayback();
    }

    /**
     * Enqueue an item for playback
     * @param queueItem a CadenceSection wrapped in a QueueItem
     * @return true if the item was queued, false if not
     */
    public boolean queue(QueueItem<CadenceSection> queueItem) {
        // don't queue if we are shutting down
        if (stopThreadRequested) return false;
        // if the queue was full then we didn't do it
        return playbackQueueItems.offer(queueItem);
    }


    private void drainQueue() {
        ArrayList<QueueItem<CadenceSection>> queueItems = new ArrayList<>();
        playbackQueueItems.drainTo(queueItems);
        for (QueueItem<CadenceSection> queueItem : queueItems) {
            logger.trace("Discarding queueItem {}", queueItem.getItem());
            queueItem.setAbandoned();
        }
    }

    /**
     * Returns an approximation of how long the queue to queue is in millis
     *
     * @return a value in millis
     */
    public long getQueueLengthMillis() {
        long result = 0;
        // we're OK to iterate over this because it will give consistent results even if it is modified on another thread
        for (QueueItem<CadenceSection> queueItem : playbackQueueItems) {

            result += queueItem.getItem().getDuration();
        }
        return result;
    }


    @Override
    public void run() {
        logger.info("Player Thread Started");
        try {
            while (!stopThreadRequested) {

                QueueItem<CadenceSection> nextQueueItem = null;
                while (nextQueueItem == null) {
                    try {
                        logger.trace("Waiting for a candidate");
                        nextQueueItem = playbackQueueItems.take();
                        logger.trace("Got a candidate {}", nextQueueItem.getItem().toString());
                    } catch (InterruptedException e) {
                        nextQueueItem = null;
                    }
                    if (stopThreadRequested) {
                        break;
                    }
                }
                // if at this point candidate is null then we are trying to stop
                if (nextQueueItem == null) break;

                nextQueueItem.setStarted();
                if (sectionPlayback.play(nextQueueItem.getItem())) {
                    logger.trace("Finished candidate {}", nextQueueItem.getItem());
                    nextQueueItem.setFinished();
                } else {
                    logger.trace("Abandoned candidate {}", nextQueueItem.getItem());
                    nextQueueItem.setAbandoned();
                }
            }
        } finally {
            logger.info("Player thread exit");
            // anyone waiting on cadences yet to be played gets notified that this isn't going to happen
            this.drainQueue();
        }
        if (!stopThreadRequested) {
            logger.error("Thread stopping without being asked");
            throw new IllegalStateException("Player thread stopped without being asked");
        }
    }

}
