package fr.turtlesport.device;

import java.io.File;

/**
 * Copyright (c) 2008-2016, Turtle Sport
 * <p/>
 * This file is part of Turtle Sport.
 * <p/>
 * Turtle Sport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * <p/>
 * Turtle Sport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Turtle Sport.  If not, see <http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>.
 */
public class FileDevice implements IProductDevice {

    private IProductDevice productDevice;

    private File file;

    public FileDevice(File file) {
        this.file = file;
        this.productDevice = NullProductDevice.INSTANCE;
    }

    public FileDevice(File file, IProductDevice productDevice) {
        this.file = file;
        this.productDevice = productDevice;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String displayName() {
        return productDevice.displayName();
    }

    @Override
    public String id() {
        return productDevice.id();
    }

    @Override
    public String softwareVersion() {
        return productDevice.softwareVersion();
    }
}

 

