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

package org.linguafranca.sound.cadence;

import org.linguafranca.sound.tone.Tone;

import java.util.ArrayList;

/**
 * Contains a list of {@link CadenceSection}s to be played one after the other.
 */
public class CadenceScript {
    private boolean isRinger;
    private String description;
    /*
     * All the tones in the cadences contained in this script
     * this is used e.g. while parsing ToneScript as the tones are referenced by position (from 1) when
     * building the cadences
     */
    private final Tone.List tones= new Tone.List();

    private final ArrayList<CadenceSection> cadences = new ArrayList<>();

    public CadenceScript(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * A script is a ringer if it has no inherent tones - i.e. it's meant to be a telephone bell sound
     * @return true if ringer
     */
    public boolean isRinger () {
        return isRinger;
    }

    /**
     * Make this script a ringer sound
     * @param ringer true for a ringer
     */
    public void setRinger(boolean ringer) {
        isRinger = ringer;
    }

    /**
     * Return the list of tones contained in this script
     * @return a list of tones that were added to this script not necessarily the tones in the cadences in it
     */
    public Tone.List getTones() {
        return tones;
    }

    /**
     * Add a list of tones to the script, but not to any constituent
     * @param tones to be added
     */
    public void setTones(Tone.List tones) {
        this.tones.addAll(tones);
    }
    /**
     * Add a tone to the list (but not to any of the constituents)
     * @param tone the tone to add
     * @return true as specified by {@link java.util.Collection#add}
     */
    public boolean addTone(Tone tone){
        return tones.add(tone);
    }

    public void addCadence(CadenceSection cs) {
        cadences.add(cs);
    }

    public ArrayList<CadenceSection> getCadences () {
        return cadences;
    }


}
