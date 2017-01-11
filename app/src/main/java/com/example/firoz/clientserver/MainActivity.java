package com.example.firoz.clientserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    EditText etip,etmessage;
    Button send;
    Socket client;
    String message;
    String ip;
    int port=4444;

    //server

    TextView tv;
    String msgserver;
    ServerSocket serverSocket=null;
    Socket clientSocket=null;
    //end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etip=(EditText)findViewById(R.id.editText);
        etmessage=(EditText)findViewById(R.id.editText3);
        send=(Button)findViewById(R.id.button);


        tv=(TextView)findViewById(R.id.textView);
            new Thread(new Runnable() {
                @Override
            public void run() {
                    try {
                        serverSocket=new ServerSocket(4444);

                        while(true) {
                            clientSocket = serverSocket.accept();

                            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                            msgserver = (String) dis.readUTF();
                            dataReceived();
                            clientSocket.close();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


            }
        }).start();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=etmessage.getText().toString();
                ip=etip.getText().toString();
                if(!ip.trim().equals("")||!message.trim().equals(""))
                {
                    if(!message.trim().equals(""))
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    client=new Socket(ip,port);
                                    DataOutputStream dout=new DataOutputStream(client.getOutputStream());
                                    dout.writeUTF(message);
                                    dout.flush();
                                    dout.close();

                                    dataSent();

                                    client.close();


                                } catch (Exception e)
                                {
                                    connectionFailed();
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Write Your Message",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Enter IP Address",Toast.LENGTH_SHORT).show();
                }
                etmessage.setText("");


            }
        });
    }
    //server
    public void dataReceived()
    {
            MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.append("Server: "+msgserver+"\n");
            }
        });
    }
    //end

    public void dataSent()
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.append("Me: "+message+"\n");
            }
        });
    }

    public void connectionFailed()
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Server Not Found!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
