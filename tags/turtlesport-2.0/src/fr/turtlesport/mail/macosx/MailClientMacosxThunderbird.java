package fr.turtlesport.mail.macosx;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.AbstractMailClientThunderbird;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.util.Exec;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) 2008-2016, Turtle Sport
 * <p>
 * This file is part of Turtle Sport.
 * <p>
 * Turtle Sport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * <p>
 * Turtle Sport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Turtle Sport.  If not, see <http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>.
 */
public class MailClientMacosxThunderbird extends AbstractMailClientThunderbird {
    private static TurtleLogger log;

    static {
        log = (TurtleLogger) TurtleLogger
                .getLogger(MailClientMacosxThunderbird.class);
    }

    private String location;

    protected MailClientMacosxThunderbird() {
        location = location();
        if (location == null) {
            throw new IllegalAccessError();
        }
    }

    /**
     * D&eacute;termine si le client mail est valable.
     *
     * @return <code>true</code> si le client mail est valable,
     * <code>false</code> sinon.
     */
    protected static boolean isAvailable() {
        return location() != null;
    }


    @Override
    public String getLocation() {
        return location;
    }

    /*
   * (non-Javadoc)
   *
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#isRunning()
   */
    @Override
    public boolean isRunning() {

/*
        try {
            Process p1 = Runtime.getRuntime().exec(new String[]{"ps", "-ef"});
            InputStream input = p1.getInputStream();
            Process p2 = Runtime.getRuntime().exec(new String[]{"grep", "thunderbird"});
            OutputStream output = p2.getOutputStream();
            IOUtil.copy(input, output);
            output.close(); // signals grep to finish
            List<String> result = IOUtil.readLines(p2.getInputStream());
            System.out.println(result);

        } catch (IOException e) {
            log.error("", e);
            return false;
        }
        */
        return true;
    }

    /*
   * (non-Javadoc)
   *
   * @see fr.turtlesport.mail.MailClient#mail(fr.turtlesport.mail.Message)
   */
    public void mail(MessageMail message) throws IOException {
        log.debug(">>mail message");

        String[] cmdarray = new String[3];
        cmdarray[0] = getLocation();

        if (isRunning()) {
            cmdarray[1] = "--new-instance ";
            cmdarray[2] = constructMailto(message.getToAddrs(),
                    message.getSubject(),
                    message.getBody(),
                    message.getAttachments());
        }
        else {
            cmdarray[1] = "-compose";
            cmdarray[2] = constructMailto(message.getToAddrs(),
                    message.getSubject(),
                    message.getBody(),
                    message.getAttachments());
        }

        if (log.isDebugEnabled()) {
            log.debug("cmdarray[0]=" + cmdarray[0]);
            log.debug("cmdarray[1]=" + cmdarray[1]);
            log.debug("cmdarray[2]=" + cmdarray[2]);
        }

        Exec.exec(cmdarray);

        log.debug("<<mail");
    }

    private static String location() {
        final String path = "/Applications/Thunderbird.app/Contents/MacOS/thunderbird";
        File f = new File(path);
        if (f.isFile()) {
            return f.getPath();
        }
        return null;
    }


}

 

