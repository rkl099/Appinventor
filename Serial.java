// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2016-2019 MIT, All rights reserved
// Copyright 2017-2019 Kodular, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

/*
     201003 Remove BufferSize(), (Just updates local bufferSize!)
     210103 Add lots of functions
*/

//package com.SerialOTG;
package com.google.appinventor.components.runtime;

import android.content.Context;
import android.util.Log;

import com.google.appinventor.components.runtime.*;


import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.ErrorMessages;

import java.lang.reflect.Field; 
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


// Select library and connectivity/extension
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.Boards;
//import com.physicaloid_ai.lib.Physicaloid;
//import com.physicaloid_ai.lib.Boards;

@DesignerComponent(version = YaVersion.SERIAL_COMPONENT_VERSION,
    description = "New Serial component which can be used to connect to devices like Arduino",
    category = ComponentCategory.CONNECTIVITY,
//    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png",
    androidMinSdk = 12)
    
@SimpleObject
//@SimpleObject(external = true)

@UsesLibraries(libraries = "physicaloid.jar" )
//@UsesLibraries(libraries = "physicaloid_ai.jar" )

public class Serial extends AndroidNonvisibleComponent implements Component {
//public class SerialOTG extends AndroidNonvisibleComponent implements Component {
  private static final String LOG_TAG = "Serial Component";

  private Context context;

  private Physicaloid mPhysicaloid=null;

  // Must correspond to default in Physicaloid
  private int baudRate = 9600;
  private int bytes = 1024;
  private int parity = 0;
  private int dtr = 0;
  private int rts = 0;

  public Serial(ComponentContainer container) {  
//  public SerialOTG(ComponentContainer container) {
    super(container.$form());
    context = container.$context();
    Log.d(LOG_TAG, "Created");
  }


//***********************  Init Open, Close  ******************************************************


  @SimpleFunction(description = "Initializes serial connection.")
  public void Initialize() {
    mPhysicaloid = new Physicaloid(context);
//    BaudRate(this.baudRate);   //cant be set before open
    Log.d(LOG_TAG, "Initialized");
  }
  

  @SimpleFunction(description = "Opens serial connection. Returns true when opened.")
  public boolean Open() {
    Log.d(LOG_TAG, "Opening connection");
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Open", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return false;
    }
    if ( !mPhysicaloid.open()) return false;
//  Set local state
    mPhysicaloid.setBaudrate(this.baudRate);
    mPhysicaloid.setParity(this.parity);
    mPhysicaloid.setDtrRts(this.dtr != 0,this.rts != 0);
    return true;
  }


  @SimpleFunction(description = "Closes serial connection. Returns true when closed.")
  public boolean Close() {
    Log.d(LOG_TAG, "Closing connection");
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Close", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return false;
    }
    return mPhysicaloid.close();
  }
  
  
/**********************  Properties *****************************************************************/


  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Returns true when the Serial connection is open.")
  public boolean IsOpen() {
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "IsOpen", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return false;
    }
    return mPhysicaloid.isOpened();
  }

  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Returns true when the Serial has been initialized.")
  public boolean IsInitialized() {
    return mPhysicaloid != null;
  }

  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Baud 300,600,1200,2400,9600,19200,38400,57600,115200")
  public int BaudRate() {
    return this.baudRate;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "9600")
  @SimpleProperty
  public void BaudRate(int baudRate) {
    this.baudRate = baudRate;
    Log.d(LOG_TAG, "Baud Rate: " + baudRate);
    if (mPhysicaloid != null)
      mPhysicaloid.setBaudrate(baudRate);
    else
      Log.w(LOG_TAG, "Could not set Serial Baud Rate to " + baudRate + ". Just saved, not applied to serial! Maybe you forgot to initialize it?");
  }


// 0=NONE,  1=ODD,  2=EVEN,  (3=MARK 4=SPACE) corresponds to UartConfig.PARITY_xxx
  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Parity 0=no, 1=Odd, 2=Even")
  public int Parity() {
      return this.parity;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "0")
  @SimpleProperty
  public void Parity(int parity) {
    this.parity = parity;
    Log.d(LOG_TAG, "Parity: " + parity);
    if (mPhysicaloid != null)
      mPhysicaloid.setParity(parity);
    else
      Log.w(LOG_TAG, "Could not set parity " + parity );
  }

  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "DTR 0,1")
  public int Dtr() {
      return this.dtr;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "0")
  @SimpleProperty
  public void Dtr(int dtr) {
    this.dtr = dtr;
    Log.d(LOG_TAG, "Dtr: " + dtr);
    if (mPhysicaloid != null)
      mPhysicaloid.setDtrRts(this.dtr != 0,this.rts != 0);
    else
      Log.w(LOG_TAG, "Could not set dtr " + dtr );
  }

  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "RTS 0,1")
  public int Rts() {
      return this.rts;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "0")
  @SimpleProperty
  public void Rts(int rts) {
    this.rts = rts;
    Log.d(LOG_TAG, "Rts: " + rts);
    if (mPhysicaloid != null)
      mPhysicaloid.setDtrRts(this.dtr != 0,this.rts != 0);
    else
      Log.w(LOG_TAG, "Could not set rts " + rts );
  }



