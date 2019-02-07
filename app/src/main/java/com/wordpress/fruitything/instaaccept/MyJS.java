package com.wordpress.fruitything.instaaccept;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

class MyJS {
    String javascript = "";
    MyJS()
    {
        StringBuffer temp = new StringBuffer();
        try {
            URL xyz = new URL("https://instadh.000webhostapp.com/myjs.txt");
            HttpURLConnection connection = (HttpURLConnection) xyz.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                temp.append(inputLine);
            in.close();
        }catch (Exception e)
        {
            Log.e("Attention",""+e.getMessage());
        }
        javascript = temp.toString();
    }
    String getJS()
    {
        return javascript;
    }
    static String giveJS(String noOfRequest) {
        String abc2 = "var button = document.getElementsByClassName(\"_0mzm- sqdOP  L3NKy       \");\n" +
                "var noOfRequest = "+noOfRequest+";\n" +
                "noOfRequest = noOfRequest * 2;\n" +
                "if(button.length < noOfRequest)\n" +
                "{\n" +
                "\tnoOfRequest = button.length;\n" +
                "}\n" +
                "var count = 0;\n" +
                "for(var i = 0; i < noOfRequest; i++)\n" +
                "{\n" +
                "\tif(button[i].innerHTML == \"Approve\")\n" +
                "\t{\n" +
                "\t\tbutton[i].click();\n" +
                "\t\tcount++;\n" +
                "\t}\n" +
                "\ti++;\n" +
                "}\n" +
                "alert(count+\" requests were accepted\");";
        String urlString = "https://instadh.000webhostapp.com";
        String js = abc2;
        try
        {
            URL url = new URL(urlString+"/myjs1.txt");
            InputStream inputStream = url.openStream();
            DataInputStream dis = new DataInputStream(inputStream);
            String s1 = dis.readUTF();
            Log.e("Dhruv DOne",s1);
            dis.close();
            inputStream.close();
            url = new URL(urlString+"/myjs2.txt");
            inputStream = url.openStream();
            dis = new DataInputStream(inputStream);
            String s2 = dis.readUTF();
            js = s1+noOfRequest+s2;

        }catch (Exception e)
        {
            Log.e("Dhruv Attention",""+e.getMessage());
        }
		return js;
    }
}
