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
package com.jaccal.command.gsm;

import com.jaccal.CardException;
import com.jaccal.command.iso.ISOReadBinary;

/**
 * 
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class GSMReadBinary extends ISOReadBinary {

    /**
     * Construct a GSM command that reads the data in a binary file from the offset<br>
     * specified by offset.
     * @param offset
     * @param lc
     * @throws CardException
     */
    public GSMReadBinary(String offset, String lc) throws CardException{
        super(offset,lc);
        super.setCla(GSM11_11.CLA_GSM11_11);
    }
    
    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */
    public void setCla(byte cla){
    }    
 }
