package ru.ifmo.ctddev.sholokhov.walk;

import java.io.*;

/**
 * Created by Шолохов on 18.02.2015.
 */
public class Walk {
    public static void main(String args[]) {
        String from = "";
        String to = "";
        if (args.length >= 1) {
            from = args[0];
        }
        if (args.length >= 2) {
            to = args[1];
        }

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(from), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to), "UTF-8"));

            String s = in.readLine();
            byte[] buf = new byte[4096];
            while (s != null) {
                try (FileInputStream fileIn = new FileInputStream(s)) {
                    int hval = 0x811c9dc5;
                    int bufLen;
                    while ((bufLen = fileIn.read(buf)) != -1) {
                        for (int i = 0; i < bufLen; i++) {
                            hval *= 0x01000193;
                            hval ^= buf[i] & 0xff;
                        }
                    }
                    String hashString = String.format("%08x", hval);
                    out.write(hashString + " " + s + '\n');
                } catch (IOException e) {
                    String hashString = String.format("%08x", 0);
                    out.write(hashString + " " + s + '\n');
                }
                s = in.readLine();
            }

        } catch (IOException e) {
            //Do nothing
        } finally {
            try {
                in.close();
                out.close();
            } catch (NullPointerException e) {
                //Do nothing
            } catch (IOException e) {
                //Do nothing
            }

        }
    }
}