//**********************************   Read and write UTF-8 string  **********************************

  @SimpleFunction(description = "Reads string data from serial.")
  public String Read() {
    String data = "";
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Read", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    } else {
      byte[] buf = new byte[this.bytes];
      if (mPhysicaloid.read(buf) > 0) {
        try {
          data = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException mEr) {
          Log.e(LOG_TAG, mEr.getMessage());
        }
      }
    }
    return data;
  } 


  @SimpleFunction(description = "Writes string data to serial.")
  public void Write(String data) {
    if (!data.isEmpty() && mPhysicaloid != null) {
      byte[] buf = data.getBytes();
      int result = mPhysicaloid.write(buf);
      if (result == -1)
        form.dispatchErrorOccurredEvent(Serial.this, "Write", ErrorMessages.ERROR_SERIAL_WRITING);
    } else if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Write", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    }
  }


//***************************   Read write message, UTF-8 string terminated by new line

  // Line buffer
  private int lbuf_l = 256;
  private int lbuf_i = 0;
  private byte[] lbuf = new byte[lbuf_l];  


  @SimpleFunction(description = "Reads string data. Return empty string until eol found in mess")
  public String ReadLn() {
    String data = "";   
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "ReadLn", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    } else {
      byte[] ch=new byte[1];
      while (true) {
        int ret=mPhysicaloid.read(ch,1);
        if (ret>0) {
           if (ch[0]==0x0a) {  // \n (LF)
             if (lbuf_i==0) return "";
             try {
               data = new String(lbuf,0,lbuf_i, "UTF-8");
             } catch (UnsupportedEncodingException mEr) {
                Log.e(LOG_TAG, mEr.getMessage());
             }
             lbuf_i=0;
             return data;
           }
           if (lbuf_i<lbuf_l) lbuf[lbuf_i++]=ch[0];
        }
        else break;      
      }    
    }
    return ""; //Empty string
  } 



  @SimpleFunction(description = "Writes given data to serial, and appends a new line at the end.")
  public void WriteLn(String data) {
    if (!data.isEmpty())
      Write(data + "\n");
  }



/*  *****************************************************************************************

    Uses a string of hex coded data, 2 char for each byte (0..255) on serial line 
    0..9 and a..f (A..F) are valid characters, so no problem with UTF-8 encoding
    Read and write astring
*/

private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  @SimpleFunction(description = "Reads n bytes from serial, return n*2 hex char in string.")
  public String ReadHex() {

    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "ReadHex", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    } else {
      byte[] buf = new byte[this.bytes];
      int len = mPhysicaloid.read(buf);
      if ( len > 0) {
        char[] hexChars = new char[len * 2];
        for (int i = 0; i < len; i++) {
          int v = (int) (buf[i]) & 0x0FF;
          hexChars[i * 2] = (char) (HEX_ARRAY[v >>> 4]);
          hexChars[i * 2 + 1] = (char) (HEX_ARRAY[v & 0x0F]);
        }
        return new String(hexChars);
      }
    }
    return new String("");
  } 


  @SimpleFunction(description = "Writes 2*n hex char in string as n bytes to serial.")
  public void WriteHex(String string) {
    if (!string.isEmpty() && mPhysicaloid != null) {
      byte[] hex = string.getBytes(); // UTF-8 in data?
      int len = hex.length;
      if ( len % 2 == 0 ) {
        byte[] buf = new byte [len / 2];
        for (int i = 0; i < len; i += 2) {
          int high = Character.digit(hex[i], 16);
          int low = Character.digit(hex[i + 1], 16);
          buf[i / 2] = (byte) (high << 4 | low);
        }
        int result = mPhysicaloid.write(buf);
        if (result == -1)
          form.dispatchErrorOccurredEvent(Serial.this, "WriteHex", ErrorMessages.ERROR_SERIAL_WRITING);
      }  
    } else if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "WriteHex", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    }
  }
  

