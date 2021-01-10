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

import java.util.HashMap;

/**
 * @author jo
 */
public class Cadences {
    public static final HashMap<String, String> CALL_PROGRESS = new HashMap<String, String>() {
        {
            put("uk_dial", "350@-21,440@-19;10(*/0/1+2)");
            put("uk_ringback", "400@-20,450@-20;10(.4/.2/1+2,.4/2/1+2)");
            put("uk_busy", "400@-20;10(.375/.375/1)");
            put("uk_reorder", "400@-20;10(*/0/1)");
            put("uk_SIT", "950@-16,1400@-16,1800@-16;5(.33/0/1,.33/0/2,.33/0/3, 0/1/0)");
    
            put("us_dial", "350@-13,440@-13;10(*/0/1+2)");
            put("us_ringback", "440@-19,480@-19;10(2/4/1+2)");
            put("us_busy", "480@-24,620@-24;10(.5/.5/1+2)");
            put("us_reorder", "480@-24,620@-24;10(.25/.25/1+2)");
            put("us_SIT", "985.2@-16,1370.6@-16,1776.7@-16;5(.380/0/1,.276/0/2,.380/0/3,0/1/0)");
        }
    };

    public static final Tone.List TRIM_PHONE_RING = new Tone.List();
    public static final Tone.List REGULAR_PHONE_RING = new Tone.List();

    static {
        TRIM_PHONE_RING.add(Tone.makeToneFA(2000.0, 0.8));
        // RINGER_TONES.add(Tone.makeToneFA(1600.0, 0.8, 5));
        //RINGER_TONES.add(Tone.makeToneFA(1500.0, 0.8, 25));
/*
        REGULAR_PHONE_RING.add(Tone.makeToneFD(800.0, -20.0));
        REGULAR_PHONE_RING.add(Tone.makeToneFD(1200.0, -20.0));
        REGULAR_PHONE_RING.add(Tone.makeToneFD(400.0, -23.0));
        REGULAR_PHONE_RING.add(Tone.makeToneFD(300.0, -23.0));
*/
        REGULAR_PHONE_RING.add(Tone.makeToneFA(2000.0, 0.25));
        REGULAR_PHONE_RING.add(Tone.makeToneFA(1500.0, 0.25));
        REGULAR_PHONE_RING.add(Tone.makeToneFA(1000.0, 0.25));
     }

    private Cadences() {}
}
