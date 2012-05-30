/** 
 * Copyright (C) 2011 Tinfoilhat
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.tinfoil.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends Activity {
	Button sendSMS;
	//EditText phoneBox;
	AutoCompleteTextView phoneBox;
    EditText messageBox;
    private ArrayList<TrustedContact> tc;
    private TrustedContact newCont;
        
    //Change the password here or give a user possibility to change it
    //private static final byte[] PASSWORD = new byte[]{ 0x20, 0x32, 0x34, 0x47, (byte) 0x84, 0x33, 0x58 };
    //private static final String PASSWORD = "test123";

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_message);
        
        Prephase2Activity.dba = new DBAccessor(this);
        
        Prephase2Activity.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        newCont = new TrustedContact(null);
        tc = Prephase2Activity.dba.getAllRows();
        
        phoneBox = (AutoCompleteTextView) findViewById(R.id.new_message_number);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
	    		R.layout.auto_complete_list_item, ContactRetriever.contactDisplayMaker(tc));
	    phoneBox.setAdapter(adapter);
	    
        sendSMS = (Button) findViewById(R.id.new_message_send);
        //phoneBox = (EditText) findViewById(R.id.new_message_number);
        messageBox = (EditText) findViewById(R.id.new_message_message);
        
        /*String s = "Billy, 12345676432";
        String[] ss = s.split(", ");
        System.out.println(ss[0] + "X" + ss[1]);*/

        phoneBox.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            	String []info = s.toString().split(", ");
            	//phoneBox.setText(info[0]);
            	
            	if (!info[0].equalsIgnoreCase(s.toString()))
            	{
            		newCont.setName(info[0]);
            		newCont.setNumber(0, info[1]);
            	}
            	else
            	{
            		//Warning this could be a word, there is nothing protected it from them
            		//entering a name that is not in the database. (message will not send though)
            		if (newCont.isNumbersEmpty())
            		{
            			newCont.addNumber(info[0]);
            		}
            		else
            		{
            			newCont.setNumber(0, info[0]);
            		}
            	}
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	
            }
        });
        
        sendSMS.setOnClickListener(new View.OnClickListener()
        {
			public void onClick(View v) 
			{
				final String number = newCont.getNumber(0);
						
		        //final String number = phoneBox.getText().toString();
				
				String text = messageBox.getText().toString();
				
				if (number.length() > 0 && text.length() > 0)
				{
					//Toast.makeText(getBaseContext(), "Message sending", Toast.LENGTH_SHORT).show();

					//Encrypt the text message before sending it	
					try
					{
                    	//Only expects encrypted messages from trusted contacts in the secure state
						if (Prephase2Activity.dba.isTrustedContact(number) && 
								Prephase2Activity.sharedPrefs.getBoolean("enable", true))
						{
							sendSMS(number, Encryption.aes_encrypt(Prephase2Activity.dba.getRow
									(ContactRetriever.format(number)).getPublicKey(), text));
							Toast.makeText(getBaseContext(), "Encrypted Message sent", Toast.LENGTH_SHORT).show();
						}
						else
						{
							sendSMS(number, text);
							Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_SHORT).show();
						}
						if (Prephase2Activity.dba.getRow(ContactRetriever.format(number)) == null)
						{
							
							
							//Toast.makeText(getBaseContext(), newNumber, Toast.LENGTH_SHORT).show();
							AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
							builder.setMessage("Would you like to add " + number + " to your contacts list?")
							       .setCancelable(false)
							       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							           public void onClick(DialogInterface dialog, int id) {
							        	   //AddContact.editTc = new TrustedContact("", number, null , 0);
							        	   		AddContact.editTc = new TrustedContact("");
							        	   		AddContact.editTc.addNumber(number);
							        	   		AddContact.addContact = false;
							        		   SendMessageActivity.this.startActivity(new Intent(
							        				   SendMessageActivity.this, AddContact.class));
							        	   finish();
							           }})
							       .setNegativeButton("No", new DialogInterface.OnClickListener() {
							               public void onClick(DialogInterface dialog, int id) {
							                   dialog.cancel();
							              }
							          });
							AlertDialog alert = builder.create();
							alert.show();
						}
						messageBox.setText("");
						phoneBox.setText("");
				    }
			        catch ( Exception e ) 
			        { 
			        	Toast.makeText(getBaseContext(), "FAILED TO SEND", Toast.LENGTH_LONG).show();
			        	e.printStackTrace(); 
			    	}
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
					builder.setMessage("You have failed to provide sufficient information")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {}});
					AlertDialog alert = builder.create();
					alert.show();
				}
				
			}
        });
    }
    
    /*public boolean onCreateOptionsMenu(Menu menu) {
    	 
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.texting_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case R.id.add:
	        startActivity(new Intent(this, AddContact.class));
	        return true;
	        default:
	        return super.onOptionsItemSelected(item);
    	}
    }*/
      
    /**
     * Sends the given message to the phone with the given number
     * @param number : String, the number of the phone that the message is sent to
     * @param message : String, the message, encrypted that will be sent to the contact
     */
    public void sendSMS (String number, String message)
    {
    	PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Object.class), 0);
        SmsManager sms = SmsManager.getDefault();
        
        //this is the function that does all the magic
        sms.sendTextMessage(number, null, message, pi, null);
    	
    }
}
