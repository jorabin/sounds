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

import org.linguafranca.sound.tone.Tone;
import org.linguafranca.sound.cadence.CadenceSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A tone that can be rendered
 * <p>
 * The tone's Clip may or may not contain sufficient info to fulfil the duration,
 * in which case you'd need to loop the Clip to fulfil the contract as per {@link #play(int)}
 */
@Immutable
public class EncodedTone {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncodedTone.class);

    private final Tone tone;
    private final int durationInMillisecs;
    private final Clip clip;

    private EncodedTone(Tone tone, int durationInMillisecs, Clip clip) {
        this.tone = tone;
        this.durationInMillisecs = durationInMillisecs;
        this.clip = clip;
    }

    /**
     * play the tone for its inherent time. If that is indefinite (-1) the loop plays indefinitely
     */
    public void play() {
        play(durationInMillisecs);
    }

    /**
     * play the tone for the time specified
     */
    public void play(int millis) {
        clip.setMicrosecondPosition(0);
        int loops = (int)(millis * 1000L / clip.getMicrosecondLength());
        clip.loop(loops == 0 ? 1 : loops);
    }

    /**
     * Make a set of EncodedTones appropriate to queue a CadenceSegment.
     *
     * @param c the cadence to be played
     * @return an array of EncodedTone
     * @throws RuntimeException if something ghastly has happened that you won't be able to fix
     */
    public static ArrayList<EncodedTone> encodeTones(CadenceSegment c, HashMap<Tone, EncodedTone> cache) {
        ArrayList<EncodedTone> encodedTones = new ArrayList<>(2);
        for (Tone t : c.getTones()) {
            try {
                if (cache.containsKey(t)) {
                    encodedTones.add(cache.get(t));
                    LOGGER.trace("Cache hit for {}", t);
                    continue;
                }
                EncodedTone pt = EncodedTone.encodeTone(t, c.getOnDuration());
                cache.put(t, pt);
                encodedTones.add(pt);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return encodedTones;
    }

    /**
     * Make a EncodedTone from a Tone and a duration
     *
     * @param t         the Tone to use
     * @param millisecs the duration of the Tone
     * @return          an EncodedTone
     * @throws IOException              if horrible things happen
     * @throws LineUnavailableException if even more horrible things happen
     */
    public static EncodedTone encodeTone(Tone t, Integer millisecs) throws IOException, LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioEncoder.createSample(t.getFrequency(), t.getAmplitude(), 200));
        return new EncodedTone(t, millisecs, clip);
    }


    public void stop() {
        clip.stop();
    }

    public Clip getClip() {
        return clip;
    }

    public int hashCode() {
        return tone.hashCode();
    }

    public boolean equals(Object o) {
        return (o instanceof EncodedTone && ((EncodedTone) o).tone.equals(tone) && ((EncodedTone) o).durationInMillisecs == durationInMillisecs);
    }

    public String toString() {
        return String.format("%s %d", tone.toString(), durationInMillisecs);
    }

}