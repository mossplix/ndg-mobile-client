/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.ndg.lwuit.control;

import br.org.indt.ndg.mobile.AppMIDlet;
import br.org.indt.ndg.mobile.FileSystem;
import br.org.indt.ndg.mobile.XmlResultFile;
import br.org.indt.ndg.lwuit.model.Result;
import java.util.Vector;

/**
 *
 * @author Alexandre Martini
 */
public class ResultControl {
    private static ResultControl instance = new ResultControl();
    private FileSystem fs;
    private Vector xmlResultFile;

    private ResultControl(){}

    public static ResultControl getInstance() {
        return instance;
    }

    public Result[] getSentResults() {
        fs = AppMIDlet.getInstance().getFileSystem();
        xmlResultFile = fs.getXmlSentFile();

        Result r;
        XmlResultFile xmlResultFileObj;
        int totalResultsFromNDG = xmlResultFile.size();
        Result[] results = new Result[totalResultsFromNDG];
        for(int i = 0; i < totalResultsFromNDG; i++){
            xmlResultFileObj = (XmlResultFile) xmlResultFile.elementAt(i);
            r = new Result(xmlResultFileObj.getDisplayName());
            r.setPhisicallyFileName( xmlResultFileObj.getFileName());
            results[i] = r;
        }
        return results;
    }
}
