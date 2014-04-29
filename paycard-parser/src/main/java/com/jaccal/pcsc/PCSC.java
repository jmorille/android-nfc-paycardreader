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
package com.jaccal.pcsc;

/**
 * @author Thomas Tarpin-Lyonnet
 *
 */
public abstract class PCSC {
	
	public static final int SCARD_SCOPE_SYSTEM = 2;
	
	/**
	 * This application is not willing to share this card with other applications.
	 */
	public static final int SCARD_SHARE_EXCLUSIVE = 1; 
	
	/**
	 * This application is willing to share this card with other applications.
	 */
	public static final int SCARD_SHARE_SHARED = 2;
	
	/**
	 * This application demands direct control of the reader, so it is not available to other
     * applications.
	 */
	public static final int SCARD_SHARE_DIRECT = 3; 

	/**
	 * Don't do anything special on close
	 */
	public static final int SCARD_LEAVE_CARD = 0; 
	
	/**
	 * Reset the card on close
	 */
	public static final int SCARD_RESET_CARD = 1; 
	
	/**
	 * Power down the card on close
	 */
	public static final int SCARD_UNPOWER_CARD = 2; 
	
	/**
	 * Eject the card on close
	 */
	public static final int SCARD_EJECT_CARD = 3; 
	
	/**
	 * There is no active protocol
	 */
	public static final long SCARD_PROTOCOL_UNDEFINED = 0;
	
	/**
	 * T=0 is the active protocol
	 */
	public static final long SCARD_PROTOCOL_T0 = 1;
	
	/**
	 * T=1 is the active protocol
	 */
	public static final long SCARD_PROTOCOL_T1 = 2;
	
	/**
	 * Raw is the active protocol
	 */
	public static final long SCARD_PROTOCOL_RAW = 0x00010000;
	
	/**
	 * Use implicit PTS. Choose default protocol of the card
	 */
	public static final long SCARD_PROTOCOL_DEFAULT = 0x80000000;	
	

}
