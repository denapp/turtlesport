package fr.turtlesport.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
public final class IOUtil {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtil() {
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static long copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * @param input
     * @return
     * @throws IOException
     */
    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    public static List readLines(InputStream input, String encoding) throws IOException {
        if (encoding == null) {
            return readLines(input);
        } else {
            InputStreamReader reader = new InputStreamReader(input, encoding);
            return readLines(reader);
        }
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

}

 

