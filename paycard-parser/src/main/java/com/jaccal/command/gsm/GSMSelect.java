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
import com.jaccal.command.iso.ISOSelect;

/**
 * 
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class GSMSelect extends ISOSelect {

    /**
     * Construct a GSM apdu command that selects the file or application specified<br>
     * by fileId
     * @param selectType
     * @param fileId
     * @throws CardException
     */
    public GSMSelect(int selectType, String fileId) throws CardException{
        super(selectType, fileId);
        setCla(GSM11_11.CLA_GSM11_11);  
    }
  
    /**
     * Construct a GSM apdu command that selects the file specified by fileId
     * @param fileId
     * @throws CardException
     */
    public GSMSelect(String fileId) throws CardException{
        super(ISOSelect.SELECT_FID, fileId);
        super.setCla(GSM11_11.CLA_GSM11_11);  
    }
 
    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */
    public void setCla(byte cla){
    }  
  
 }
