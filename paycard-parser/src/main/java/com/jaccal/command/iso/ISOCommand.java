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
import com.jaccal.command.Command;
import com.jaccal.util.NumUtil;


/**
 * @author Thomas Tarpin-Lyonnet
 *
 */
public abstract class ISOCommand extends Command{

    public ISOCommand(byte cla, byte ins){
        super(cla, ins);
    }
 
    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */    
    public void setCla(byte cla) throws CardException{
        
        // check the value of the CLA byte to be set
        // Valid CLA byte values are:
        // 0x00, 0x04, 0x80, 0x84, 0x90, 0x94, 0xA0 or 0xA4
        if((NumUtil.getUnsignedValue(cla) != 0x00) && (NumUtil.getUnsignedValue(cla) != 0x04)
                && (NumUtil.getUnsignedValue(cla) != 0x80) && (NumUtil.getUnsignedValue(cla) != 0x84) 
                && (NumUtil.getUnsignedValue(cla) != 0x90) && (NumUtil.getUnsignedValue(cla) != 0x94)
                && (NumUtil.getUnsignedValue(cla) != 0xA0) && (NumUtil.getUnsignedValue(cla) != 0xA4))
  		    throw new CardException("(setCla) cla byte value is incorrect. cla byte value should be: 0x00, 0x04, 0x80, 0x84, 0x90, 0x94, 0xA0 or 0xA4");
            
        super.setCla(cla);
    } 
}
