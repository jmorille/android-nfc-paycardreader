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
import com.jaccal.command.iso.ISOReadRecord;

/**
 * 
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class GSMReadRecord extends ISOReadRecord {
    public final static byte NEXT_REC = (byte)2;
    public final static byte PREV_REC = (byte)3;
    public final static byte CURRENT_REC = (byte)4;

    /**
     * Construct a GSM command that reads a record file.
     * <ul>
     * <li>NEXT_REC: reads the next record (recordNumber is not significant)</li>
     * <li>PREV_REC: reads the previous record (recordNumber is not significant)</li>
     * <li>CURRENR_REC: reads the current record or the one specified by recordNumber</li>
     * </ul>
     * @param recordNumber
     * @param lc
     * @param mode
     * @throws CardException
     */
    public GSMReadRecord(String recordNumber, String lc, byte mode) throws CardException {
        super(recordNumber, lc);
        if((mode != NEXT_REC) && (mode != PREV_REC) && (mode != CURRENT_REC))
            throw new CardException("(ReadRecord) mode is invalid. Authorised values are: NEXT_REC, PREV_REC or CURRENT_REC");
        
        super.setCla(GSM11_11.CLA_GSM11_11);
      	
        setP2(mode);     
    }
  
    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */
    public void setCla(byte cla){
    }
 }
