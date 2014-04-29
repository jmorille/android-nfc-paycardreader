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
package com.jaccal.command;

import com.jaccal.CardException;
/**
 * @author Thomas Tarpin-Lyonnet
 */
public class ApduCmd extends Command {

    public ApduCmd(byte cla, byte ins, byte p1, byte p2, byte lc, byte[] data, byte le){
        super(cla, ins, p1, p2, lc, data, le);
    }
    
	/**
	 * @param cmdStr
	 */
	public ApduCmd(String cmdStr) throws CardException{
		super(cmdStr);
	}
	
	/**
	 * @param cmdHeader
	 * @param cmdDataField
	 */
	public ApduCmd(String cmdHeader, String cmdDataField) throws CardException{
		super(cmdHeader, cmdDataField);
	}
	
    public void setCla(byte cla) {
        this.cla = cla;
    }

    public void setIns(byte ins) {
        this.ins = ins;
    }  

 }
