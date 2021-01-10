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

import org.jetbrains.annotations.NotNull;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.cadence.CadenceSegment;
import org.linguafranca.sound.tone.Tone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Coordinates the playback of CadenceSections, looping and truncating as necessary to fulfill the timings specified
 * in the section and its segments.
 *
 * @author jo
 */
public class CadenceSectionPlayback {

    private static final Logger logger = LoggerFactory.getLogger(CadenceSectionPlayback.class);

    // a monitor to allow us to wait for sounds to complete, also to allow cancellation
    private final Object cancelPlaybackGuard = new Object();
    private volatile boolean cancelPlaybackRequested = false;
    private final HashMap<Tone, EncodedTone> cache = new HashMap<>();

    /**
     * Stop the playback of anything currently being played
     */
    public void cancelPlayback() {
        synchronized (cancelPlaybackGuard) {
            cancelPlaybackRequested = true;
            cancelPlaybackGuard.notify();
        }
    }

    private static int getAdjustedDuration(int desiredDuration, int timeRemaining) {
        return Math.min(desiredDuration, timeRemaining);
    }

    private static long getTimeElapsed(long timeStarted) {
        return System.currentTimeMillis() - timeStarted;
    }

    private static int getTimeRemaining(long timeStarted, int overallDuration) {
        return overallDuration - (int) getTimeElapsed(timeStarted);
    }

    /**
     * Play the cadences in the supplied section for the duration specified in the section, looping if
     * necessary till the time is used up, or truncating if the inherent length of the section
     * is longer than the time specified in the section itself.
     *
     * @param section a Cadence Section
     * @return true if the playback completed without interruption
     */
    public boolean play(@NotNull CadenceSection section) {
        long timeStarted = System.currentTimeMillis();

        int timeRemaining;
        while ((timeRemaining = getTimeRemaining(timeStarted, section.getDuration())) > 0) {
            if (!play(timeRemaining, section)) {
                break;
            }
        }
        return !cancelPlaybackRequested && (timeRemaining <= 0);
    }

    /**
     * Play the cadences in the supplied section for at most the duration specified. The duration supplied in the
     * section itself is not examined.
     * @param durationMillis the time to play, truncating playback if necessary
     * @param section a section to play
     * @return true if playback completed without interruption
     */
    public boolean play(int durationMillis, @NotNull CadenceSection section) {

        synchronized (cancelPlaybackGuard) {
            cancelPlaybackRequested = false;
        }

        logger.trace("Looping over cadence list with {} to go", durationMillis);
        for (CadenceSegment segment : section) {

            int onDuration = segment.getOnDuration();
            // if there are sounds to make (this is also a proxy for onDuration == 0)
            if (segment.getTones().size() > 0) {
                // -1 means indefinite i.e. length of section
                if (onDuration < 0) {
                    onDuration = section.getDuration();
                }
                onDuration = getAdjustedDuration(onDuration, durationMillis);
                // TODO not sure if this is quite right - check condition when onDuration is 0
                // i.e. add && segment.onDuration != 0 or above if condition would do
                if (onDuration <= 0) {
                    return true;
                }

                // make the noise suggested by each cadence
                play(onDuration, EncodedTone.encodeTones(segment, cache));
            }

            // return if we are trying to stop
            if (cancelPlaybackRequested) {
                return false;
            }

            // skip if there is no silence section
            if (segment.getOffDuration() == 0) {
                continue;
            }

            // otherwise sleep in silence
            try {
                durationMillis = durationMillis - onDuration;
                int offDuration = getAdjustedDuration(segment.getOffDuration(), durationMillis);
                if (offDuration <= 0) {
                    return true;
                }

                logger.trace("Sleeping for offDuration {}", offDuration);
                synchronized (cancelPlaybackGuard) {
                    cancelPlaybackGuard.wait(offDuration);
                }

                // return if we are trying to stop
                if (cancelPlaybackRequested) {
                    return false;
                }

            } catch (InterruptedException ignored) {
                logger.trace("Interrupted from sleep");
                return false;
            }
        }
        logger.trace("Finished the section");
        return true;
    }

    /**
     * Play to tones supplied for the duration specified, or until stopped. The inherent length of the tones
     * specified is ignored, they are all played at the same length.
     *
     * @param durationMillis the duration of the tones
     * @param encodedTones a list of tones to play (simultaneously)
     */
    public void play(int durationMillis, @NotNull ArrayList<EncodedTone> encodedTones) {

        checkArgument(durationMillis > 0, "Duration must be greater than 0");
        // start playing each of the playableTones
        for (EncodedTone encodedTone : encodedTones) {
            logger.trace("Starting Clip for {}", encodedTone);
            encodedTone.play(durationMillis);
        }

        try {
            final long start = System.currentTimeMillis();
            long timeRemaining;
            while ((timeRemaining = getTimeRemaining(start, durationMillis)) > 0 && !cancelPlaybackRequested) {
                logger.trace("Waiting for {}", timeRemaining);
                synchronized (cancelPlaybackGuard) {
                    cancelPlaybackGuard.wait(timeRemaining);
                }
            }
            logger.trace("Time left is {}", getTimeRemaining(start, durationMillis));

        } catch (InterruptedException e) {
            logger.trace("Interrupted while playing playableTones");
        }

        for (EncodedTone encodedTone : encodedTones) {
            logger.trace("Stopping Clip for {}", encodedTone);
            encodedTone.stop();
        }
    }
}