/*
  *****************   Read and write a number 0..255 that is sent received to serial line   *********************************
*/

  @SimpleFunction(description = "Reads 1 byte data from serial as int (0-255). If empty, return -1")
  public int ReadByte() {

    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "ReadByte", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return -1;
    } else {
      byte[] buf = new byte[this.bytes];
      if (mPhysicaloid.read(buf,1) > 0) {
        return 0x00ff & (int) buf[0];
      }
    }
    return -1;
  } 


  @SimpleFunction(description = "Writes int (0-255) as 1 byte to serial.")
  public void WriteByte (int number) {
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "WriteByte", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    }
    else {
      byte[] buf = new byte[1];
      buf[0] = (byte) (number & 0x00ff);
      int result = mPhysicaloid.write(buf);
      if (result == -1)
        form.dispatchErrorOccurredEvent(Serial.this, "WriteSerial", ErrorMessages.ERROR_SERIAL_WRITING);
    }
  }
 
 // **********************   Read and write list of bytes

/*
    Read a stream of bytes, return a list, each element containing a byte 0..255)
*/

  @SimpleFunction(description = "Read multiple unsigned byte values from serial (0..255) " +
                                "Return List, if 0 bytes then return empty List")
  public List<Integer> ReadBytes () {
    List<Integer> list = new ArrayList<Integer>();
    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "ReadBytes", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    } else {
      byte[] buf = new byte[this.bytes];
      int nrbytes = mPhysicaloid.read(buf);
      if ( nrbytes > 0) {
        for (int i = 0; i < nrbytes; i++) {
          int n = buf[i] & 0xFF;
          list.add(n);
        }
      }
    }
    return list;
  }

/*
    Write a list, each element contains a byte 0..255, send as a stream of bytes
*/    

  @SimpleFunction(description = "Write a list (int) as a stream of unsigned byte values (0..255) to serial ")
  public void WriteBytes (List<Integer> list) {

    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "WriteBytes", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
    } else { 
     Object[] array = list.toArray();
     byte[] bytes = new byte[array.length];
     for (int i = 0; i < array.length; i++) {
       Object element = array[i];
       String s = element.toString();  //Assume its a string (should be tested)
       int n = Integer.decode(s);
       bytes[i] = (byte) ((0x00ff) & n);
     }
     int result = mPhysicaloid.write(bytes,array.length);
     if (result == -1)
        form.dispatchErrorOccurredEvent(Serial.this, "WriteSerial", ErrorMessages.ERROR_SERIAL_WRITING);
    }
  }



  
//***********************  Misc functions  *****************************************  

  @SimpleFunction(description = "Nr of bytes to read in buffer")
  public int Available() {

    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Available", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return -1;
    } else {
      return mPhysicaloid.bytesInReadBuffer(); 
    }
  } 

  @SimpleFunction(description = "Flush read buffer")
  public int Flush() {

    if (mPhysicaloid == null) {
      form.dispatchErrorOccurredEvent(Serial.this, "Flush", ErrorMessages.ERROR_SERIAL_NOT_INITIALIZED);
      return -1;
    } else {
          byte[] buf = new byte[this.bytes];
          int len = mPhysicaloid.read(buf);
          lbuf_i=0; 
          return 0;
    }
  } 

//*****************  Experimental support and new functions  


// Return driver name

  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Driver name from Open")
  public String DriverName() {
    if (mPhysicaloid != null)
        return mPhysicaloid.getDriverName();
//        return mPhysicaloid.getPhysicalConnectionName();  // like USB etc
    else
        return "";    
  }

/* 
       Loading hex files to different arduiuno board 
*/

/*
  @SimpleFunction(description = "Upload hex file to aurdino " +
                               "1=UNO  " + 
                               "2=UNO MEGA2560  " + 
                               "3=Nano328  " + 
                               "4=Mini328  " + 
                               "5=Pro/Pro mini 5V 328  " + 
                               "6=Pro/Pro Mini 3.3V 328  ") 

  public void Upload(int boardType, String filepath) {
    switch (boardType) {
      case 1:
           mPhysicaloid.upload(Boards.ARDUINO_UNO, filepath);
         break;
      case 2:
           mPhysicaloid.upload(Boards.ARDUINO_MEGA_2560_ADK, filepath);
         break;
      case 3:
           mPhysicaloid.upload(Boards.ARDUINO_NANO_328, filepath);
         break;
      case 4:
           mPhysicaloid.upload(Boards.ARDUINO_MINI_328, filepath);
         break;
      case 5:
           mPhysicaloid.upload(Boards.ARDUINO_PRO_5V_328, filepath);
         break;
      case 6:
           mPhysicaloid.upload(Boards.ARDUINO_PRO_33V_328, filepath);
         break;
    }      
  }
*/ 
//**********************************  redundent functions               

/*

// Updates local BufferSize
  @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "AI internal buffer size in bytes")
  public int BufferSize() {
    return this.bytes;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER, defaultValue = "1024")
  @SimpleProperty
  public void BufferSize(int bytes) {
    this.bytes = bytes;
    Log.d(LOG_TAG, "Buffer Size: " + bytes);
  }

*/




}
