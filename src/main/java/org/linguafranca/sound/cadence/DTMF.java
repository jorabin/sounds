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
 * The class represents the tones corresponding to DTMF or Q.23.
 * <p>
 * Each DTMF digit is the two sounds from a table of rows and cols looking like a standard
 * telephone keypad, with an extra column (A, B, C, D) not used in domestic telephony
 * <p>
 * Documentation:
 * http://nemesis.lonestar.org/reference/telecom/signaling/dtmf.html
 */
public class DTMF {
    public static final double[] ROW_FREQUENCIES = {697, 770, 852, 941};
    public static final double[] COL_FREQUENCIES = {1209, 1336, 1477, 1633};

    public static double COL_DIGIT_dB = -13.1;
    public static double ROW_DIGIT_dB = -14.1;

    public static int ON_DURATION = 150;
    public static int OFF_DURATION = 60;

    public enum Digit {
        _1(0),
        _2(1),
        _3(2),
        _A(3),
        _4(4),
        _5(5),
        _6(6),
        _B(7),
        _7(8),
        _8(9),
        _9(10),
        _C(11),
        _STAR(12),
        _0(13),
        _HASH(14),
        _D(15);

        private final int value;
        private final CadenceSection cadences = new CadenceSection(this.name(), OFF_DURATION + ON_DURATION);

        Digit(int value) {
            this.value = value;
            Tone.List toneList = new Tone.List();

            toneList.add(Tone.makeToneFD(ROW_FREQUENCIES[value / 4], ROW_DIGIT_dB));
            toneList.add(Tone.makeToneFD(COL_FREQUENCIES[value % 4], COL_DIGIT_dB));
            cadences.add(new CadenceSegment(toneList, ON_DURATION, OFF_DURATION));

        }

        public int getValue() {
            return value;
        }

        public CadenceSection getCadences() {
            return cadences;
        }

        public static Digit valueOf(int row, int col) {
            return Digit.values()[(row * 4 + col)];
        }

        public static Digit valueOf(int value) {
            return Digit.values()[value];
        }

        public static Digit valueOf(Character c) {
            switch (c) {
                case '0':
                    return (_0);
                case '1':
                    return (_1);
                case '2':
                    return (_2);
                case '3':
                    return (_3);
                case '4':
                    return (_4);
                case '5':
                    return (_5);
                case '6':
                    return (_6);
                case '7':
                    return (_7);
                case '8':
                    return (_8);
                case '9':
                    return (_9);
                case '*':
                    return (_STAR);
                case '#':
                    return (_HASH);
                case 'A':
                    return (_A);
                case 'B':
                    return (_B);
                case 'C':
                    return (_C);
                case 'D':
                    return (_D);
                default:
                    throw new RuntimeException("Invalid DTMF " + c);
            }
        }
    }
}
