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

/**
 * Created by IntelliJ IDEA.
 * User: Jo
 * Date: 26/12/2011
 * Time: 16:00
 *
 */

import org.linguafranca.sound.cadence.DTMF;
import org.linguafranca.sound.cadence.CadenceSection;


public interface CadencePlayer {

    public void openSound() throws Exception;

    public QueueItem<CadenceSection> queue(CadenceSection section, boolean block);

    public void queue(QueueItem<CadenceSection> cadences, boolean block);

    public boolean stopSound(int timeToWaitMillis);

    public boolean closeSound(int timeToWaitMillis);

    public void dial(DTMF.Digit d);

    public void dial(Character c);

    public void dial(String dialString);


    /**
     * An abstract base class implementation of the {@link CadencePlayer} interface providing concrete instances of the dial methods.
     */
    public abstract class Skeleton implements CadencePlayer{

        /**
         * Plays the digit requested
         * @param d the digit to queue
         */
        @Override
        public void dial(DTMF.Digit d) {
            queue(new QueueItem<>(d.getCadences()), false);
        }

        /**
         * Plays the digit corresponding to the character supplied.
         * @param c  The character for the digit
         * @throws RuntimeException if there is no digit for the character
         */
        @Override
        public void dial(Character c){
            dial(DTMF.Digit.valueOf(c));
        }

        /**
         * Plays the string interpreted as {@link DTMF.Digit}s (ignoring space)
         * @param dialString  The string to dial
         * @throws RuntimeException if the string is not valid
         */
        @Override
        public void dial(String dialString) {
            for (int i=0; i<dialString.length(); i++){
                if (dialString.charAt(i)!=' ')
                    dial(dialString.charAt(i));
            }
        }

    }
}
