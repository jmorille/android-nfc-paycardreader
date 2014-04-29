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

/**
 * GSM11_11 encapsulates constants related to GSM SIM cards.
 * Contains only static fields.
 */
public interface GSM11_11 extends com.jaccal.command.iso.ISO7816{

	public static final byte CLA_GSM11_11 = (byte)0xA0;
/**
 * DF Telecom FID
 */ 
	public static final String DF_TELECOM_FID = "7F10";
	
/**
 * EF ADN FID
 */ 
	public static final String EF_ADN_FID = "6F3A";
	
/**
 * EF SMS FID
 */ 
	public static final String EF_SMS_FID = "6F3C";
}