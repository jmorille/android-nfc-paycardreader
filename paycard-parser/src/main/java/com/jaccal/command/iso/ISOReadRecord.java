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
package com.jaccal.command.iso;

import com.jaccal.CardException;
import com.jaccal.util.NumUtil;

/**
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class ISOReadRecord extends ISOCommand{

    /**
     * Construct a ISO APDU command that reads the record number recordNumber<br> 
     * from the current selected file.
     * @param recordNumber Hexadecimal value of the record number. Ex:"0C"
     * @param le Hexadecimal value of the expected length. Ex:"AB"
     * @throws CardException
     */
    public ISOReadRecord(String recordNumber,String le) throws CardException {
        super(ISO7816.CLA_ISO7816,ISO7816.INS_READ_REC);
 
        if(recordNumber == null)
            throw new CardException("(ReadRecord) recordNumber is null");
        if(le == null)
            throw new CardException("(ReadRecord) le is null");
        if(recordNumber.length() > 2)
            throw new CardException("(ReadRecord) recordNumber is more than 1 byte long");
        if(le.length() > 2)
            throw new CardException("(ReadRecord) le is more than 1 byte long");
       
    	byte [] b;
        
        if(recordNumber.length() == 0) {
            setP1((byte) 0);
        }else{
            b = NumUtil.toStringHex(recordNumber);
            setP1(b[0]);
        }

        if(le.length() == 0) {
            setLc((byte) 0);
        } else{
            b = NumUtil.toStringHex(le);
            setLc(b[0]);
        }
    }
    
    /**
     * Construct a ISO APDU command that reads the record number recordNumber<br> 
     * in the file specified by fileSFI.
     * @param recordNumber Hexadecimal value of the record number. Ex:"0C"
     * @param fileSFI Hexadecimal value of the file SFI. Ex: "08"
     * @param le Hexadecimal value of the expected length. Ex:"AB"
     * @throws CardException
     */
    public ISOReadRecord(String fileSFI, String recordNumber, String le) throws CardException {
        super(ISO7816.CLA_ISO7816,ISO7816.INS_READ_REC);
 
        if(recordNumber == null)
            throw new CardException("(ReadRecord) recordNumber is null");
        if(le == null)
            throw new CardException("(ReadRecord) le is null");
        if(fileSFI == null)
            throw new CardException("(ReadRecord) fileSFI is null");
        if(recordNumber.length() > 2)
            throw new CardException("(ReadRecord) recordNumber is more than 1 byte long");
        if(fileSFI.length() > 2)
            throw new CardException("(ReadRecord) fileSFI is more than 1 byte long");
        if(le.length() > 2)
            throw new CardException("(ReadRecord) le is more than 1 byte long");
        
    	byte [] b;
    	byte c;
        
        if(recordNumber.length() == 0) {
            setP1((byte) 0);
        } else{
            b = NumUtil.toStringHex(recordNumber);
            setP1(b[0]);
        }

        if(fileSFI.length() == 0) {
            setP2((byte) 0);
        }else{
            b = NumUtil.toStringHex(fileSFI);
            if(NumUtil.getUnsignedValue(b[0]) >= (byte)0x1F)
                throw new CardException("(ReadRecord) fileSFI is invalid");
            c = (byte)((b[0] & 0x00FF) << 3);
            setP2(c);
        }
        
        if(le.length() == 0) {
            setLc((byte) 0);
        }else{
            b = NumUtil.toStringHex(le);
            setLc(b[0]);
        }
    }    

}
