/*******************************************************************************
 * Copyright (c) 2014 - 2016 Andre Bossert.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Bossert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package de.anbos.eclipse.easyshell.plugin.types;

import java.util.ArrayList;
import java.util.List;

public enum CommandTokenizer {
    commandTokenizerUnknown(-1, "Unknown"),
	commandTokenizerDisabled(0, "Disabled"),
	commandTokenizerSpaces(1, "Split spaces only"),
	commandTokenizerSpacesAndQuotes(2, "Split spaces and quotes");
    // attributes
    private final int id;
    private final String name;
    // construct
    CommandTokenizer(int id, String mode) {
        this.id = id;
        this.name = mode;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public static CommandTokenizer getFromId(int id) {
    	CommandTokenizer ret = commandTokenizerUnknown;
        for(int i = 0; i < CommandTokenizer.values().length; i++) {
            if (CommandTokenizer.values()[i].getId() == id) {
                ret = CommandTokenizer.values()[i];
                break;
            }
        }
        return ret;
    }
    public static CommandTokenizer getFromName(String name) {
        CommandTokenizer ret = commandTokenizerUnknown;
        for(int i = 0; i < CommandTokenizer.values().length; i++) {
            if (CommandTokenizer.values()[i].getName().equals(name)) {
                ret = CommandTokenizer.values()[i];
                break;
            }
        }
        return ret;
    }
    public static CommandTokenizer getFromEnum(String name) {
        return CommandTokenizer.valueOf(name);
    }
    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < CommandTokenizer.values().length; i++) {
            if (CommandTokenizer.values()[i] != commandTokenizerUnknown) {
                list.add(CommandTokenizer.values()[i].getName());
            }
        }
        return list;
    }
    public static String[] getNamesAsArray() {
        List<String> list = getNamesAsList();
        String[] arr = new String[list.size()];
        for (int i=0;i<list.size();i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
};
