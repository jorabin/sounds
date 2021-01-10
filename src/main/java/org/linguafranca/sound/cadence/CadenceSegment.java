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

package org.linguafranca.sound.cadence;

import org.linguafranca.sound.tone.Tone;

/**
 * A class representing a period of sound followed by a period of silence.
 * <p>
 * The sounds are provided as a {@link Tone.List}
 * to be played at the same time and the timings are in millis.
 */
public class CadenceSegment {

    /** the list of tones in this CadenceSegment */
    private final Tone.List tones;

    /** The duration of tones in milli secs*/
    private final int onDuration;
    /** The duration of silence in milli secs */
    private final int offDuration;

    /**
     * Get the tones in this CadenceSegment
     * @return  a pointer to the tones
     */
    public Tone.List getTones() {
        return tones;
    }

    /**
     * Set the tones in this CadenceSegment
     */
    public void setTones(Tone.List list) {
        tones.addAll(list);
    }

    /**
     * How long the sound is on 
     * @return  the duration in millis
     */
    public int getOnDuration() {
        return onDuration;
    }

    /**
     * How long the silence is 
     * @return  the duration in millis
     */
    public int getOffDuration() {
        return offDuration;
    }

    /**
     * Make a new cadence with the specified tones
     * @param onDuration    the duration of the sound in millis
     * @param offDuration   the duration of the silence in millis
     */
    public CadenceSegment(int onDuration, int offDuration) {
        this.tones = new Tone.List();
        this.onDuration = onDuration;
        this.offDuration = offDuration;
    }

    /**
     * Make a new cadence with the specified tones
     * @param tones        the tones to use
     * @param onDuration    the duration of the sound in millis
     * @param offDuration   the duration of the silence in millis
     */
    public CadenceSegment(Tone.List tones, int onDuration, int offDuration) {
        this.tones = tones;
        this.onDuration = onDuration;
        this.offDuration = offDuration;
    }
    /** 
     * Get the overall duration of the CadenceSegment (sound on plus sound off)
     * @return the duration in millis
     */
    public long getDuration() {
        return onDuration + offDuration;
    }

}

