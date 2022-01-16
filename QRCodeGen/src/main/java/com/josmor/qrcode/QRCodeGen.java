package com.josmor.qrcode;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;  
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;  
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;  

public class QRCodeGen {

    //static function to create a QR Code and save it in a local image file
    public String generateQRcode(String data, String path, int h, int w) 
    		throws WriterException, IOException {  

        //Encoding charset to be used  
        String charset = "UTF-8";  

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();  
        
        //generates QR code with Low level(L) error correction capability  
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  
    	
    	//the BitMatrix class represents the 2D matrix of bits  
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);  
        
        path += "\\" + UUID.randomUUID().toString() + ".png";
        
        FileOutputStream f = new FileOutputStream(path);
        MatrixToImageWriter.writeToStream(matrix, "png", f);
        f.flush();
        f.close();  
        
        return path;
    }  
    
    //Method to decode QR Code passed in as an image file
    public String readQRCode(String filePath)
    		throws FileNotFoundException, IOException, NotFoundException, ReaderException, FormatException {
		
    	Map<DecodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<DecodeHintType, ErrorCorrectionLevel>();
    	hintMap.put(DecodeHintType.TRY_HARDER,ErrorCorrectionLevel.L);
    	
    	FileInputStream f = new FileInputStream(filePath);
    	    	
    	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(f.readAllBytes());
    	
    	f.close();
    	BufferedImageLuminanceSource s = new BufferedImageLuminanceSource(
    			ImageIO.read(byteArrayInputStream)); 
    	
    	BinaryBitmap binaryBitmap = new BinaryBitmap(
    				new HybridBinarizer(s));
    	
		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);

		return qrCodeResult.getText();
    }
    
    private static boolean contains(String[] arr, String val) {
    	boolean res = false;
    	for(String item : arr) {
    		if(item.equalsIgnoreCase(val)) {
    			res = true;
    			break;
    		}
    	}
		return res;
    	
    }

    public static void main(String[] args) {
        //data that we want to store in the QR code  
        String str= "Name: Some random name";
        String [] runTypes = new String [] {"code", "decode"};
        String runType = "";
        String qrSize = "";
        int qrSizeInt = 380; //default images size for qr code
        
        //path where we want to get QR Code  
        String path = "";  
        
        //parse the input arguments
    	if(args.length > 0) {
    		for( String arg : args) {
    			if(arg.startsWith("-path=")) {
    				path = arg.replace("-path=", "");
    			}
    			else if(arg.startsWith("-run=")) {
    				runType = arg.replace("-run=", "");
    				if(!contains(runTypes, runType))
    				{
    					throw new IllegalArgumentException(runType + " is not a valid <run> argument");
    				}
    			}
    			else if (arg.startsWith("-size=")) {
    				qrSize = arg.replace("-size=", "");
    				try {
    					qrSizeInt = Integer.parseInt(qrSize);
    					if (qrSizeInt < 115 || qrSizeInt > 1150) {
        					throw new IllegalArgumentException(qrSize + " is not an appropriate QR code image size");
        				}
    				}
    				catch (NumberFormatException numErr) {
    					System.out.println(qrSize + "is not a valid <size> argument");
    				}
    			}
    		}
    	}
        
    	//run the requested command: code or decode
        try {
        	
        	if(runType.equalsIgnoreCase("code")) {
        		QRCodeGen t = new QRCodeGen();
            	
        		String res = t.generateQRcode(str, path, qrSizeInt, qrSizeInt);

            	System.out.println("QR Code created successfully: " + res);  
        	}
        	else if(runType.equalsIgnoreCase("decode")) {
    			
        		QRCodeGen t = new QRCodeGen();

        		String res = t.readQRCode(path);
    			
    			System.out.println("QR Code decoded text: " + res);
        	}else {
    			System.out.println("Nothing to do!!!\r\n Please think of a run command [\"code\" or \"decode\"] and provide  file path for execution.");
        	}
			
		} catch (WriterException e) {
			System.out.println("QR Code failed: " + e.getMessage()); 
			e.printStackTrace();
		} catch (NotFoundException e) {
			System.out.println("QR Code failed: " + e.getMessage()); 
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("QR Code failed: " + e.getMessage()); 
			e.printStackTrace();
		}   
    }

}
