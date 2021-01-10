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

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.round;

/**
 * A list of {@link CadenceSegment}s to be played one after the other
 * and in a loop if necessary for a specified duration.
 */

public class CadenceSection extends ArrayList<CadenceSegment> {


    private final String description;

    // the length of time to queue the list in millisecs
    private int duration;


    /**
     * Make a new list of {@link CadenceSegment}s with a desired play time
     * @param description a string to identify the CadenceSection
     * @param duration  duration of the list in millisecs - loop
     *                  if necessary if the constituent tones are not long enougg
     */
    public CadenceSection(String description, int duration) {
        this.description = description;
        setDuration(duration);
    }


    /**
     * Length of time it takes play all the {@link CadenceSegment}s
     * - i.e. the sum of the durations of each of the segments
     *
     * @return  the duration in millisecs
     */
    public int getInherentLength() {
        int accumulator = 0;
        for (CadenceSegment cadence : this) {
            // plays indefinitely so same length as the section
            if (cadence.getOnDuration() < 0) {
                checkArgument(this.size() == 1, "Indefinite tone found with other tones");
                return this.duration;
            }
            accumulator += cadence.getOnDuration() + cadence.getOffDuration();
        }
        return accumulator;
    }

    /**
     * How many times the {@link CadenceSection} must be played
     * after the initial play.
     * @return  times to queue the list of cadences
     */
    public int getRepeatCount() {
        if (getInherentLength() > duration) {
            throw new IllegalStateException(String.format("Cadence Section cannot complete within duration (%s)", description));
        }
        return round((float) duration / getInherentLength());
    }

    /**
     * The time the {@link CadenceSection} is supposed to be played for
     * @return the currently defined time in millisecs
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the time to play the {@link CadenceSection} for
     * @param duration  the duration in millisecs, negative is indefinite
     */
    public void setDuration(int duration) {
        checkArgument(duration > 0, "Cadence duration must be > 0");
        this.duration = duration;
    }

    @Override
    public String toString(){
        return description;
    }


}
