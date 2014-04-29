/*
 * Copyright (c) 2005 Chang Sau Sheong, Thomas Tarpin-Lyonnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaccal;

import com.jaccal.util.NumUtil;

/**
 * Models the Answer-To-Reset reply from the card
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class Atr {
    private byte[] cardAtr;
    

    /**
     * @return Returns the cardAtr.
     */
    public byte[] getCardAtr() {
        return cardAtr;
    }
    
    /**
     * @param cardAtr The cardAtr to set.
     */
    public void setCardAtr(byte[] cardAtr) {
        this.cardAtr = cardAtr;
    }
        
    /**
     * Displays the String of the ATR
     */
    public String toString() {
        String s = new String();
        
        s = "[ATR] " + NumUtil.toHexString(cardAtr) + "\n";
        
        return s;
    }
}
